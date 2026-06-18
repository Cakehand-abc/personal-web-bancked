package com.blog.controller;

import com.blog.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/upload")
public class UploadController {

    // 从配置文件读取上传路径
    @Value("${blog.upload.path}")
    private String uploadPath;

    @PostMapping
    public Result<String> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }

        try {
            // 1. 确保目录存在
            File directory = new File(uploadPath);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // 2. 生成一个全宇宙唯一的随机文件名，防止别人上传同名文件把你的覆盖了
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String newFilename = UUID.randomUUID().toString().replace("-", "") + extension;

            // 3. 把文件存入本地硬盘
            File destFile = new File(directory.getAbsolutePath() + File.separator + newFilename);
            file.transferTo(destFile);

            // 4. 拼接出可以在浏览器直接访问的 URL 链接并返回给前端
            // 默认后端跑在 8080 端口，所以拼接 http://localhost:8080/uploads/文件名
            // 以后上线到了服务器，这里换成您的真实域名即可
            String fileUrl = "http://localhost:8080/uploads/" + newFilename;
            return Result.success(fileUrl);

        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }
}
