package com.fqq.futurecommunity.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.community.domain.ComplaintSuggest;
import com.fqq.futurecommunity.community.domain.dto.ComplaintSuggestDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ComplaintSuggestMapper extends BaseMapper<ComplaintSuggest> {
    @Select("""
    <script>
    select s.*, ow.owner_real_name as ownerRealName, ow.owner_phone_number as ownerPhoneNumber,
           ow.owner_nickname as ownerNickname, ow.owner_portrait as ownerPortrait,
           c.community_name as communityName
    from complaint_suggest s
    left join owner ow on ow.owner_id = s.user_id
    left join community c on c.community_id = s.community_id
    <where>
      <if test="q != null and q.complaintSuggestId != null">
        and s.complaint_suggest_id = #{q.complaintSuggestId}
      </if>
      <if test="q != null and q.communityId != null">
        and s.community_id = #{q.communityId}
      </if>
      <if test="q != null and q.complaintSuggestType != null and q.complaintSuggestType != ''">
        and (
          s.complaint_suggest_type = #{q.complaintSuggestType}
          or (#{q.complaintSuggestType} = 'Complaint' and s.complaint_suggest_type = '投诉')
          or (#{q.complaintSuggestType} = 'Suggest' and s.complaint_suggest_type = '建议')
        )
      </if>
      <if test="q != null and q.complaintSuggestContent != null and q.complaintSuggestContent != ''">
        and s.complaint_suggest_content like concat('%', #{q.complaintSuggestContent}, '%')
      </if>
      <if test="q != null and q.userId != null">
        and s.user_id = #{q.userId}
      </if>
    </where>
    order by s.create_time desc
    </script>
    """)
    Page<ComplaintSuggestDto> selectComplaintSuggestList(Page<ComplaintSuggestDto> page, @Param("q") ComplaintSuggest q);
}
