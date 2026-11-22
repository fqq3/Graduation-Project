package com.fqq.futurecommunity.common.utils;

import com.alibaba.excel.EasyExcel;
import jakarta.servlet.http.HttpServletResponse;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class ExcelUtils {

    private ExcelUtils() {
    }

    public static void exportExcel(List<?> list,
                                   Class<?> clazz,
                                   String fileName,
                                   HttpServletResponse response,
                                   String sheetName) {
        try {
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");

            String encodeFileName = URLEncoder.encode(fileName, "UTF-8");
            response.setHeader("Content-disposition",
                    "attachment;filename=" + encodeFileName + ".xlsx");

            EasyExcel.write(response.getOutputStream(), clazz)
                    .sheet(sheetName)
                    .doWrite(list);
        } catch (Exception e) {
            throw new RuntimeException("Export excel failed", e);
        }
    }

    public static void writeExcelToFile(List<?> list,
                                        Class<?> clazz,
                                        Path filePath,
                                        String sheetName) {
        try {
            Files.createDirectories(filePath.getParent());
            EasyExcel.write(filePath.toFile(), clazz)
                    .sheet(sheetName)
                    .doWrite(list);
        } catch (Exception e) {
            throw new RuntimeException("Write excel file failed", e);
        }
    }

    public static <T> List<T> readExcel(InputStream inputStream, Class<T> clazz) {
        try {
            return EasyExcel.read(inputStream, clazz, null)
                    .sheet()
                    .doReadSync();
        } catch (Exception e) {
            throw new RuntimeException("Read excel failed", e);
        }
    }
}
