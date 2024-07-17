package com.bruce.dufs;

import java.io.File;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.UUID;

/**
 * @date 2024/7/17
 */
public class FileUtils {

    static String DEFAULT_MIME_TYPE = "application/octet-stream";

    public static String getMimeType(String fileName){
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String content = fileNameMap.getContentTypeFor(fileName);
        return content == null ? DEFAULT_MIME_TYPE : content;
    }

    public static void init(String uploadPath){
        File dir = new File(uploadPath);
        if(!dir.exists()){
            dir.mkdir();
        }
        for (int i = 0; i < 256; i++) {
            String subdir = String.format("%02x", i);
            File file = new File(uploadPath + "/" + subdir);
            if(!file.exists()){
                file.mkdir();
            }
        }
    }

    public static String getUUIDFile(String originalFilename) {
        return UUID.randomUUID().toString() + getExt(originalFilename);
    }

    public static String getSubDir(String file) {
        return file.substring(0,2);
    }

    public static String getExt(String originalFilename) {
        return originalFilename.substring(originalFilename.lastIndexOf("."));
    }
}
