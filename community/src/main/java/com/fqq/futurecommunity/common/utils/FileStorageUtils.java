package com.fqq.futurecommunity.common.utils;

import com.fqq.futurecommunity.common.constant.Constants;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class FileStorageUtils {

    private static final String DOWNLOAD_DIR = "download";
    private static final String UPLOAD_DIR = "upload";
    private static final String AVATAR_DIR = "avatar";
    private static final DateTimeFormatter FILE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private FileStorageUtils() {
    }

    public static Path getProfileRoot() {
        return Paths.get(System.getProperty("user.dir"), Constants.RESOURCE_PREFIX.replaceFirst("^/", ""))
                .toAbsolutePath()
                .normalize();
    }

    public static Path getDownloadDir() {
        return getProfileRoot().resolve(DOWNLOAD_DIR);
    }

    public static Path getUploadDir() {
        return getProfileRoot().resolve(UPLOAD_DIR);
    }

    public static Path getAvatarDir() {
        return getProfileRoot().resolve(AVATAR_DIR);
    }

    public static String buildProfileUrl(String folder, String fileName) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(Constants.RESOURCE_PREFIX)
                .path("/")
                .path(folder)
                .path("/")
                .path(fileName)
                .build()
                .getPath();
    }

    public static String generateFileName(String prefix, String extension) {
        String safeExtension = normalizeExtension(extension);
        return prefix + "_" + LocalDateTime.now().format(FILE_TIME_FORMATTER) + safeExtension;
    }

    public static boolean isSafeFileName(String fileName) {
        return StringUtils.hasText(fileName)
                && !fileName.contains("..")
                && !fileName.contains("/")
                && !fileName.contains("\\");
    }

    public static String normalizeExtension(String extension) {
        if (!StringUtils.hasText(extension)) {
            return "";
        }
        return extension.startsWith(".") ? extension : "." + extension;
    }
}
