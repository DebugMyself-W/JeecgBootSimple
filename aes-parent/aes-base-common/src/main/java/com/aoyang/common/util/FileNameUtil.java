package com.aoyang.common.util;

import org.springframework.util.StringUtils;

/**
 * 文件名工具类
 * @author went
 */
public class FileNameUtil {

    private static final String CONNECTOR = "_";

    private FileNameUtil() {}

    /**
     * 文件名追加时间戳
     * @param fileName 文件名
     * @return
     */
    public static String appendTimestamp(String fileName) {

        if (fileName == null || StringUtils.isEmpty(fileName)) {
            return null;
        }

        if (!fileName.contains(".")) {
            return fileName + CONNECTOR + System.currentTimeMillis();
        }

        return fileName.substring(0, fileName.lastIndexOf("."))
                + CONNECTOR + System.currentTimeMillis()
                + fileName.substring(fileName.lastIndexOf("."));
    }
}
