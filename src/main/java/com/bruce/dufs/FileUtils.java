package com.bruce.dufs;

import com.alibaba.fastjson.JSON;
import jakarta.servlet.ServletOutputStream;
import lombok.SneakyThrows;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
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

    @SneakyThrows
    public static void writeMeta(File metaFile, FileMeta meta) {
        String json = JSON.toJSONString(meta);
        Files.writeString(Paths.get(metaFile.getAbsolutePath()), json,
                StandardOpenOption.CREATE, StandardOpenOption.WRITE);
    }

    @SneakyThrows
    public static void writeString(File metaFile, String content) {
        Files.writeString(Paths.get(metaFile.getAbsolutePath()), content,
                StandardOpenOption.CREATE, StandardOpenOption.WRITE);
    }

    public static void download(String downloadUrl, File file) {
        System.out.println(" ===>>> download file: " + file.getAbsolutePath());
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<?> entity = new HttpEntity<>(new HttpHeaders());
        ResponseEntity<Resource> exchange = restTemplate.exchange(downloadUrl, HttpMethod.GET, entity, Resource.class);

        try {
            InputStream fis = new BufferedInputStream(exchange.getBody().getInputStream());
            byte[] buffer = new byte[16*1024];

            FileOutputStream outputStream = new FileOutputStream(file);
            while (fis.read(buffer) != -1){
                outputStream.write(buffer);
            }
            outputStream.flush();
            outputStream.close();
            fis.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
