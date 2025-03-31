package com.Lvtu.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.Lvtu.dto.Result;
import com.Lvtu.dto.UserDTO;
import com.Lvtu.entity.Blog;
import com.Lvtu.service.IBlogService;
import com.Lvtu.utils.SystemConstants;
import com.Lvtu.utils.UserHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 虎哥
 */
@RestController
@RequestMapping("/blog")
@Api(tags = "博客相关接口")
public class BlogController {

    @Resource
    private IBlogService blogService;

    @PostMapping
    @ApiOperation("保存博客")
    public Result saveBlog(@RequestBody Blog blog) {
        return blogService.saveBlog(blog);
    }

    @PutMapping("/like/{id}")
    @ApiOperation("点赞博客")
    public Result likeBlog(@PathVariable("id") Long id) {
        return blogService.likeBlog(id);
    }

    @GetMapping("/of/me")
    @ApiOperation("查询登录用户的博客列表")
    public Result queryMyBlog(
            @ApiParam(
                    name = "current",
                    value = "当前页码（默认第1页）",
                    required = false,
                    defaultValue = "1",
                    example = "1"
            )
            @RequestParam(value = "current", defaultValue = "1") Integer current
    ) {
        // 获取登录用户
        UserDTO user = UserHolder.getUser();
        // 根据用户查询
        Page<Blog> page = blogService.query()
                .eq("user_id", user.getId()).page(new Page<>(current, SystemConstants.MAX_PAGE_SIZE));
        // 获取当前页数据
        List<Blog> records = page.getRecords();
        return Result.ok(records);
    }

    @GetMapping("/hot")
    @ApiOperation("查询热门博客")
    public Result queryHotBlog(@RequestParam(value = "current", defaultValue = "1") Integer current) {
        return blogService.queryHotBlog(current);
    }

    @GetMapping("/{id}")
    @ApiOperation("查询博客详情")
    public Result queryBlogById(@PathVariable("id") Long id) {
        return blogService.queryBlogById(id);
    }

    //暂时没用到
//    @GetMapping("/likes/{id}")
//    @ApiOperation("从redis缓存查询博客点赞前5的用户信息")
//    public Result queryBlogLikes(@PathVariable("id") Long id) {
//        return blogService.queryBlogLikes(id);
//    }

    //暂时没用到
//    @GetMapping("/of/user")
//    @ApiOperation("查询用户博客")
//    public Result queryBlogByUserId(
//            @RequestParam(value = "current", defaultValue = "1") Integer current,
//            @RequestParam("id") Long id) {
//        // 根据用户查询
//        Page<Blog> page = blogService.query()
//                .eq("user_id", id).page(new Page<>(current, SystemConstants.MAX_PAGE_SIZE));
//        // 获取当前页数据
//        List<Blog> records = page.getRecords();
//        return Result.ok(records);
//    }

    @GetMapping("/list")
    @ApiOperation("分页查询所有博客")
    public Result queryAllBlogs(
            @RequestParam(value = "current", defaultValue = "1") Integer current,
            @RequestParam(value = "size", defaultValue = "5") Integer size) {
        // 根据分页参数查询所有博客（按创建时间倒序）
        Page<Blog> page = blogService.query()
                .orderByDesc(SystemConstants.BLOG_ORDER_FIELD)
                .page(new Page<>(current, size));
        // 获取当前页数据
        List<Blog> records = page.getRecords();
        return Result.ok(records);
    }


    @GetMapping("/of/follow")
    @ApiOperation("查询关注用户博客")
    public Result queryBlogOfFollow(
            @RequestParam("lastId") Long max, @RequestParam(value = "offset", defaultValue = "0") Integer offset){
        return blogService.queryBlogOfFollow(max, offset);
    }
}
