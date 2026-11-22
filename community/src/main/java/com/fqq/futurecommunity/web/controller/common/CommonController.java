package com.fqq.futurecommunity.web.controller.common;

import com.fqq.futurecommunity.common.utils.ChainedMap;
import com.fqq.futurecommunity.common.utils.FileStorageUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@RestController
@RequestMapping("/common")
public class CommonController {

    @PostMapping("/upload")
    public ChainedMap upload(@RequestParam("file") MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return ChainedMap.create().set("code", 500).set("msg", "Upload file cannot be empty");
        }

        String extension = FileStorageUtils.normalizeExtension(StringUtils.getFilenameExtension(file.getOriginalFilename()));
        String fileName = UUID.randomUUID().toString().replace("-", "") + extension;
        Path uploadDir = FileStorageUtils.getUploadDir();
        Files.createDirectories(uploadDir);
        Files.copy(file.getInputStream(), uploadDir.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);

        String url = FileStorageUtils.buildProfileUrl("upload", fileName);
        return ChainedMap.create()
                .set("code", 200)
                .set("msg", "success")
                .set("url", url)
                .set("fileName", fileName);
    }

    @GetMapping("/download")
    public void download(@RequestParam("fileName") String fileName,
                         @RequestParam(value = "delete", defaultValue = "false") boolean delete,
                         HttpServletResponse response) throws IOException {
        if (!FileStorageUtils.isSafeFileName(fileName)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Illegal file name");
            return;
        }

        Path filePath = FileStorageUtils.getDownloadDir().resolve(fileName).normalize();
        if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
            return;
        }

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));

        try (var inputStream = Files.newInputStream(filePath);
             var outputStream = response.getOutputStream()) {
            inputStream.transferTo(outputStream);
            outputStream.flush();
        } finally {
            if (delete) {
                Files.deleteIfExists(filePath);
            }
        }
    }
}
