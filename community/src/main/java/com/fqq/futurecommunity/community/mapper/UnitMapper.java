package com.fqq.futurecommunity.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.community.domain.Unit;
import com.fqq.futurecommunity.community.domain.dto.UnitDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UnitMapper extends BaseMapper<Unit> {
    @Select("""
    <script>
    select u.*, b.building_name as buildingName, c.community_name as communityName
    from unit u
    left join building b on b.building_id = u.building_id
    left join community c on c.community_id = u.community_id
    <where>
      <if test="q != null and q.communityId != null">
        and u.community_id = #{q.communityId}
      </if>
      <if test="q != null and q.buildingId != null">
        and u.building_id = #{q.buildingId}
      </if>
      <if test="q != null and q.unitId != null">
        and u.unit_id = #{q.unitId}
      </if>
      <if test="q != null and q.unitName != null and q.unitName != ''">
        and u.unit_name like concat('%', #{q.unitName}, '%')
      </if>
      <if test="q != null and q.unitCode != null and q.unitCode != ''">
        and u.unit_code like concat('%', #{q.unitCode}, '%')
      </if>
      <if test="q != null and q.unitHaveElevator != null and q.unitHaveElevator != ''">
        and u.unit_have_elevator = #{q.unitHaveElevator}
      </if>
    </where>
    order by u.create_time desc
    </script>
    """)
    Page<UnitDto> selectUnitList(Page<UnitDto> page, @Param("q") Unit q);
}
