package com.fqq.futurecommunity.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.community.domain.Room;
import com.fqq.futurecommunity.community.domain.dto.RoomDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface RoomMapper extends BaseMapper<Room> {
    @Select("""
    <script>
    select r.*, u.unit_name as unitName, b.building_name as buildingName, c.community_name as communityName
    from room r
    left join unit u on u.unit_id = r.unit_id
    left join building b on b.building_id = r.building_id
    left join community c on c.community_id = r.community_id
    <where>
      <if test="q != null and q.communityId != null">
        and r.community_id = #{q.communityId}
      </if>
      <if test="q != null and q.buildingId != null">
        and r.building_id = #{q.buildingId}
      </if>
      <if test="q != null and q.unitId != null">
        and r.unit_id = #{q.unitId}
      </if>
      <if test="q != null and q.roomId != null">
        and r.room_id = #{q.roomId}
      </if>
      <if test="q != null and q.roomName != null and q.roomName != ''">
        and r.room_name like concat('%', #{q.roomName}, '%')
      </if>
      <if test="q != null and q.roomCode != null and q.roomCode != ''">
        and r.room_code like concat('%', #{q.roomCode}, '%')
      </if>
      <if test="q != null and q.roomStatus != null and q.roomStatus != ''">
        and r.room_status = #{q.roomStatus}
      </if>
      <if test="q != null and q.roomIsShop != null and q.roomIsShop != ''">
        and r.room_is_shop = #{q.roomIsShop}
      </if>
      <if test="q != null and q.roomSCommercialHouse != null and q.roomSCommercialHouse != ''">
        and r.room_s_commercial_house = #{q.roomSCommercialHouse}
      </if>
      <if test="q != null and q.roomHouseType != null and q.roomHouseType != ''">
        and r.room_house_type = #{q.roomHouseType}
      </if>
    </where>
    order by r.create_time desc
    </script>
    """)
    Page<RoomDto> selectRoomList(Page<RoomDto> page, @Param("q") Room q);
}
