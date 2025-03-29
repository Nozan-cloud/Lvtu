package com.Lvtu.service.impl;

import com.Lvtu.dto.CommentDTO;
import com.Lvtu.dto.Result;
import com.Lvtu.dto.UserDTO;
import com.Lvtu.entity.BlogComments;
import com.Lvtu.entity.User;
import com.Lvtu.mapper.BlogCommentsMapper;
import com.Lvtu.service.IBlogCommentsService;
import com.Lvtu.service.IUserService;
import com.Lvtu.utils.UserHolder;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 博客评论服务实现类
 */
@Slf4j
@Service
public class BlogCommentsServiceImpl extends ServiceImpl<BlogCommentsMapper, BlogComments> implements IBlogCommentsService {

    @Resource
    private IUserService userService;

    @Override
    public Result addComment(CommentDTO commentDTO) {
        try {
            // 1. 参数校验
            if (commentDTO == null) {
                return Result.fail("评论数据不能为空");
            }

            // 2. 获取当前登录用户
            UserDTO currentUser = UserHolder.getUser();
            if (currentUser == null) {
                return Result.fail("用户未登录");
            }

            // 3. 校验博客ID
            if (commentDTO.getBlogId() == null || commentDTO.getBlogId() <= 0) {
                return Result.fail("博客ID不合法");
            }

            // 4. 校验评论内容
            if (StringUtils.isEmpty(commentDTO.getContent())) {
                return Result.fail("评论内容不能为空");
            }
            if (commentDTO.getContent().length() > 500) {
                return Result.fail("评论内容过长，最多500个字符");
            }

            // 5. 构建评论实体
            BlogComments comment = new BlogComments();
            comment.setUserId(currentUser.getId());
            comment.setBlogId(commentDTO.getBlogId());
            comment.setParentId(commentDTO.getParentId() != null ? commentDTO.getParentId() : 0L);
            comment.setAnswerId(commentDTO.getAnswerId());
            comment.setContent(commentDTO.getContent());
            comment.setLiked(0);
            comment.setStatus(false);
            comment.setCreateTime(LocalDateTime.now());
            comment.setUpdateTime(LocalDateTime.now());

            // 6. 保存评论
            boolean saved = save(comment);
            if (!saved) {
                return Result.fail("评论保存失败");
            }

            return Result.ok(comment.getId());

        } catch (Exception e) {
            log.error("添加评论异常", e);
            return Result.fail("系统异常，添加评论失败");
        }
    }

    @Override
    public Result deleteComment(Long commentId) {
        try {
            // 1. 参数校验
            if (commentId == null || commentId <= 0) {
                return Result.fail("评论ID不合法");
            }

            // 2. 获取当前用户
            UserDTO currentUser = UserHolder.getUser();
            if (currentUser == null) {
                return Result.fail("用户未登录");
            }

            // 3. 查询评论是否存在
            BlogComments comment = getById(commentId);
            if (comment == null) {
                return Result.fail("评论不存在");
            }

            // 4. 检查删除权限：评论所有者或管理员
            if (!comment.getUserId().equals(currentUser.getId()) && !UserHolder.isAdmin()) {
                return Result.fail("无权删除该评论");
            }

            // 5. 删除评论及其回复
            LambdaQueryWrapper<BlogComments> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(BlogComments::getId, commentId)
                    .or()
                    .eq(BlogComments::getParentId, commentId);
            boolean removed = remove(queryWrapper);

            if (!removed) {
                return Result.fail("评论删除失败");
            }

            return Result.ok();

        } catch (Exception e) {
            log.error("删除评论异常", e);
            return Result.fail("系统异常，删除评论失败");
        }
    }

    @Override
    public Result queryBlogComments(Long blogId) {
        try {
            // 1. 参数校验
            if (blogId == null || blogId <= 0) {
                return Result.fail("博客ID不合法");
            }

            // 2. 查询一级评论
            LambdaQueryWrapper<BlogComments> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(BlogComments::getBlogId, blogId)
                    .eq(BlogComments::getParentId, 0)
                    .orderByDesc(BlogComments::getCreateTime);

            List<BlogComments> comments = list(queryWrapper);

            // 3. 转换为DTO并填充用户信息
            List<CommentDTO> dtos = comments.stream().map(comment -> {
                CommentDTO dto = new CommentDTO();
                BeanUtils.copyProperties(comment, dto);

                // 查询用户信息
                User user = userService.getById(comment.getUserId());
                if (user != null) {
                    dto.setUsername(user.getNickName());
                    dto.setAvatar(user.getIcon());
                }

                return dto;
            }).collect(Collectors.toList());

            return Result.ok(dtos);

        } catch (Exception e) {
            log.error("查询博客评论异常", e);
            return Result.fail("系统异常，查询评论失败");
        }
    }

    @Override
    public Result queryUserComments(Long userId) {
        try {
            // 1. 参数校验
            if (userId == null || userId <= 0) {
                return Result.fail("用户ID不合法");
            }

            // 2. 查询用户评论
            LambdaQueryWrapper<BlogComments> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(BlogComments::getUserId, userId)
                    .orderByDesc(BlogComments::getCreateTime);

            List<BlogComments> comments = list(queryWrapper);

            // 3. 转换为DTO
            List<CommentDTO> dtos = comments.stream().map(comment -> {
                CommentDTO dto = new CommentDTO();
                BeanUtils.copyProperties(comment, dto);
                return dto;
            }).collect(Collectors.toList());

            return Result.ok(dtos);

        } catch (Exception e) {
            log.error("查询用户评论异常", e);
            return Result.fail("系统异常，查询评论失败");
        }
    }

    @Override
    public Result likeComment(Long commentId) {
        try {
            // 1. 参数校验
            if (commentId == null || commentId <= 0) {
                return Result.fail("评论ID不合法");
            }

            // 2. 更新点赞数
            boolean updated = lambdaUpdate()
                    .setSql("liked = liked + 1")
                    .eq(BlogComments::getId, commentId)
                    .update();

            if (!updated) {
                return Result.fail("点赞失败");
            }

            return Result.ok();

        } catch (Exception e) {
            log.error("点赞评论异常", e);
            return Result.fail("系统异常，点赞失败");
        }
    }

    @Override
    public Result queryCommentReplies(Long commentId) {
        try {
            // 1. 参数校验
            if (commentId == null || commentId <= 0) {
                return Result.fail("评论ID不合法");
            }

            // 2. 查询回复
            LambdaQueryWrapper<BlogComments> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(BlogComments::getParentId, commentId)
                    .orderByAsc(BlogComments::getCreateTime);

            List<BlogComments> replies = list(queryWrapper);

            // 3. 转换为DTO并填充用户信息
            List<CommentDTO> dtos = replies.stream().map(reply -> {
                CommentDTO dto = new CommentDTO();
                BeanUtils.copyProperties(reply, dto);

                // 查询用户信息
                User user = userService.getById(reply.getUserId());
                if (user != null) {
                    dto.setUsername(user.getNickName());
                    dto.setAvatar(user.getIcon());
                }

                return dto;
            }).collect(Collectors.toList());

            return Result.ok(dtos);

        } catch (Exception e) {
            log.error("查询评论回复异常", e);
            return Result.fail("系统异常，查询回复失败");
        }
    }
}