package com.fqq.futurecommunity.community.service;

import com.fqq.futurecommunity.community.domain.vo.ComplaintWordCloudVo;
import com.fqq.futurecommunity.community.domain.vo.ParkingPeakVo;
import com.fqq.futurecommunity.community.domain.vo.CommunityRankingVo;
import com.fqq.futurecommunity.community.domain.vo.TfidfAnalysisVo;

import java.util.List;
import java.util.Map;

/** 数据分析服务 */
public interface AnalysisService {
    ComplaintWordCloudVo getComplaintWordCloud(Long communityId, String beginTime, String endTime);

    ParkingPeakVo getParkingPeak(Long communityId, String date);

    List<CommunityRankingVo> getCommunityRanking(Long communityId, Integer days);

    TfidfAnalysisVo getComplaintTfidf(Long communityId, String beginTime, String endTime, String type, Integer topN);

    List<Map<String, Object>> getCommunityRankingIndicators();
}
