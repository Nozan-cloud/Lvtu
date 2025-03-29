package com.Lvtu.service;

import com.Lvtu.dto.CommentDTO;
import com.Lvtu.dto.Result;
import com.Lvtu.entity.BlogComments;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 博客评论服务接口
 */
public interface IBlogCommentsService extends IService<BlogComments> {

    /**
     * 添加评论
     * @param commentDTO 评论数据
     * @return 操作结果
     */
    Result addComment(CommentDTO commentDTO);

    /**
     * 删除评论
     * @param commentId 评论ID
     * @return 操作结果
     */
    Result deleteComment(Long commentId);

    /**
     * 获取博客下的评论列表
     * @param blogId 博客ID
     * @return 评论列表
     */
    Result queryBlogComments(Long blogId);

    /**
     * 获取用户的所有评论
     * @param userId 用户ID
     * @return 评论列表
     */
    Result queryUserComments(Long userId);

    /**
     * 点赞评论
     * @param commentId 评论ID
     * @return 操作结果
     */
    Result likeComment(Long commentId);

    /**
     * 获取评论的回复列表
     * @param commentId 评论ID
     * @return 回复列表
     */
    Result queryCommentReplies(Long commentId);
}