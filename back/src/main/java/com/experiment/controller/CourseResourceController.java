package com.experiment.controller;

import com.experiment.pojo.CourseResource;
import com.experiment.result.Result;
import com.experiment.mapper.CourseResourceMapper;
import com.experiment.mapper.StudentAnnotationMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/course-resources")
@CrossOrigin
public class CourseResourceController {

    @Autowired
    private CourseResourceMapper courseResourceMapper;
    
    @Autowired
    private StudentAnnotationMapper studentAnnotationMapper;

    /**
     * 获取课程资源列表
     */
    @GetMapping("/course/{courseId}")
    public Result<List<CourseResource>> getCourseResources(@PathVariable Long courseId) {
        try {
            List<CourseResource> resources = courseResourceMapper.findByCourseId(courseId);
            return Result.success("获取课程资源成功", resources);
        } catch (Exception e) {
            log.error("获取课程资源失败", e);
            return Result.error("获取课程资源失败: " + e.getMessage());
        }
    }

    /**
     * 获取教师上传的资源列表
     */
    @GetMapping("/teacher/{teacherId}")
    public Result<List<CourseResource>> getTeacherResources(@PathVariable Long teacherId) {
        try {
            List<CourseResource> resources = courseResourceMapper.findByTeacherId(teacherId);
            return Result.success("获取教师资源成功", resources);
        } catch (Exception e) {
            log.error("获取教师资源失败", e);
            return Result.error("获取教师资源失败: " + e.getMessage());
        }
    }

    /**
     * 获取资源详情
     */
    @GetMapping("/{id}")
    public Result<CourseResource> getResourceById(@PathVariable Long id) {
        try {
            CourseResource resource = courseResourceMapper.findById(id);
            if (resource == null) {
                return Result.error("资源不存在");
            }
            return Result.success("获取资源详情成功", resource);
        } catch (Exception e) {
            log.error("获取资源详情失败", e);
            return Result.error("获取资源详情失败: " + e.getMessage());
        }
    }

    /**
     * 上传文件资源
     */
    @PostMapping("/upload")
    public Result<CourseResource> uploadResource(
            @RequestParam("file") MultipartFile file,
            @RequestParam("courseId") Long courseId,
            @RequestParam("teacherId") Long teacherId,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description) {
        
        try {
            if (file.isEmpty()) {
                return Result.error("文件不能为空");
            }

            // 创建上传目录 - 使用项目根目录下的uploads文件夹
            String projectRoot = System.getProperty("user.dir");
            String uploadDir = projectRoot + File.separator + "uploads" + File.separator + "course" + courseId;
            Path uploadPath = Paths.get(uploadDir);
            
            // 确保目录创建成功
            Files.createDirectories(uploadPath);
            log.info("创建上传目录: " + uploadPath.toString());

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isEmpty()) {
                return Result.error("文件名不能为空");
            }
            
            String fileExtension = "";
            if (originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            
            String fileName = UUID.randomUUID().toString() + fileExtension;
            Path filePath = uploadPath.resolve(fileName);
            
            log.info("准备保存文件到: " + filePath.toString());

            // 保存文件
            file.transferTo(filePath.toFile());
            log.info("文件保存成功: " + filePath.toString());

            // 确定文件类型
            String fileType = getFileType(originalFilename);

            // 创建资源记录
            CourseResource resource = new CourseResource();
            resource.setCourseId(courseId);
            resource.setTeacherId(teacherId);
            resource.setTitle(title);
            resource.setDescription(description);
            resource.setFileName(originalFilename);
            // 设置相对URL路径
            String relativeUrl = "/uploads/course" + courseId + "/" + fileName;
            resource.setFileUrl(relativeUrl);
            resource.setFileType(fileType);
            resource.setFileSize(file.getSize());
            resource.setDownloadCount(0);
            resource.setStatus("published");

            int result = courseResourceMapper.insert(resource);
            if (result > 0) {
                return Result.success("文件上传成功", resource);
            } else {
                return Result.error("文件上传失败");
            }

        } catch (Exception e) {
            log.error("文件上传失败", e);
            if (e instanceof IOException) {
                return Result.error("文件操作失败: " + e.getMessage());
            } else {
                return Result.error("上传资源失败: " + e.getMessage());
            }
        }
    }

    /**
     * 更新资源信息
     */
    @PutMapping("/{id}")
    public Result<String> updateResource(@PathVariable Long id, @RequestBody CourseResource resource) {
        try {
            resource.setId(id);
            int result = courseResourceMapper.update(resource);
            if (result > 0) {
                return Result.success("更新资源成功");
            } else {
                return Result.error("更新资源失败");
            }
        } catch (Exception e) {
            log.error("更新资源失败", e);
            return Result.error("更新资源失败: " + e.getMessage());
        }
    }

