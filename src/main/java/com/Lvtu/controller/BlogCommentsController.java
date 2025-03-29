package com.Lvtu.controller;

import com.Lvtu.dto.CommentDTO;
import com.Lvtu.dto.Result;
import com.Lvtu.service.IBlogCommentsService;
import com.Lvtu.utils.UserHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 博客评论控制器
 * 处理所有与评论相关的HTTP请求
 */
@Slf4j
@RestController
@RequestMapping("/blog-comments")
@Api(tags = "博客评论接口")
public class BlogCommentsController {

    @Resource
    private IBlogCommentsService commentService;

    /**
     * 添加评论
     * @param commentDTO 评论数据
     * @return 操作结果
     */
    @PostMapping
    @ApiOperation("添加评论")
    public Result addComment(@RequestBody CommentDTO commentDTO) {
        log.info("添加评论请求，数据：{}", commentDTO);
        try {
            return commentService.addComment(commentDTO);
        } catch (Exception e) {
            log.error("添加评论失败", e);
            return Result.fail("添加评论失败，请稍后再试");
        }
    }

    /**
     * 删除评论
     * @param commentId 评论ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @ApiOperation("删除评论")
    public Result deleteComment(@PathVariable("id") Long commentId) {
        log.info("删除评论请求，评论ID：{}", commentId);
        try {
            return commentService.deleteComment(commentId);
        } catch (Exception e) {
            log.error("删除评论失败", e);
            return Result.fail("删除评论失败，请稍后再试");
        }
    }

    /**
     * 获取博客下的评论列表
     * @param blogId 博客ID
     * @return 评论列表
     */
    @GetMapping("/of/blog/{id}")
    @ApiOperation("获取博客评论列表")
    public Result queryBlogComments(@PathVariable("id") Long blogId) {
        log.info("获取博客评论列表，博客ID：{}", blogId);
        try {
            return commentService.queryBlogComments(blogId);
        } catch (Exception e) {
            log.error("获取博客评论列表失败", e);
            return Result.fail("获取评论失败，请稍后再试");
        }
    }

    /**
     * 获取用户的所有评论
     * @return 评论列表
     */
    @GetMapping("/of/user")
    @ApiOperation("获取用户评论列表")
    public Result queryUserComments() {
        Long userId = UserHolder.getUser().getId();
        log.info("获取用户评论列表，用户ID：{}", userId);
        try {
            return commentService.queryUserComments(userId);
        } catch (Exception e) {
            log.error("获取用户评论列表失败", e);
            return Result.fail("获取评论失败，请稍后再试");
        }
    }

    /**
     * 点赞评论
     * @param commentId 评论ID
     * @return 操作结果
     */
    @PutMapping("/like/{id}")
    @ApiOperation("点赞评论")
    public Result likeComment(@PathVariable("id") Long commentId) {
        log.info("点赞评论，评论ID：{}", commentId);
        try {
            return commentService.likeComment(commentId);
        } catch (Exception e) {
            log.error("点赞评论失败", e);
            return Result.fail("点赞失败，请稍后再试");
        }
    }

    /**
     * 获取评论的回复列表
     * @param commentId 评论ID
     * @return 回复列表
     */
    @GetMapping("/of/comment/{id}")
    @ApiOperation("获取评论回复列表")
    public Result queryCommentReplies(@PathVariable("id") Long commentId) {
        log.info("获取评论回复列表，评论ID：{}", commentId);
        try {
            return commentService.queryCommentReplies(commentId);
        } catch (Exception e) {
            log.error("获取评论回复列表失败", e);
            return Result.fail("获取回复失败，请稍后再试");
        }
    }
}