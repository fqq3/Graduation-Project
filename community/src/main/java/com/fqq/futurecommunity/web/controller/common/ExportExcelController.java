package com.fqq.futurecommunity.web.controller.common;

import com.fqq.futurecommunity.common.core.domain.BaseController;
import com.fqq.futurecommunity.common.utils.ChainedMap;
import com.fqq.futurecommunity.common.utils.ExcelUtils;
import com.fqq.futurecommunity.common.utils.FileStorageUtils;
import com.fqq.futurecommunity.community.domain.Community;
import com.fqq.futurecommunity.community.domain.dto.CommunityDto;
import com.fqq.futurecommunity.community.domain.dto.CommunityExcelDto;
import com.fqq.futurecommunity.community.service.CommunityService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: fqq
 * @Date: 2026/3/21 - 03 - 21 - 3:02
 * @Description: com.fqq.futurecommunity.web.controller.common
 * @version: 1.0
 */
@RestController
@RequestMapping("/exportExcel")
public class ExportExcelController extends BaseController {

    @Resource
    private CommunityService communityService;

    /**
     * 导出小区数据
     * @param community
     * @return: file name for common download endpoint
     */
    @PreAuthorize("@pe.hasPerms('system:community:export')")
    @GetMapping("/exportCommunityExcel")
    public ChainedMap exportExcel(Community community) {
        //查询全部数据（不分页）
        List<CommunityDto> list = communityService.selectCommunityListNoPage(community);

        List<CommunityExcelDto> excelDtoList = list.stream().map(
                communityDto -> {
                    CommunityExcelDto excelDto = new CommunityExcelDto();
                    excelDto.setCommunityId(communityDto.getCommunityId());
                    excelDto.setCommunityName(communityDto.getCommunityName());
                    excelDto.setCommunityCode(communityDto.getCommunityCode());
                    excelDto.setCommunityProvinceName(communityDto.getCommunityProvinceName());
                    excelDto.setCommunityCityName(communityDto.getCommunityCityName());
                    excelDto.setCommunityTownName(communityDto.getCommunityTownName());
                    excelDto.setCreateTime(communityDto.getCreateTime());
                    excelDto.setRemark(communityDto.getRemark());

                    return excelDto;
                }
        ).collect(Collectors.toList());
        String fileName = FileStorageUtils.generateFileName("community", ".xlsx");
        Path filePath = FileStorageUtils.getDownloadDir().resolve(fileName);
        ExcelUtils.writeExcelToFile(excelDtoList, CommunityExcelDto.class, filePath, "Community");
        return ChainedMap.create().set("code", 200).set("msg", fileName);
    }
}
