package com.bruce.dufs;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import static com.bruce.dufs.FileUtils.getMimeType;
import static com.bruce.dufs.FileUtils.getUUIDFile;

/**
 * @date 2024/7/10
 */
@RestController
public class FileController {

    @Value("${dufs.path}")
    private String uploadPath;
    @Value("${dufs.backupUrl}")
    private String backupUrl;

    @Autowired
    HttpSyncer httpSyncer;

    @SneakyThrows
    @PostMapping("/upload")
    public String upload(@RequestParam("file")MultipartFile file, HttpServletRequest request){
        boolean needSync = false;
        String filename = request.getHeader(HttpSyncer.XFILENAME);
        if(filename == null || filename.isEmpty()){
            needSync = true;
            filename = getUUIDFile(file.getOriginalFilename());
        }
        String subDir = FileUtils.getSubDir(filename);
        File dest = new File(uploadPath + "/"  + subDir +"/" + filename);
        file.transferTo(dest);

        // 同步文件到backup
        if(needSync){
            httpSyncer.sync(dest,backupUrl);
        }

        return filename;
    }



    @RequestMapping("/download")
    public void download(@RequestParam("name") String name, HttpServletResponse response){
        String subDir = FileUtils.getSubDir(name);
        String path = uploadPath + "/" + subDir + "/" + name;
        File file = new File(path);
        try {
            FileInputStream fis = new FileInputStream(file);
            InputStream in = new BufferedInputStream(fis);
            byte[] buffer = new byte[16*1024];

            // 加一些response的头
            response.setCharacterEncoding("UTF-8");
            response.setContentType(getMimeType(name));
//            response.setHeader("Content-Disposition","attachment;filename=" + name);
            response.setHeader("Content-Length", String.valueOf(file.length()));

            // 读取文件信息，并逐段输出
            ServletOutputStream outputStream = response.getOutputStream();
            while (in.read(buffer) != -1){
                outputStream.write(buffer);
            }
            outputStream.flush();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