    /**
     * 删除资源（级联删除关联数据）
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteResource(@PathVariable Long id) {
        try {
            log.info("开始删除资源，资源ID: {}", id);
            
            // 1. 先获取资源信息
            CourseResource resource = courseResourceMapper.findById(id);
            if (resource == null) {
                log.warn("资源不存在，ID: {}", id);
                return Result.error("资源不存在");
            }
            
            log.info("资源信息 - 标题: {}, 文件: {}", resource.getTitle(), resource.getFileName());
            
            // 2. 删除所有关联的学生标注（级联删除）
            try {
                int annotationCount = studentAnnotationMapper.deleteByResourceId(id);
                log.info("删除关联标注成功，数量: {}", annotationCount);
            } catch (Exception e) {
                log.warn("删除关联标注时出错（可能没有标注）: {}", e.getMessage());
                // 继续执行，即使没有标注也要删除资源
            }
            
            // 3. 删除数据库记录
            int result = courseResourceMapper.deleteById(id);
            if (result > 0) {
                log.info("数据库记录删除成功，资源ID: {}", id);
                
                // 4. 尝试删除物理文件
                try {
                    String projectRoot = System.getProperty("user.dir");
                    String filePath = projectRoot + resource.getFileUrl().replace("/", File.separator);
                    Path path = Paths.get(filePath);
                    
                    if (Files.exists(path)) {
                        Files.delete(path);
                        log.info("物理文件删除成功: {}", filePath);
                    } else {
                        log.warn("物理文件不存在: {}", filePath);
                    }
                } catch (Exception e) {
                    log.error("删除物理文件失败，但数据库记录已删除", e);
                    // 即使物理文件删除失败，也返回成功（数据库记录已删除）
                }
                
                return Result.success("删除资源成功");
            } else {
                log.error("删除资源失败，数据库操作返回0");
                return Result.error("删除资源失败");
            }
        } catch (Exception e) {
            log.error("删除资源失败，资源ID: {}", id, e);
            return Result.error("删除资源失败: " + e.getMessage());
        }
    }

    /**
     * 下载资源（更新下载次数）
     */
    @PostMapping("/{id}/download")
    public Result<String> downloadResource(@PathVariable Long id) {
        try {
            courseResourceMapper.updateDownloadCount(id);
            return Result.success("下载次数已更新");
        } catch (Exception e) {
            log.error("更新下载次数失败", e);
            return Result.error("更新下载次数失败: " + e.getMessage());
        }
    }

    /**
     * 搜索资源
     */
    @GetMapping("/search")
    public Result<List<CourseResource>> searchResources(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) String fileType) {
        try {
            List<CourseResource> resources = courseResourceMapper.searchResources(keyword, courseId, fileType);
            return Result.success("搜索资源成功", resources);
        } catch (Exception e) {
            log.error("搜索资源失败", e);
            return Result.error("搜索资源失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有资源（管理员）
     */
    @GetMapping("/admin/all")
    public Result<List<CourseResource>> getAllResources() {
        try {
            List<CourseResource> resources = courseResourceMapper.findAll();
            return Result.success("获取所有资源成功", resources);
        } catch (Exception e) {
            log.error("获取所有资源失败", e);
            return Result.error("获取所有资源失败: " + e.getMessage());
        }
    }

    /**
     * 读取文件内容（用于文档预览）
     */
    @GetMapping("/{id}/content")
    public Result<String> getResourceContent(@PathVariable Long id) {
        try {
            log.info("开始读取资源内容，资源ID: {}", id);
            
            CourseResource resource = courseResourceMapper.findById(id);
            if (resource == null) {
                log.error("资源不存在，ID: {}", id);
                return Result.error("资源不存在");
            }
            
            log.info("资源信息 - 标题: {}, 文件URL: {}", resource.getTitle(), resource.getFileUrl());

            // 获取文件路径
            String projectRoot = System.getProperty("user.dir");
            String filePath = projectRoot + resource.getFileUrl().replace("/", File.separator);
            log.info("项目根目录: {}", projectRoot);
            log.info("完整文件路径: {}", filePath);
            
            Path path = Paths.get(filePath);

            if (!Files.exists(path)) {
                log.error("文件不存在: {}", filePath);
                return Result.error("文件不存在: " + filePath);
            }

            // 读取文件内容
            log.info("开始读取文件内容，文件大小: {} 字节", Files.size(path));
            String content = new String(Files.readAllBytes(path), "UTF-8");
            log.info("文件内容读取成功，内容长度: {} 字符", content.length());
            
            return Result.success("读取文件内容成功", content);

        } catch (Exception e) {
            log.error("读取文件内容失败，资源ID: {}", id, e);
            return Result.error("读取文件内容失败: " + e.getMessage());
        }
    }

    /**
     * 根据文件扩展名确定文件类型
     */
    private String getFileType(String filename) {
        if (filename == null) return "unknown";
        
        String extension = filename.toLowerCase().substring(filename.lastIndexOf(".") + 1);
        switch (extension) {
            case "ppt":
            case "pptx":
                return "ppt";
            case "pdf":
                return "pdf";
            case "doc":
            case "docx":
                return "doc";
            case "mp4":
            case "avi":
            case "mov":
                return "video";
            case "jpg":
            case "jpeg":
            case "png":
            case "gif":
                return "image";
            case "txt":
            case "md":
                return "text";
            default:
                return "other";
        }
    }
} 