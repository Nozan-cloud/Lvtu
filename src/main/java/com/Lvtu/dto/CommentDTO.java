package com.Lvtu.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 评论数据传输对象
 * 用于前端与后端之间的数据交互
 */
@Data
public class CommentDTO {
    /**
     * 评论ID
     */
    private Long id;

    /**
     * 关联的博客ID
     */
    private Long blogId;

    /**
     * 评论用户ID
     */
    private Long userId;

    /**
     * 父评论ID，一级评论为0
     */
    private Long parentId;

    /**
     * 回复的目标评论ID
     */
    private Long answerId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 点赞数
     */
    private Integer liked;

    /**
     * 评论状态：false-正常，true-异常
     */
    private Boolean status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 评论者用户名（非数据库字段，用于前端显示）
     */
    private String username;

    /**
     * 评论者头像URL（非数据库字段，用于前端显示）
     */
    private String avatar;
}