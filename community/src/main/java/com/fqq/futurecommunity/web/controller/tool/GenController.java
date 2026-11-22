package com.fqq.futurecommunity.web.controller.tool;

import com.fqq.futurecommunity.common.core.domain.BaseResponse;
import com.fqq.futurecommunity.common.core.page.PageResult;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

/**
 * Fallback endpoints for the front-end code generator module.
 */
@RestController
@RequestMapping("/tool/gen")
public class GenController {

    private static final String NOT_CONFIGURED = "Code generator backend is not configured.";

    @GetMapping("/list")
    @PreAuthorize("@pe.hasPerms('tool:gen:list')")
    public PageResult<Map<String, Object>> list() {
        return PageResult.SUCCESS(0, Collections.emptyList());
    }

    @GetMapping("/db/list")
    @PreAuthorize("@pe.hasPerms('tool:gen:list')")
    public PageResult<Map<String, Object>> dataList() {
        return PageResult.SUCCESS(0, Collections.emptyList());
    }

    @GetMapping("/{tableId}")
    @PreAuthorize("@pe.hasPerms('tool:gen:query')")
    public BaseResponse<Void> getInfo(@PathVariable Long tableId) {
        return BaseResponse.fail(NOT_CONFIGURED);
    }

    @PutMapping
    @PreAuthorize("@pe.hasPerms('tool:gen:edit')")
    public BaseResponse<Void> edit(@RequestBody Map<String, Object> table) {
        return BaseResponse.fail(NOT_CONFIGURED);
    }

    @PostMapping("/importTable")
    @PreAuthorize("@pe.hasPerms('tool:gen:import')")
    public BaseResponse<Void> importTable(@RequestParam(required = false) String tables) {
        return BaseResponse.fail(NOT_CONFIGURED);
    }

    @GetMapping("/preview/{tableId}")
    @PreAuthorize("@pe.hasPerms('tool:gen:preview')")
    public BaseResponse<Void> preview(@PathVariable Long tableId) {
        return BaseResponse.fail(NOT_CONFIGURED);
    }

    @DeleteMapping("/{tableId}")
    @PreAuthorize("@pe.hasPerms('tool:gen:remove')")
    public BaseResponse<Void> remove(@PathVariable Long tableId) {
        return BaseResponse.fail(NOT_CONFIGURED);
    }

    @GetMapping("/genCode/{tableName}")
    @PreAuthorize("@pe.hasPerms('tool:gen:code')")
    public BaseResponse<Void> genCode(@PathVariable String tableName) {
        return BaseResponse.fail(NOT_CONFIGURED);
    }

    @GetMapping("/synchDb/{tableName}")
    @PreAuthorize("@pe.hasPerms('tool:gen:edit')")
    public BaseResponse<Void> synchDb(@PathVariable String tableName) {
        return BaseResponse.fail(NOT_CONFIGURED);
    }

    @GetMapping("/batchGenCode")
    @PreAuthorize("@pe.hasPerms('tool:gen:code')")
    public void batchGenCode(@RequestParam(required = false) String tables,
                             HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":501,\"msg\":\"" + NOT_CONFIGURED + "\"}");
    }
}
