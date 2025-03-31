package com.Lvtu.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * {@link UploadController} 的单元测试类
 *
 * <p>测试图片上传控制器的各种场景，包括单文件和多文件上传</p>
 *
 * @author YourName
 * @since 2023-08-01
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("图片上传控制器测试")
class UploadControllerTest {

    private static final String TEST_IMAGES_DIR = "test-images/";
    private static final String SINGLE_UPLOAD_URL = "/upload/blog";
    private static final String MULTI_UPLOAD_URL = "/upload/blogs";

    @Autowired
    private MockMvc mockMvc;

    // =============== 测试用例 ===============

    @Test
    @DisplayName("测试上传单个JPEG图片")
    void uploadSingleJpegImage_ShouldReturnSuccess() throws Exception {
        // 准备测试数据
        File imageFile = loadTestImage("test-image.jpg");
        MockMultipartFile multipartFile = createMockMultipartFile("file", imageFile, "image/jpeg");

        // 执行并验证
        performUploadTest(SINGLE_UPLOAD_URL, multipartFile);
    }

    @Test
    @DisplayName("测试上传单个PNG图片")
    void uploadSinglePngImage_ShouldReturnSuccess() throws Exception {
        File imageFile = loadTestImage("test-image.png");
        MockMultipartFile multipartFile = createMockMultipartFile("file", imageFile, "image/png");

        performUploadTest(SINGLE_UPLOAD_URL, multipartFile);
    }

    @Test
    @DisplayName("测试上传多个不同格式图片")
    void uploadMultipleImages_ShouldReturnSuccess() throws Exception {
        MockMultipartFile jpegFile = createMockMultipartFile(
                "files",
                loadTestImage("test-image.jpg"),
                "image/jpeg"
        );

        MockMultipartFile pngFile = createMockMultipartFile(
                "files",
                loadTestImage("test-image.png"),
                "image/png"
        );

        mockMvc.perform(multipart(MULTI_UPLOAD_URL)
                        .file(jpegFile)
                        .file(pngFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    @DisplayName("测试上传空文件应返回错误")
    void uploadEmptyFile_ShouldReturnError() throws Exception {
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty.jpg",
                "image/jpeg",
                new byte[0]
        );

        mockMvc.perform(multipart(SINGLE_UPLOAD_URL)
                        .file(emptyFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    // =============== 私有工具方法 ===============

    @Autowired
    private ResourceLoader resourceLoader;

    private File loadTestImage(String filename) throws Exception {
        //TODO: 这里不知道为什么没法找到文件
        Resource resource = resourceLoader.getResource("classpath:test-images/" + filename);
        if (!resource.exists()) {
            throw new FileNotFoundException("文件不存在: " + filename);
        }
        return resource.getFile();
    }
    private MockMultipartFile createMockMultipartFile(
            String paramName,
            File file,
            String contentType) throws Exception {
        return new MockMultipartFile(
                paramName,
                file.getName(),
                contentType,
                Files.readAllBytes(file.toPath())
        );
    }

    private void performUploadTest(
            String url,
            MockMultipartFile multipartFile) throws Exception {
        mockMvc.perform(multipart(url)
                        .file(multipartFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").exists());
    }
@Test
    void debugResourceLoading() {
        String[] files = {"test-image.jpg", "test-image.jpeg", "test-image.png"};
        for (String file : files) {
            URL url = getClass().getClassLoader().getResource("test-images/" + file);
            System.out.println(file + " => " + (url != null ? "找到" : "未找到"));
        }
    }
}