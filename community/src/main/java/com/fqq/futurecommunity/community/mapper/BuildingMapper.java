package com.fqq.futurecommunity.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.community.domain.Building;
import com.fqq.futurecommunity.community.domain.dto.BuildingDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface BuildingMapper extends BaseMapper<Building> {
    @Select("""
    <script>
    select b.*, c.community_name as communityName
    from building b
    left join community c on c.community_id = b.community_id
    <where>
      <if test="q != null and q.communityId != null">
        and b.community_id = #{q.communityId}
      </if>
      <if test="q != null and q.buildingId != null">
        and b.building_id = #{q.buildingId}
      </if>
      <if test="q != null and q.buildingName != null and q.buildingName != ''">
        and b.building_name like concat('%', #{q.buildingName}, '%')
      </if>
      <if test="q != null and q.buildingCode != null and q.buildingCode != ''">
        and b.building_code like concat('%', #{q.buildingCode}, '%')
      </if>
    </where>
    order by b.create_time desc
    </script>
    """)
    Page<BuildingDto> selectBuildingList(Page<BuildingDto> page, @Param("q") Building q);

    @Select("""
    <script>
    select building_id as buildingId, building_name as buildingName, community_id as communityId, building_id as code, building_name as name
    from building
    <where>
      <if test="q != null and q.communityId != null">
        and community_id = #{q.communityId}
      </if>
    </where>
    order by create_time desc
    </script>
    """)
    java.util.List<com.fqq.futurecommunity.community.domain.vo.BuildingVo> selectBuildingOptions(@Param("q") Building q);

    @Select("select unit_id as unitId, unit_name as unitName, unit_id as code, unit_name as name from unit where building_id = #{buildingId} order by create_time desc")
    java.util.List<com.fqq.futurecommunity.community.domain.vo.UnitVo> selectUnitOptions(Long buildingId);
}
