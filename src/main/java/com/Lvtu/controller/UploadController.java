package com.Lvtu.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.Lvtu.dto.Result;
import com.Lvtu.utils.SystemConstants;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 文件上传控制器
 * 使用阿里云OSS作为文件存储服务
 */
@Slf4j
@RestController
@RequestMapping("upload")
public class UploadController {

    // 从配置文件中注入阿里云OSS相关配置
    @Value("${lvtu.alioss.endpoint}")
    private String endpoint;

    @Value("${lvtu.alioss.access-key-id}")
    private String accessKeyId;

    @Value("${lvtu.alioss.access-key-secret}")
    private String accessKeySecret;

    @Value("${lvtu.alioss.bucket-name}")
    private String bucketName;

    /**
     * 上传博客图片到阿里云OSS
     * @param image 上传的图片文件
     * @return 上传结果，包含文件访问路径
     */
    @PostMapping("blog")
    public Result uploadImage(@RequestParam("file") MultipartFile image) {
        // 校验文件是否为空
        if (image.isEmpty()) {
            return Result.fail("上传文件不能为空");
        }

        // 获取原始文件名并生成新的文件名
        String originalFilename = image.getOriginalFilename();
        String fileName = createNewFileName(originalFilename);

        // 创建OSS客户端
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            // 上传文件到OSS
            ossClient.putObject(new PutObjectRequest(bucketName, fileName,
                    new ByteArrayInputStream(image.getBytes())));

            // 生成文件访问URL
            String fileUrl = generateFileUrl(fileName);

            log.info("文件上传成功，文件名: {}, 访问URL: {}", fileName, fileUrl);
            return Result.ok(fileUrl);
        } catch (IOException e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            return Result.fail("文件上传失败");
        } finally {
            // 关闭OSS客户端
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }


    /**
     * 批量上传博客图片到阿里云OSS
     * @param images 上传的图片文件数组
     * @return 上传结果，包含所有文件访问路径
     */
    @PostMapping("blogs")
    public Result uploadImages(@RequestParam("files") MultipartFile[] images) {
        if (images == null || images.length == 0) {
            return Result.fail("请选择至少一个文件上传");
        }
        return doUploadImages(Arrays.asList(images));
    }

    /**
     * 执行图片上传的核心方法
     * @param files 要上传的文件列表
     * @return 上传结果
     */
    private Result doUploadImages(List<MultipartFile> files) {
        // 过滤掉空文件
        List<MultipartFile> validFiles = files.stream()
                .filter(file -> !file.isEmpty())
                .collect(Collectors.toList());

        if (CollUtil.isEmpty(validFiles)) {
            return Result.fail("上传文件不能为空");
        }

        // 创建OSS客户端
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            List<String> fileUrls = new ArrayList<>();

            for (MultipartFile file : validFiles) {
                try {
                    // 获取原始文件名并生成新的文件名
                    String originalFilename = file.getOriginalFilename();
                    String fileName = createNewFileName(originalFilename);

                    // 上传文件到OSS
                    ossClient.putObject(new PutObjectRequest(
                            bucketName,
                            fileName,
                            new ByteArrayInputStream(file.getBytes()))
                    );

                    // 生成文件访问URL并添加到结果列表
                    String fileUrl = generateFileUrl(fileName);
                    fileUrls.add(fileUrl);

                    log.debug("文件上传成功，文件名: {}, 访问URL: {}", fileName, fileUrl);
                } catch (IOException e) {
                    log.error("文件上传失败: {}", e.getMessage(), e);
                    // 单个文件上传失败不影响其他文件
                    continue;
                }
            }

            if (fileUrls.isEmpty()) {
                return Result.fail("所有文件上传失败");
            }

            return Result.ok(fileUrls.size() == 1 ? fileUrls.get(0) : fileUrls);
        } finally {
            // 关闭OSS客户端
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    /**
     * 删除阿里云OSS上的博客图片
     * @param filename 要删除的文件名
     * @return 删除结果
     */
    @GetMapping("/blog/delete")
    public Result deleteBlogImg(@RequestParam("name") String filename) {
        // 校验文件名是否合法
        if (StrUtil.isBlank(filename)) {
            return Result.fail("文件名不能为空");
        }

        // 创建OSS客户端
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            // 检查文件是否存在
            boolean exists = ossClient.doesObjectExist(bucketName, filename);
            if (!exists) {
                return Result.fail("文件不存在");
            }

            // 删除文件
            ossClient.deleteObject(bucketName, filename);
            log.info("文件删除成功，文件名: {}", filename);
            return Result.ok();
        } catch (Exception e) {
            log.error("文件删除失败: {}", e.getMessage(), e);
            return Result.fail("文件删除失败");
        } finally {
            // 关闭OSS客户端
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    /**
     * 生成新的文件名
     * @param originalFilename 原始文件名
     * @return 新生成的文件名
     */
    private String createNewFileName(String originalFilename) {
        // 获取文件后缀
        String suffix = StrUtil.subAfter(originalFilename, ".", true);
        // 使用UUID生成唯一文件名
        String name = UUID.randomUUID().toString();
        // 根据文件名hash生成两级目录结构
        int hash = name.hashCode();
        int d1 = hash & 0xF;
        int d2 = (hash >> 4) & 0xF;

        // 格式化为: blogs/一级目录/二级目录/UUID.后缀
        return StrUtil.format("blogs/{}/{}/{}.{}", d1, d2, name, suffix);
    }

    /**
     * 生成文件访问URL
     * @param fileName 文件名
     * @return 完整的文件访问URL
     */
    private String generateFileUrl(String fileName) {
        // 如果使用自定义域名，可以在这里配置
        // 否则使用阿里云OSS默认域名
        return StrUtil.format("https://{}.{}/{}", bucketName, endpoint, fileName);
    }
}