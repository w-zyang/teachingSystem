package com.experiment.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.experiment.pojo.ClassSummary;
import com.experiment.pojo.ClassSummaryDTO;
import com.experiment.result.Result;
import com.experiment.service.ClassSummaryService;

import lombok.extern.slf4j.Slf4j;

/**
 * 课堂重点整理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/class-summary")
public class ClassSummaryController {

    @Autowired
    private ClassSummaryService classSummaryService;

    /**
     * 创建课堂总结
     */
    @PostMapping("/create")
    public Result<ClassSummary> createClassSummary(@RequestBody ClassSummaryDTO dto) {
        log.info("创建课堂总结，课程ID: {}, 教师ID: {}", dto.getCourseId(), dto.getTeacherId());
        
        try {
            ClassSummary classSummary = classSummaryService.createClassSummary(dto);
            return Result.success("创建课堂总结成功", classSummary);
        } catch (Exception e) {
            log.error("创建课堂总结失败", e);
            return Result.error("创建课堂总结失败: " + e.getMessage());
        }
    }

    /**
     * 上传录音文件
     */
    @PostMapping("/upload-audio")
    public Result<String> uploadAudioFile(@RequestParam("audioFile") MultipartFile audioFile,
                                         @RequestParam("courseId") Long courseId,
                                         @RequestParam(value = "summaryId", required = false) Long summaryId,
                                         @RequestParam(value = "audioDuration", required = false) Integer audioDuration) {
        log.info("上传录音文件，课程ID: {}, 总结ID: {}, 文件大小: {}, 时长: {}秒", 
                courseId, summaryId, audioFile.getSize(), audioDuration);
        
        try {
            if (audioFile.isEmpty()) {
                return Result.error("录音文件不能为空");
            }
            
            String filePath = classSummaryService.uploadAudioFile(audioFile, courseId, summaryId, audioDuration);
            return Result.success("录音文件上传成功", filePath);
        } catch (Exception e) {
            log.error("上传录音文件失败", e);
            return Result.error("上传录音文件失败: " + e.getMessage());
        }
    }

    /**
     * 处理录音（语音转文字）
     */
    @PostMapping("/process-audio")
    public Result<String> processAudioToText(@RequestBody Map<String, Object> request) {
        Long summaryId = ((Number) request.get("summaryId")).longValue();
        // 支持 audioUrl 和 audioFilePath 两种参数名（兼容前端）
        String audioPath = (String) (request.get("audioFilePath") != null ? 
            request.get("audioFilePath") : request.get("audioUrl"));
        
        // 清理路径：去除首尾的引号和空格
        if (audioPath != null) {
            audioPath = audioPath.trim();
            // 去除首尾的引号（单引号或双引号）
            if ((audioPath.startsWith("\"") && audioPath.endsWith("\"")) ||
                (audioPath.startsWith("'") && audioPath.endsWith("'"))) {
                audioPath = audioPath.substring(1, audioPath.length() - 1).trim();
            }
        }
        
        log.info("========== 开始处理录音转文字 ==========");
        log.info("总结ID: {}, 音频文件路径: {}", summaryId, audioPath);
        
        try {
            if (audioPath == null || audioPath.isEmpty()) {
                return Result.error("音频文件路径不能为空");
        }
        
            String transcriptText = classSummaryService.processAudioToText(summaryId, audioPath);
            log.info("========== 录音转文字成功，文本长度: {} ==========", transcriptText != null ? transcriptText.length() : 0);
            return Result.success("语音转文字成功", transcriptText);
        } catch (Exception e) {
            log.error("========== 录音转文字失败 ==========", e);
            return Result.error("语音转文字失败: " + e.getMessage());
        }
    }
    
    /**
     * 测试接口：验证音频文件路径和OSS上传
     */
    @GetMapping("/test-audio/{summaryId}")
    public Result<Map<String, Object>> testAudioFile(@PathVariable Long summaryId) {
        log.info("========== 测试音频文件 ==========");
        log.info("总结ID: {}", summaryId);
        
        Map<String, Object> result = new HashMap<>();
        try {
            ClassSummary summary = classSummaryService.getSummaryById(summaryId);
            if (summary == null) {
                return Result.error("课堂总结不存在");
            }
            
            String audioFilePath = summary.getAudioFilePath();
            result.put("summaryId", summaryId);
            result.put("audioFilePath", audioFilePath);
            result.put("audioDuration", summary.getAudioDuration());
            
            if (audioFilePath != null) {
                // 检查本地文件
                if (!audioFilePath.startsWith("http://") && !audioFilePath.startsWith("https://")) {
                    java.io.File audioFile = new java.io.File(audioFilePath);
                    result.put("localFileExists", audioFile.exists());
                    result.put("localFileSize", audioFile.exists() ? audioFile.length() : 0);
                    result.put("localFileAbsolutePath", audioFile.getAbsolutePath());
                } else {
                    result.put("isRemoteUrl", true);
                    result.put("remoteUrl", audioFilePath);
                }
            } else {
                result.put("error", "音频文件路径为空");
            }
            
            log.info("测试结果: {}", result);
            return Result.success("测试完成", result);
        } catch (Exception e) {
            log.error("测试失败", e);
            result.put("error", e.getMessage());
            return Result.error("测试失败: " + e.getMessage());
        }
    }

    /**
     * 生成AI重点整理
     */
    @PostMapping("/generate-summary")
    public Result<String> generateSummaryWithAI(@RequestBody Map<String, Object> request) {
        log.info("========== 收到生成AI重点整理请求 ==========");
        log.info("请求体: {}", request);
        
        Long summaryId = ((Number) request.get("summaryId")).longValue();
        String transcriptText = (String) request.get("transcriptText");
        String coursewareContent = (String) request.get("coursewareContent");
        
        log.info("总结ID: {}", summaryId);
        log.info("转录文本长度: {}", transcriptText != null ? transcriptText.length() : 0);
        log.info("课件内容长度: {}", coursewareContent != null ? coursewareContent.length() : 0);
        log.info("课件内容是否为空: {}", coursewareContent == null || coursewareContent.trim().isEmpty());
        
        try {
            String summaryContent = classSummaryService.generateSummaryWithAI(summaryId, transcriptText, coursewareContent);
            log.info("AI重点整理生成成功，返回内容长度: {}", summaryContent.length());
            return Result.success("AI重点整理生成成功", summaryContent);
        } catch (Exception e) {
            log.error("AI重点整理生成失败", e);
            return Result.error("AI重点整理生成失败: " + e.getMessage());
        }
    }

    /**
     * 更新最终文档内容
     */
    @PutMapping("/update-content/{summaryId}")
    public Result<Void> updateFinalContent(@PathVariable Long summaryId,
                                          @RequestBody Map<String, String> request) {
        String finalContent = request.get("finalContent");
        
        log.info("更新最终文档内容，总结ID: {}", summaryId);
        
        try {
            classSummaryService.updateFinalContent(summaryId, finalContent);
            return Result.success("更新文档内容成功");
        } catch (Exception e) {
            log.error("更新文档内容失败", e);
            return Result.error("更新文档内容失败: " + e.getMessage());
        }
    }

    /**
     * 发布课堂总结
     */
    @PutMapping("/publish/{summaryId}")
    public Result<Void> publishClassSummary(@PathVariable Long summaryId) {
        log.info("发布课堂总结，ID: {}", summaryId);
        
        try {
            classSummaryService.publishClassSummary(summaryId);
            return Result.success("发布课堂总结成功");
        } catch (Exception e) {
            log.error("发布课堂总结失败", e);
            return Result.error("发布课堂总结失败: " + e.getMessage());
        }
    }

    /**
     * 获取教师的课堂总结列表
     */
    @GetMapping("/teacher/{teacherId}")
    public Result<List<ClassSummary>> getTeacherSummaries(@PathVariable Long teacherId) {
        log.info("获取教师课堂总结列表，教师ID: {}", teacherId);
        
        try {
            List<ClassSummary> summaries = classSummaryService.getTeacherSummaries(teacherId);
            return Result.success("获取教师课堂总结列表成功", summaries);
        } catch (Exception e) {
            log.error("获取教师课堂总结列表失败", e);
            return Result.error("获取教师课堂总结列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取课程的已发布总结列表
     */
    @GetMapping("/course/{courseId}")
    public Result<List<ClassSummary>> getPublishedSummaries(@PathVariable Long courseId) {
        log.info("获取课程已发布总结列表，课程ID: {}", courseId);
        
        try {
            List<ClassSummary> summaries = classSummaryService.getPublishedSummaries(courseId);
            return Result.success("获取课程已发布总结列表成功", summaries);
        } catch (Exception e) {
            log.error("获取课程已发布总结列表失败", e);
            return Result.error("获取课程已发布总结列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有已发布的总结（学生端）
     */
    @GetMapping("/published")
    public Result<List<ClassSummary>> getAllPublishedSummaries() {
        log.info("========== 获取所有已发布的课堂总结 ==========");
        
        try {
            List<ClassSummary> summaries = classSummaryService.getAllPublishedSummaries();
            log.info("查询到的课堂总结数量: {}", summaries != null ? summaries.size() : 0);
            
            if (summaries != null && !summaries.isEmpty()) {
                for (ClassSummary summary : summaries) {
                    log.info("课堂总结: ID={}, 标题={}, 状态={}, 课程={}, 教师={}", 
                            summary.getId(), 
                            summary.getTitle(), 
                            summary.getStatus(),
                            summary.getCourseName(),
                            summary.getTeacherName());
                }
            } else {
                log.warn("⚠️ 数据库中没有已发布的课堂总结");
            }
            
            return Result.success("获取所有已发布总结成功", summaries);
        } catch (Exception e) {
            log.error("========== 获取所有已发布总结失败 ==========", e);
            return Result.error("获取所有已发布总结失败: " + e.getMessage());
        }
    }

    /**
     * 获取课堂总结详情
     */
    @GetMapping("/detail/{summaryId}")
    public Result<ClassSummary> getSummaryDetail(@PathVariable Long summaryId) {
        log.info("获取课堂总结详情，ID: {}", summaryId);
        
        try {
            ClassSummary summary = classSummaryService.getSummaryById(summaryId);
            if (summary != null) {
                // 增加浏览次数
                classSummaryService.incrementViewCount(summaryId);
                return Result.success("获取课堂总结详情成功", summary);
            } else {
                return Result.error("课堂总结不存在");
            }
        } catch (Exception e) {
            log.error("获取课堂总结详情失败", e);
            return Result.error("获取课堂总结详情失败: " + e.getMessage());
        }
    }

    /**
     * 搜索已发布的课堂总结
     */
    @GetMapping("/search")
    public Result<List<ClassSummary>> searchPublishedSummaries(@RequestParam String keyword) {
        log.info("搜索已发布的课堂总结，关键词: {}", keyword);
        
        try {
            List<ClassSummary> summaries = classSummaryService.searchPublishedSummaries(keyword);
            return Result.success("搜索课堂总结成功", summaries);
        } catch (Exception e) {
            log.error("搜索课堂总结失败", e);
            return Result.error("搜索课堂总结失败: " + e.getMessage());
        }
    }

    /**
     * 删除课堂总结
     */
    @DeleteMapping("/delete/{summaryId}")
    public Result<Void> deleteClassSummary(@PathVariable Long summaryId,
                                          @RequestParam Long teacherId) {
        log.info("删除课堂总结，ID: {}, 教师ID: {}", summaryId, teacherId);
        
        try {
            classSummaryService.deleteClassSummary(summaryId, teacherId);
            return Result.success("删除课堂总结成功");
        } catch (Exception e) {
            log.error("删除课堂总结失败", e);
            return Result.error("删除课堂总结失败: " + e.getMessage());
        }
    }

    /**
     * 获取课堂总结统计信息
     */
    @GetMapping("/statistics/{teacherId}")
    public Result<Map<String, Object>> getSummaryStatistics(@PathVariable Long teacherId) {
        log.info("获取课堂总结统计信息，教师ID: {}", teacherId);
        
        try {
            Map<String, Object> statistics = classSummaryService.getSummaryStatistics(teacherId);
            return Result.success("获取统计信息成功", statistics);
        } catch (Exception e) {
            log.error("获取统计信息失败", e);
            return Result.error("获取统计信息失败: " + e.getMessage());
        }
    }

    /**
     * 删除录音文件
     */
    @DeleteMapping("/delete-audio/{summaryId}")
    public Result<Void> deleteAudioFile(@PathVariable Long summaryId,
                                        @RequestParam Long teacherId) {
        log.info("删除录音文件，总结ID: {}, 教师ID: {}", summaryId, teacherId);
        
        try {
            classSummaryService.deleteAudioFile(summaryId, teacherId);
            return Result.success("删除录音文件成功");
        } catch (Exception e) {
            log.error("删除录音文件失败", e);
            return Result.error("删除录音文件失败: " + e.getMessage());
        }
    }
} 