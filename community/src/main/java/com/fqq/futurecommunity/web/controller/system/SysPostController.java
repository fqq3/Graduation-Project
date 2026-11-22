package com.fqq.futurecommunity.web.controller.system;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.common.constant.UserConstants;
import com.fqq.futurecommunity.common.core.domain.BaseController;
import com.fqq.futurecommunity.common.core.domain.BaseResponse;
import com.fqq.futurecommunity.common.core.page.PageResult;
import com.fqq.futurecommunity.common.utils.ChainedMap;
import com.fqq.futurecommunity.common.utils.ExcelUtils;
import com.fqq.futurecommunity.common.utils.FileStorageUtils;
import com.fqq.futurecommunity.common.utils.SecurityUtils;
import com.fqq.futurecommunity.system.domain.SysPost;
import com.fqq.futurecommunity.system.service.SysPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.util.List;

/**
 * 岗位信息操作处理
 * @author spikeCong
 * @date 2023/5/24
 **/
@RestController
@RequestMapping("/system/post")
public class SysPostController extends BaseController {

    @Autowired
    private SysPostService postService;

    /**
     * 获取岗位列表
     */
    @GetMapping("/list")
    public PageResult list(SysPost post)
    {
        Page<SysPost> page = startPage();
        Page<SysPost> result = postService.selectPostList(page, post);
        return getData(result);
    }

    /**
     * 根据岗位编号获取详细信息
     */
    @GetMapping(value = "/{postId}")
    public BaseResponse getInfo(@PathVariable Long postId)
    {
        return BaseResponse.success(postService.selectPostById(postId));
    }

    /**
     * 新增岗位
     */
    @PostMapping
    public BaseResponse add( @RequestBody SysPost post)
    {
        if (UserConstants.NOT_UNIQUE.equals(postService.checkPostNameUnique(post)))
        {
            return BaseResponse.fail("新增岗位'" + post.getPostName() + "'失败，岗位名称已存在");
        }
        else if (UserConstants.NOT_UNIQUE.equals(postService.checkPostCodeUnique(post)))
        {
            return BaseResponse.fail("新增岗位'" + post.getPostName() + "'失败，岗位编码已存在");
        }
        post.setCreateBy(SecurityUtils.getUserName());
        return toAjax(postService.insertPost(post));
    }

    /**
     * 修改岗位
     */
    @PutMapping
    public BaseResponse edit(@RequestBody SysPost post)
    {
        if (UserConstants.NOT_UNIQUE.equals(postService.checkPostNameUnique(post)))
        {
            return BaseResponse.fail("修改岗位'" + post.getPostName() + "'失败，岗位名称已存在");
        }
        else if (UserConstants.NOT_UNIQUE.equals(postService.checkPostCodeUnique(post)))
        {
            return BaseResponse.fail("修改岗位'" + post.getPostName() + "'失败，岗位编码已存在");
        }
        post.setUpdateBy(SecurityUtils.getUserName());
        return toAjax(postService.updatePost(post));
    }

    /**
     * 删除岗位
     */
    @DeleteMapping("/{postIds}")
    public BaseResponse remove(@PathVariable Long[] postIds)
    {
        return toAjax(postService.deletePostByIds(postIds));
    }

    /**
     * 获取岗位选择框列表
     */
    @GetMapping("/optionselect")
    public BaseResponse optionselect()
    {
        List<SysPost> posts = postService.selectPostAll();
        return BaseResponse.success(posts);
    }

    @GetMapping("/export")
    @PreAuthorize("@pe.hasPerms('system:post:export')")
    public ChainedMap export(SysPost post) {
        Page<SysPost> page = new Page<>(1, 100000);
        List<SysPost> postList = postService.selectPostList(page, post).getRecords();
        String fileName = FileStorageUtils.generateFileName("post", "xlsx");
        Path filePath = FileStorageUtils.getDownloadDir().resolve(fileName);
        ExcelUtils.writeExcelToFile(postList, SysPost.class, filePath, "Post");
        return ChainedMap.create().set("code", 200).set("msg", fileName);
    }

}
