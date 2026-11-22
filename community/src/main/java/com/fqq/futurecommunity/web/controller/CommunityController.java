package com.fqq.futurecommunity.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.common.core.domain.BaseController;
import com.fqq.futurecommunity.common.core.domain.BaseResponse;
import com.fqq.futurecommunity.common.core.page.PageResult;
import com.fqq.futurecommunity.community.domain.Community;
import com.fqq.futurecommunity.community.domain.dto.CommunityDto;
import com.fqq.futurecommunity.community.domain.vo.CommunityOverviewVo;
import com.fqq.futurecommunity.community.domain.vo.CommunityVo;
import com.fqq.futurecommunity.community.service.CommunityService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author fqq
 * @since 2026年03月06日
 */
@Slf4j
@RestController
@RequestMapping("/community")
public class CommunityController extends BaseController {

    @Resource
    private CommunityService communityService;

    //查询小区列表
    @GetMapping("/list")
    @PreAuthorize("@pe.hasPerms('system:community:list')")
    public PageResult list(Community community){
        // 获取分页对象
        Page<CommunityDto> page = startPage();
        //  传入 Service
        Page<CommunityDto> result = communityService
                .selectCommunityList(page, community);
        return getData(result);
    }

    // 新增小区
    @PostMapping
    @PreAuthorize("@pe.hasPerms('system:community:add')")
    public BaseResponse add(@RequestBody Community community){

        return toAjax( communityService.insertCommunity(community));
    }

    //  根据id查询小区信息
    @GetMapping(value = "/{communityId}")
    @PreAuthorize("@pe.hasPerms('system:community:query')")
    public BaseResponse getInfo(@PathVariable("communityId") Long communityId){

        return BaseResponse.success(communityService.selectCommunityById(communityId));
    }

    //修改小区
    @PutMapping
    @PreAuthorize("@pe.hasPerms('system:community:edit')")
    public BaseResponse edit(@RequestBody Community community){
        return toAjax(communityService.updateCommunity(community));
    }

    //删除小区
    @DeleteMapping("/{communityIds}")
    @PreAuthorize("@pe.hasPerms('system:community:remove')")
    public BaseResponse remove(@PathVariable Long[] communityIds){
        return toAjax(communityService.deleteCommunityByIds(communityIds));
    }

    //小区下拉列表展示
    @GetMapping("/queryPullDown")
    @PreAuthorize("@pe.hasPerms('system:community:list')")
    public BaseResponse queryPullDown(Community community){

        log.info("log() called with parameters => [community = {}]", community);

        List<CommunityVo> voList = null;

        try {
            voList = communityService.queryPullDown(community);
        } catch (Exception e) {
            log.warn("获取小区下拉列表失败!", e);
        }

        log.info("log() returned: {}", voList);
        return BaseResponse.success(voList);
    }

    /**
     * 地图卡片：查询单个小区运行概览。
     */
    @GetMapping("/overview/{communityId}")
    @PreAuthorize("@pe.hasPerms('system:community:list')")
    public BaseResponse getOverview(@PathVariable("communityId") Long communityId) {
        CommunityOverviewVo overview = communityService.getOverview(communityId);
        if (overview == null) {
            return BaseResponse.fail("404", "小区信息不存在");
        }
        return BaseResponse.success(overview);
    }

    /**
     * 地图卡片：查询 community 表中的所有小区运行概览。
     *
     * 前端用于在地图上展示所有已录入小区；
     * 当前点进来的 communityId 只用于让该小区排在第一位，地图中心与缩放比例由前端控制。
     */
    @GetMapping("/overview/all/{communityId}")
    @PreAuthorize("@pe.hasPerms('system:community:list')")
    public BaseResponse listAllOverview(@PathVariable("communityId") Long communityId) {
        return BaseResponse.success(communityService.listAllOverview(communityId));
    }

    /**
     * 兼容旧前端 nearby 接口：返回所有已录入小区运行概览。
     */
    @GetMapping("/overview/nearby/{communityId}")
    @PreAuthorize("@pe.hasPerms('system:community:list')")
    public BaseResponse listNearbyOverview(@PathVariable("communityId") Long communityId,
                                           @RequestParam(value = "radiusKm", required = false) Double radiusKm) {
        return BaseResponse.success(communityService.listNearbyOverview(communityId, radiusKm));
    }


}
