package com.fqq.futurecommunity.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.community.domain.Visitor;
import com.fqq.futurecommunity.community.domain.dto.VisitorDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface VisitorMapper extends BaseMapper<Visitor> {
    @Select("""
    <script>
    select v.*, c.community_name as communityName,
           ow.owner_real_name as ownerRealName, ow.owner_phone_number as ownerPhoneNumber,
           ow.owner_nickname as ownerNickname, ow.owner_portrait as ownerPortrait
    from visitor v
    left join community c on c.community_id = v.community_id
    left join owner ow on ow.owner_id = v.create_by_id
    <where>
      <if test="q != null and q.visitorId != null">
        and v.visitor_id = #{q.visitorId}
      </if>
      <if test="q != null and q.communityId != null">
        and v.community_id = #{q.communityId}
      </if>
      <if test="q != null and q.visitorName != null and q.visitorName != ''">
        and v.visitor_name like concat('%', #{q.visitorName}, '%')
      </if>
      <if test="q != null and q.visitorPhoneNumber != null and q.visitorPhoneNumber != ''">
        and v.visitor_phone_number like concat('%', #{q.visitorPhoneNumber}, '%')
      </if>
      <if test="q != null and q.visitorDate != null">
        and v.visitor_date = #{q.visitorDate}
      </if>
      <if test="q != null and q.createById != null">
        and v.create_by_id = #{q.createById}
      </if>
      <if test="q != null and q.createByOpenId != null and q.createByOpenId != ''">
        and v.create_by_open_id = #{q.createByOpenId}
      </if>
    </where>
    order by v.create_time desc
    </script>
    """)
    Page<VisitorDto> selectVisitorList(Page<VisitorDto> page, @Param("q") Visitor q);
}
