package com.fqq.futurecommunity.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.community.domain.Owner;
import com.fqq.futurecommunity.community.domain.dto.OwnerDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface OwnerMapper extends BaseMapper<Owner> {
    @Select("""
    <script>
    select
      o.owner_id as ownerId,
      o.owner_nickname as ownerNickname,
      o.owner_real_name as ownerRealName,
      o.owner_gender as ownerGender,
      o.owner_age as ownerAge,
      o.owner_id_card as ownerIdCard,
      o.owner_phone_number as ownerPhoneNumber,
      o.owner_open_id as ownerOpenId,
      o.owner_wechat_id as ownerWechatId,
      o.owner_qq_number as ownerQqNumber,
      o.owner_birthday as ownerBirthday,
      o.owner_portrait as ownerPortrait,
      o.owner_signature as ownerSignature,
      o.owner_status as ownerStatus,
      o.owner_logon_mode as ownerLogonMode,
      o.owner_type as ownerType,
      o.owner_password as ownerPassword,
      o.create_by as createBy,
      o.create_time as createTime,
      o.update_by as updateBy,
      o.update_time as updateTime,
      o.remark as remark,
      orr.owner_room_id as ownerRoomId,
      coalesce(orr.community_id, r.community_id) as communityId,
      c.community_name as communityName,
      coalesce(orr.building_id, r.building_id) as buildingId,
      b.building_name as buildingName,
      coalesce(orr.unit_id, r.unit_id) as unitId,
      u.unit_name as unitName,
      coalesce(orr.room_id, r.room_id) as roomId,
      r.room_name as roomName,
      orr.room_status as roomStatus
    from owner o
    left join owner_room orr
      on orr.owner_id = o.owner_id
      and orr.owner_room_id = (
        select max(orr2.owner_room_id)
        from owner_room orr2
        where orr2.owner_id = o.owner_id
      )
    left join room r on r.room_id = orr.room_id
    left join community c on c.community_id = coalesce(orr.community_id, r.community_id)
    left join building b on b.building_id = coalesce(orr.building_id, r.building_id)
    left join unit u on u.unit_id = coalesce(orr.unit_id, r.unit_id)
    <where>
      <if test="q != null and q.ownerId != null">
        and o.owner_id = #{q.ownerId}
      </if>
      <if test="q != null and q.communityId != null">
        and coalesce(orr.community_id, r.community_id) = #{q.communityId}
      </if>
      <if test="q != null and q.ownerRealName != null and q.ownerRealName != ''">
        and o.owner_real_name like concat('%', #{q.ownerRealName}, '%')
      </if>
      <if test="q != null and q.ownerNickname != null and q.ownerNickname != ''">
        and o.owner_nickname like concat('%', #{q.ownerNickname}, '%')
      </if>
      <if test="q != null and q.ownerPhoneNumber != null and q.ownerPhoneNumber != ''">
        and o.owner_phone_number like concat('%', #{q.ownerPhoneNumber}, '%')
      </if>
      <if test="q != null and q.ownerIdCard != null and q.ownerIdCard != ''">
        and o.owner_id_card like concat('%', #{q.ownerIdCard}, '%')
      </if>
      <if test="q != null and q.ownerGender != null and q.ownerGender != ''">
        and o.owner_gender = #{q.ownerGender}
      </if>
      <if test="q != null and q.ownerStatus != null and q.ownerStatus != ''">
        and o.owner_status = #{q.ownerStatus}
      </if>
      <if test="q != null and q.ownerType != null and q.ownerType != ''">
        and o.owner_type = #{q.ownerType}
      </if>
      <if test="q != null and q.ownerLogonMode != null and q.ownerLogonMode != ''">
        and o.owner_logon_mode = #{q.ownerLogonMode}
      </if>
    </where>
    order by o.create_time desc, o.owner_id desc
    </script>
    """)
    Page<OwnerDto> selectOwnerList(Page<OwnerDto> page, @Param("q") Owner q);
}
