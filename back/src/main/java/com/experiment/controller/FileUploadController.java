package com.experiment.controller;

import com.experiment.result.Result;
import com.experiment.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@CrossOrigin
public class FileUploadController {

    @Autowired
    private AliOssUtil aliOssUtil;

    /**
     * 通用文件上传接口
     * 上传文件到阿里云OSS
     */
    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        log.info("文件上传请求，文件名：{}", file.getOriginalFilename());

        try {
            // 获取原始文件名
            String originalFilename = file.getOriginalFilename();
            
            // 生成唯一文件名，避免重复
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String objectName = "homework/" + UUID.randomUUID().toString() + extension;

            // 上传文件到OSS
            String url = aliOssUtil.upload(file.getBytes(), objectName);
            
            log.info("文件上传成功，URL：{}", url);
            return Result.success("文件上传成功", url);

        } catch (IOException e) {
            log.error("文件上传失败", e);
            return Result.error("文件上传失败：" + e.getMessage());
        }
    }
}

