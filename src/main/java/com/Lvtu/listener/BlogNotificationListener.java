package com.Lvtu.listener;

import com.Lvtu.entity.Blog;
import com.Lvtu.entity.Follow;
import com.Lvtu.service.IBlogService;
import com.Lvtu.service.IFollowService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.Lvtu.utils.RabbitMQConstants.BLOG_NOTIFICATION_QUEUE;
import static com.Lvtu.utils.RedisConstants.FEED_KEY;

@Slf4j
@Component
public class BlogNotificationListener {

    @Autowired
    private IFollowService followService;

    @Autowired
    private IBlogService blogService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @RabbitListener(queues = BLOG_NOTIFICATION_QUEUE)
    public void handleBlogNotification(Long blogId) {
        log.info("收到消息，博客 ID: {}", blogId);
        // 通过博客ID可以获取到用户ID
        Long userId = getUserIdByBlogId(blogId);
        log.info("userid: {}", userId);
        //查询笔记作者的所有粉丝 select * from tb_follow where follow_user_id = ?
        List<Follow> follows = followService.query().eq("follow_user_id", userId).list();
        log.info("follows: {}", follows);
        //推送笔记id给所有粉丝
        for (Follow follow : follows) {
            //获取粉丝id
            Long followerId = follow.getUserId();
            System.out.println(followerId+"---------------------------------");
            //推送
            String key = FEED_KEY + followerId;
            stringRedisTemplate.opsForZSet().add(key, blogId.toString(), System.currentTimeMillis());
        }
    }


    private Long getUserIdByBlogId(Long blogId) {
        // 根据博客 ID 查询博客对象
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", blogId);
        Blog blog = blogService.getOne(queryWrapper);
        if (blog != null) {
            return blog.getUserId();
        }
        return null;
    }
}