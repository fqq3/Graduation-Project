package com.fqq.futurecommunity.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.system.domain.SysJob;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysJobMapper extends BaseMapper<SysJob> {

    Page<SysJob> selectJobPage(Page<SysJob> page, @Param("job") SysJob job);

    List<SysJob> selectJobList(@Param("job") SysJob job);
}
