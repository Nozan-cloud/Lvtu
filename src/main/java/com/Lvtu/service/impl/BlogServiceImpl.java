package com.Lvtu.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.Lvtu.dto.Result;
import com.Lvtu.dto.ScrollResult;
import com.Lvtu.dto.UserDTO;
import com.Lvtu.entity.Blog;
import com.Lvtu.entity.Follow;
import com.Lvtu.entity.User;
import com.Lvtu.mapper.BlogMapper;
import com.Lvtu.service.IBlogService;
import com.Lvtu.service.IFollowService;
import com.Lvtu.service.IUserService;
import com.Lvtu.utils.SystemConstants;
import com.Lvtu.utils.UserHolder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.Lvtu.utils.RabbitMQConstants.BLOG_NOTIFICATION_EXCHANGE;
import static com.Lvtu.utils.RabbitMQConstants.BLOG_NOTIFICATION_ROUTING_KEY;
import static com.Lvtu.utils.RedisConstants.BLOG_LIKED_KEY;
import static com.Lvtu.utils.RedisConstants.FEED_KEY;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements IBlogService {

    @Resource
    private IUserService userService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private IFollowService followService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public Result queryHotBlog(Integer current) {
        // 根据用户查询
        Page<Blog> page = query()
                .orderByDesc("liked")
                .page(new Page<>(current, SystemConstants.MAX_PAGE_SIZE));
        // 获取当前页数据
        List<Blog> records = page.getRecords();
        // 查询用户
        records.forEach(blog -> {
            this.queryBlogUser(blog);
            this.isBlogLiked(blog);
        });
        return Result.ok(records);
    }

    @Override
    public Result queryBlogById(Long id) {
        // 1.查询blog
        Blog blog = getById(id);
        if (blog == null) {
            return Result.fail("笔记不存在！");
        }
        // 2.查询blog有关的用户
        queryBlogUser(blog);
        // 3.查询blog是否被点赞
        isBlogLiked(blog);
        return Result.ok(blog);
    }

    private void isBlogLiked(Blog blog) {
        // 1.获取登录用户
        UserDTO user = UserHolder.getUser();
        if (user == null) {
            // 用户未登录，无需查询是否点赞
            return;
        }
        Long userId = user.getId();
        // 2.判断当前登录用户是否已经点赞
        String key = "blog:liked:" + blog.getId();
        Double score = stringRedisTemplate.opsForZSet().score(key, userId.toString());
        blog.setIsLike(score != null);
    }

    @Override
    public Result likeBlog(Long id) {
        // 1.获取登录用户
        Long userId = UserHolder.getUser().getId();
        // 2.判断当前登录用户是否已经点赞
        String key = BLOG_LIKED_KEY + id;
        Double score = stringRedisTemplate.opsForZSet().score(key, userId.toString());
        if (score == null) {
            // 3.如果未点赞，可以点赞
            // 3.1.数据库点赞数 + 1
            boolean isSuccess = update().setSql("liked = liked + 1").eq("id", id).update();
            // 3.2.保存用户到Redis的set集合  zadd key value score
            if (isSuccess) {
                stringRedisTemplate.opsForZSet().add(key, userId.toString(), System.currentTimeMillis());
            }
        } else {
            // 4.如果已点赞，取消点赞
            // 4.1.数据库点赞数 -1
            boolean isSuccess = update().setSql("liked = liked - 1").eq("id", id).update();
            // 4.2.把用户从Redis的set集合移除
            if (isSuccess) {
                stringRedisTemplate.opsForZSet().remove(key, userId.toString());
            }
        }
        return Result.ok();
    }

    @Override
    /**
     * 查询博客点赞前5的用户信息
     * @param id 博客ID
     * @return 包含前5点赞用户信息的Result对象
     */
    public Result queryBlogLikes(Long id) {
        // 构造Redis中存储点赞信息的key
        String key = BLOG_LIKED_KEY + id;
        // 1. 从Redis的有序集合中查询前5个点赞用户ID（按分数从低到高排序）
        // zrange key 0 4 命令获取排名0-4的元素
        Set<String> top5 = stringRedisTemplate.opsForZSet().range(key, 0, 4);
//        System.out.println("前5的元素："+top5);
        // 如果查询结果为空，返回空列表
        if (top5 == null || top5.isEmpty()) {
            return Result.ok(Collections.emptyList());
        }

        // 2. 将String类型的用户ID转换为Long类型列表
        List<Long> ids = top5.stream().map(Long::valueOf).collect(Collectors.toList());
//        System.out.println("获取的点赞前5的用户类型列表："+ids);
        // 将用户ID列表用逗号拼接成字符串，用于后续SQL中的ORDER BY FIELD
        String idStr = StrUtil.join(",", ids);

        // 3. 根据用户ID查询用户信息，并按照传入的ID顺序排序
        // 使用MyBatis Plus的query构造器，WHERE id IN (...) ORDER BY FIELD(id,...)
        List<UserDTO> userDTOS = userService.query()
                .in("id", ids)  // WHERE id IN (5,1,...)
                .last("ORDER BY FIELD(id," + idStr + ")")  // 保持与Redis查询结果相同的顺序
                .list()
                .stream()
                .map(user -> BeanUtil.copyProperties(user, UserDTO.class))  // 转换为DTO对象
                .collect(Collectors.toList());

        // 4. 返回查询结果
        return Result.ok(userDTOS);
    }
    @Override
    public Result saveBlog(Blog blog) {
        // 1.获取登录用户
        UserDTO user = UserHolder.getUser();
        blog.setUserId(user.getId());
        // 2.保存探店笔记
        boolean isSuccess = save(blog);
        if(!isSuccess){
            return Result.fail("新增笔记失败!");
        }
        //发送消息到RabbitMQ队列
        rabbitTemplate.convertAndSend(BLOG_NOTIFICATION_EXCHANGE, BLOG_NOTIFICATION_ROUTING_KEY, blog.getId());
        return Result.ok(blog.getId());
//        // 3.查询笔记作者的所有粉丝 select * from tb_follow where follow_user_id = ?
//        List<Follow> follows = followService.query().eq("follow_user_id", user.getId()).list();
//        // 4.推送笔记id给所有粉丝
//        for (Follow follow : follows) {
//            // 4.1.获取粉丝id
//            Long userId = follow.getUserId();
//            // 4.2.推送
//            String key = FEED_KEY + userId;
//            stringRedisTemplate.opsForZSet().add(key, blog.getId().toString(), System.currentTimeMillis());
//        }
//        // 5.返回id
//        return Result.ok(blog.getId());
    }

    @Override
    public Result queryBlogOfFollow(Long max, Integer offset) {
        // 1.获取当前用户
        Long userId = UserHolder.getUser().getId();
        // 2.查询收件箱 ZREVRANGEBYSCORE key Max Min LIMIT offset count
        String key = FEED_KEY + userId;
        Set<ZSetOperations.TypedTuple<String>> typedTuples = stringRedisTemplate.opsForZSet()
                .reverseRangeByScoreWithScores(key, 0, max, offset, 2);
        // 3.非空判断
        if (typedTuples == null || typedTuples.isEmpty()) {
            return Result.ok();
        }
        // 4.解析数据：blogId、minTime（时间戳）、offset
        List<Long> ids = new ArrayList<>(typedTuples.size());
        long minTime = 0; // 2
        int os = 1; // 2
        for (ZSetOperations.TypedTuple<String> tuple : typedTuples) { // 5 4 4 2 2
            // 4.1.获取id
            ids.add(Long.valueOf(tuple.getValue()));
            // 4.2.获取分数(时间戳）
            long time = tuple.getScore().longValue();
            if(time == minTime){
                os++;
            }else{
                minTime = time;
                os = 1;
            }
        }

        // 5.根据id查询blog
        String idStr = StrUtil.join(",", ids);
        List<Blog> blogs = query().in("id", ids).last("ORDER BY FIELD(id," + idStr + ")").list();

        for (Blog blog : blogs) {
            // 5.1.查询blog有关的用户
            queryBlogUser(blog);
            // 5.2.查询blog是否被点赞
            isBlogLiked(blog);
        }

        // 6.封装并返回
        ScrollResult r = new ScrollResult();
        r.setList(blogs);
        r.setOffset(os);
        r.setMinTime(minTime);

        return Result.ok(r);
    }

    private void queryBlogUser(Blog blog) {
        Long userId = blog.getUserId();
        User user = userService.getById(userId);
        blog.setName(user.getNickName());
        blog.setIcon(user.getIcon());
    }
}
