package com.fqq.futurecommunity.community.mapper;

import com.fqq.futurecommunity.community.domain.vo.AnalysisTextRow;
import com.fqq.futurecommunity.community.domain.vo.CommunityRankingVo;
import com.fqq.futurecommunity.community.domain.vo.ParkingPeriodRow;
import com.fqq.futurecommunity.community.domain.vo.ParkingTrendVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/** Mapper for analysis queries. */
@Mapper
public interface AnalysisMapper {


    List<CommunityRankingVo> selectRankingCommunityBases(@Param("communityId") Long communityId,
                                                         @Param("deptId") Long deptId);

    Integer countRankingRoomByCommunityId(@Param("communityId") Long communityId);

    Integer countRankingOccupiedRoomByCommunityId(@Param("communityId") Long communityId);

    Integer countRankingDeviceByCommunityId(@Param("communityId") Long communityId);

    Integer countRankingErrorDeviceByCommunityId(@Param("communityId") Long communityId);

    Integer countRankingValidFeeBillByCommunityId(@Param("communityId") Long communityId,
                                                  @Param("begin") LocalDateTime begin,
                                                  @Param("end") LocalDateTime end);

    Integer countRankingPaidFeeBillByCommunityId(@Param("communityId") Long communityId,
                                                 @Param("begin") LocalDateTime begin,
                                                 @Param("end") LocalDateTime end);

    Integer countRankingRepairByCommunityId(@Param("communityId") Long communityId,
                                            @Param("begin") LocalDateTime begin,
                                            @Param("end") LocalDateTime end);

    Integer countRankingPendingRepairByCommunityId(@Param("communityId") Long communityId,
                                                   @Param("begin") LocalDateTime begin,
                                                   @Param("end") LocalDateTime end);

    Integer countRankingComplaintByCommunityId(@Param("communityId") Long communityId,
                                               @Param("begin") LocalDateTime begin,
                                               @Param("end") LocalDateTime end);

    Integer countRankingParkingSpaceByCommunityId(@Param("communityId") Long communityId);

    Integer countRankingParkingRecordByCommunityId(@Param("communityId") Long communityId,
                                                   @Param("begin") LocalDateTime begin,
                                                   @Param("end") LocalDateTime end);

    List<AnalysisTextRow> selectComplaintSuggestTexts(@Param("communityId") Long communityId,
                                                      @Param("beginTime") LocalDateTime beginTime,
                                                      @Param("endTime") LocalDateTime endTime);

    Long countParkingSpace(@Param("communityId") Long communityId);

    List<ParkingPeriodRow> selectParkingPeriods(@Param("communityId") Long communityId,
                                                @Param("begin") LocalDateTime begin,
                                                @Param("end") LocalDateTime end);

    List<ParkingTrendVo> selectParkingTrend7Days(@Param("communityId") Long communityId,
                                                 @Param("beginDate") LocalDate beginDate,
                                                 @Param("endDate") LocalDate endDate);
}
