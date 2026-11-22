package com.fqq.futurecommunity.community.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.community.domain.OwnerRoomRecord;
import com.fqq.futurecommunity.community.domain.dto.OwnerRoomRecordDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
@Mapper
public interface OwnerRoomRecordMapper extends BaseMapper<OwnerRoomRecord> {
  @Select("""
  <script>
  select o.*,
         o.create_by as createByName,
         ow.owner_real_name as ownerRealName,
         ow.owner_phone_number as ownerPhoneNumber,
         ow.owner_nickname as ownerNickname,
         ow.owner_portrait as ownerPortrait,
         r.room_name as roomName,
         u.unit_name as unitName,
         b.building_name as buildingName,
         c.community_name as communityName
  from owner_room_record o
  left join owner ow on ow.owner_id = o.owner_id
  left join room r on r.room_id = o.room_id
  left join unit u on u.unit_id = o.unit_id
  left join building b on b.building_id = o.building_id
  left join community c on c.community_id = o.community_id
  <where>
    <if test="q != null and q.recordId != null">
      and o.record_id = #{q.recordId}
    </if>
    <if test="q != null and q.ownerRoomId != null and q.ownerRoomId != ''">
      and o.owner_room_id = #{q.ownerRoomId}
    </if>
    <if test="q != null and q.communityId != null">
      and o.community_id = #{q.communityId}
    </if>
    <if test="q != null and q.ownerId != null">
      and o.owner_id = #{q.ownerId}
    </if>
    <if test="q != null and q.ownerType != null and q.ownerType != ''">
      and o.owner_type = #{q.ownerType}
    </if>
    <if test="q != null and q.roomStatus != null and q.roomStatus != ''">
      and o.room_status = #{q.roomStatus}
    </if>
  </where>
  order by o.create_time desc
  </script>
  """)
  Page<OwnerRoomRecordDto> selectOwnerRoomRecordList(Page<OwnerRoomRecordDto> page, @Param("q") OwnerRoomRecord q);
}
