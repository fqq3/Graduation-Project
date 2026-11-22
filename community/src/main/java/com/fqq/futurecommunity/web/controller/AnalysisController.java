package com.fqq.futurecommunity.web.controller;

import com.fqq.futurecommunity.common.core.domain.BaseResponse;
import com.fqq.futurecommunity.community.service.AnalysisService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Community analysis endpoints. */
@RestController
@RequestMapping("/community/analysis")
public class AnalysisController {

    @Resource
    private AnalysisService analysisService;


    @GetMapping("/community/ranking")
    @PreAuthorize("@pe.hasPerms('community:analysis:ranking')")
    public BaseResponse communityRanking(@RequestParam(required = false) Long communityId,
                                         @RequestParam(defaultValue = "30") Integer days) {
        return BaseResponse.success(analysisService.getCommunityRanking(communityId, days));
    }


    @GetMapping("/community/ranking/indicators")
    @PreAuthorize("@pe.hasPerms('community:analysis:ranking')")
    public BaseResponse communityRankingIndicators() {
        return BaseResponse.success(analysisService.getCommunityRankingIndicators());
    }

    @GetMapping("/complaint/wordcloud")
    @PreAuthorize("@pe.hasPerms('community:analysis:complaintWordCloud')")
    public BaseResponse complaintWordCloud(@RequestParam(required = false) Long communityId,
                                           @RequestParam(required = false) String beginTime,
                                           @RequestParam(required = false) String endTime) {
        return BaseResponse.success(analysisService.getComplaintWordCloud(communityId, beginTime, endTime));
    }


    @GetMapping("/complaint/tfidf")
    @PreAuthorize("@pe.hasPerms('community:analysis:complaintWordCloud')")
    public BaseResponse complaintTfidf(@RequestParam(required = false) Long communityId,
                                       @RequestParam(required = false) String beginTime,
                                       @RequestParam(required = false) String endTime,
                                       @RequestParam(required = false) String type,
                                       @RequestParam(defaultValue = "30") Integer topN) {
        return BaseResponse.success(analysisService.getComplaintTfidf(communityId, beginTime, endTime, type, topN));
    }

    @GetMapping("/parking/peak")
    @PreAuthorize("@pe.hasPerms('community:analysis:parkingPeak')")
    public BaseResponse parkingPeak(@RequestParam(required = false) Long communityId,
                                    @RequestParam(required = false) String date) {
        return BaseResponse.success(analysisService.getParkingPeak(communityId, date));
    }
}
