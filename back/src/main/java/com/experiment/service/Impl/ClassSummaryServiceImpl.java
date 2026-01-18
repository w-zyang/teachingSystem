package com.experiment.service.Impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.experiment.config.BailianConfig;
import com.experiment.mapper.ClassSummaryMapper;
import com.experiment.pojo.ClassSummary;
import com.experiment.pojo.ClassSummaryDTO;
import com.experiment.service.AIService;
import com.experiment.service.ClassSummaryService;

import lombok.extern.slf4j.Slf4j;

/**
 * 课堂重点整理服务实现类
 */
@Slf4j
@Service
public class ClassSummaryServiceImpl implements ClassSummaryService {
    
    @Autowired
    private ClassSummaryMapper classSummaryMapper;
    
    @Autowired
    private AIService aiService;
    
    @Autowired
    private BailianConfig bailianConfig;
    
    @Autowired
    private com.experiment.utils.AliOssProperties aliOssProperties;
    
    private static final String AUDIO_UPLOAD_PATH = "uploads/audio/";
    
    @Override
    public ClassSummary createClassSummary(ClassSummaryDTO dto) {
        ClassSummary classSummary = new ClassSummary();
        classSummary.setCourseId(dto.getCourseId());
        classSummary.setTeacherId(dto.getTeacherId());
        classSummary.setTitle(dto.getTitle());
        classSummary.setDescription(dto.getDescription());
        classSummary.setCoursewareFilePath(dto.getCoursewareFilePath());
        classSummary.setKeywords(dto.getKeywords());
        classSummary.setStatus("DRAFT");
        classSummary.setCreateTime(LocalDateTime.now());
        classSummary.setUpdateTime(LocalDateTime.now());
        classSummary.setViewCount(0);
        
        classSummaryMapper.insert(classSummary);
        log.info("创建课堂总结成功，ID: {}", classSummary.getId());
        
        return classSummary;
    }
    
    @Override
    public String uploadAudioFile(MultipartFile audioFile, Long courseId, Long summaryId, Integer audioDuration) {
        try {
            // 生成唯一文件名
            String originalFilename = audioFile.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".") 
                    ? originalFilename.substring(originalFilename.lastIndexOf(".")) 
                    : ".webm";
            String filename = UUID.randomUUID().toString() + extension;
            
            // 保存到本地（本地运行，不需要 OSS）
            String uploadDir = AUDIO_UPLOAD_PATH + "course" + courseId + "/";
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            // 获取绝对路径
            String filePath = new File(uploadDir + filename).getAbsolutePath();
            
            // 保存文件
            Path path = Paths.get(filePath);
            Files.createDirectories(path.getParent()); // 确保目录存在
            Files.write(path, audioFile.getBytes());
            
            log.info("录音文件已保存到本地: 物理路径={}, 文件大小={} bytes, 时长={}秒", 
                    filePath, audioFile.getSize(), audioDuration);
            
            // 如果提供了summaryId，更新数据库中的音频文件路径和时长
            if (summaryId != null) {
                ClassSummary summary = classSummaryMapper.selectById(summaryId);
                if (summary != null) {
                    // 保存物理路径到数据库
                    summary.setAudioFilePath(filePath);
                    if (audioDuration != null && audioDuration > 0) {
                        summary.setAudioDuration(audioDuration);
                    }
                    summary.setUpdateTime(LocalDateTime.now());
                    classSummaryMapper.update(summary);
                    log.info("已更新课堂总结的音频信息，总结ID: {}, 路径: {}, 时长: {}秒", 
                            summaryId, filePath, audioDuration);
                } else {
                    log.warn("未找到对应的课堂总结，总结ID: {}", summaryId);
                }
            }
            
            // 返回物理路径（用于 Whisper 直接调用）
            return filePath;
            
        } catch (IOException e) {
            log.error("录音文件上传失败", e);
            throw new RuntimeException("录音文件上传失败: " + e.getMessage());
        }
    }
    
    @Override
    public String processAudioToText(Long summaryId, String audioUrl) {
        try {
            log.info("========== 开始处理录音转文字 ==========");
            log.info("总结ID: {}, 音频文件路径: {}", summaryId, audioUrl);
            
            if (audioUrl == null || audioUrl.isEmpty()) {
                throw new RuntimeException("音频文件路径不能为空");
            }
            
            // 调用 AI 服务进行语音转文字（直接使用本地文件路径）
            String transcriptText = aiService.speechToText(audioUrl);
            
            // 更新数据库
            if (summaryId != null) {
            classSummaryMapper.updateTranscriptText(summaryId, transcriptText);
                log.info("已更新数据库，总结ID: {}, 转录文本长度: {}", summaryId, transcriptText.length());
            }
            
            log.info("========== 录音转文字完成，字数: {} ==========", transcriptText.length());
            return transcriptText;
            
        } catch (Exception e) {
            log.error("录音转文字失败", e);
            throw new RuntimeException("录音转文字失败: " + e.getMessage());
        }
    }
    
    @Override
    public String generateSummaryWithAI(Long summaryId, String transcriptText, String coursewareContent) {
        String summaryContent = null;
        try {
            log.info("========== 开始生成AI重点整理 ==========");
            log.info("总结ID: {}", summaryId);
            log.info("转录文字长度: {}", transcriptText != null ? transcriptText.length() : 0);
            log.info("课件内容长度: {}", coursewareContent != null ? coursewareContent.length() : 0);
            log.info("课件内容是否为空: {}", coursewareContent == null || coursewareContent.trim().isEmpty());
            
            // 调用阿里云百炼生成重点整理API
            String apiKey = bailianConfig.getSummaryGeneration().getApiKey();
            String appId = bailianConfig.getSummaryGeneration().getAppId();
            
            log.info("使用API Key: {}...", apiKey != null && apiKey.length() > 10 ? apiKey.substring(0, 10) : "null");
            log.info("使用App ID: {}", appId);
            
            // 调用AI服务，传递转录文本和课件内容
            summaryContent = aiService.generateSummary(transcriptText, coursewareContent, apiKey, appId);
            log.info("AI重点整理生成成功，内容长度: {}", summaryContent.length());
            
            // 尝试更新数据库，如果失败不影响返回结果
            try {
                classSummaryMapper.updateSummaryContent(summaryId, summaryContent);
                log.info("数据库更新成功，总结ID: {}", summaryId);
            } catch (Exception dbException) {
                log.error("数据库更新失败（但AI生成成功），总结ID: {}，错误: {}", summaryId, dbException.getMessage(), dbException);
                // 数据库更新失败不影响返回AI生成的内容
            }
            
            log.info("========== AI重点整理生成完成 ==========");
            return summaryContent;
            
        } catch (Exception e) {
            log.error("AI重点整理生成失败", e);
            // 如果AI生成失败，抛出异常
            if (summaryContent != null) {
                log.warn("AI生成成功但处理过程中出现异常，返回已生成的内容");
                return summaryContent;
            }
            throw new RuntimeException("AI重点整理生成失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void updateFinalContent(Long summaryId, String finalContent) {
        classSummaryMapper.updateFinalContent(summaryId, finalContent);
        log.info("更新最终文档内容成功，总结ID: {}", summaryId);
    }
    
    @Override
    public void publishClassSummary(Long summaryId) {
        // 获取课堂总结
        ClassSummary summary = classSummaryMapper.selectById(summaryId);
        if (summary == null) {
            throw new RuntimeException("课堂总结不存在");
        }
        
        // 确保有最终内容可以发布
        if (summary.getFinalContent() == null || summary.getFinalContent().trim().isEmpty()) {
            // 如果没有最终内容，使用AI生成的内容
            if (summary.getSummaryContent() != null && !summary.getSummaryContent().trim().isEmpty()) {
                summary.setFinalContent(summary.getSummaryContent());
                classSummaryMapper.updateFinalContent(summaryId, summary.getSummaryContent());
                log.info("使用AI生成的内容作为最终内容");
            } else {
                throw new RuntimeException("没有可发布的内容，请先生成或编辑内容");
            }
        }
        
        // 更新状态为已发布
        classSummaryMapper.updateStatus(summaryId, "PUBLISHED", LocalDateTime.now());
        log.info("发布课堂总结成功，ID: {}", summaryId);
    }
    
    @Override
    public List<ClassSummary> getTeacherSummaries(Long teacherId) {
        return classSummaryMapper.selectByTeacherId(teacherId);
    }
    
    @Override
    public List<ClassSummary> getPublishedSummaries(Long courseId) {
        return classSummaryMapper.selectPublishedByCourseId(courseId);
    }
    
    @Override
    public List<ClassSummary> getAllPublishedSummaries() {
        log.info("========== Service: 查询所有已发布的课堂总结 ==========");
        try {
            List<ClassSummary> summaries = classSummaryMapper.selectAllPublished();
            log.info("Mapper 返回结果数量: {}", summaries != null ? summaries.size() : 0);
            
            if (summaries != null && !summaries.isEmpty()) {
                for (ClassSummary summary : summaries) {
                    log.info("课堂总结详情: ID={}, 标题={}, 状态={}, 课程ID={}, 教师ID={}, 发布时间={}", 
                            summary.getId(), 
                            summary.getTitle(), 
                            summary.getStatus(),
                            summary.getCourseId(),
                            summary.getTeacherId(),
                            summary.getPublishTime());
                }
            } else {
                log.warn("⚠️ Mapper 返回空列表或 null");
            }
            
            return summaries;
        } catch (Exception e) {
            log.error("查询所有已发布课堂总结失败", e);
            throw new RuntimeException("查询失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public ClassSummary getSummaryById(Long summaryId) {
        return classSummaryMapper.selectById(summaryId);
    }
    
    @Override
    public void incrementViewCount(Long summaryId) {
        classSummaryMapper.incrementViewCount(summaryId);
    }
    
    @Override
    public List<ClassSummary> searchPublishedSummaries(String keyword) {
        return classSummaryMapper.searchPublished(keyword);
    }
    
    @Override
    public void deleteClassSummary(Long summaryId, Long teacherId) {
        ClassSummary summary = classSummaryMapper.selectById(summaryId);
        if (summary == null) {
            throw new RuntimeException("课堂总结不存在");
        }
        if (!summary.getTeacherId().equals(teacherId)) {
            throw new RuntimeException("无权限删除此课堂总结");
        }
        
        // 删除相关文件
        deleteAssociatedFiles(summary);
        
        // 删除数据库记录
        classSummaryMapper.deleteById(summaryId);
        log.info("删除课堂总结成功，ID: {}", summaryId);
    }
    
    @Override
    public Map<String, Object> getSummaryStatistics(Long teacherId) {
        List<ClassSummary> summaries = classSummaryMapper.selectByTeacherId(teacherId);
        
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalCount", summaries.size());
        
        long publishedCount = summaries.stream()
                .filter(s -> "PUBLISHED".equals(s.getStatus()))
                .count();
        statistics.put("publishedCount", publishedCount);
        
        long draftCount = summaries.stream()
                .filter(s -> "DRAFT".equals(s.getStatus()))
                .count();
        statistics.put("draftCount", draftCount);
        
        int totalViews = summaries.stream()
                .filter(s -> s.getViewCount() != null)
                .mapToInt(ClassSummary::getViewCount)
                .sum();
        statistics.put("totalViews", totalViews);
        
        return statistics;
    }
    
    /**
     * 模拟录音转文字功能
     * 实际实现中应该调用真实的语音识别服务
     */
    /**
     * 删除关联的文件
     */
    private void deleteAssociatedFiles(ClassSummary summary) {
        try {
            if (summary.getAudioFilePath() != null) {
                Files.deleteIfExists(Paths.get(summary.getAudioFilePath()));
            }
        } catch (IOException e) {
            log.warn("删除关联文件失败: {}", e.getMessage());
        }
    }
    
    @Override
    public void deleteAudioFile(Long summaryId, Long teacherId) {
        ClassSummary summary = classSummaryMapper.selectById(summaryId);
        if (summary == null) {
            throw new RuntimeException("课堂总结不存在");
        }
        if (!summary.getTeacherId().equals(teacherId)) {
            throw new RuntimeException("无权限删除此录音");
        }
        
        // 删除录音文件
        try {
            if (summary.getAudioFilePath() != null) {
                Files.deleteIfExists(Paths.get(summary.getAudioFilePath()));
                log.info("删除录音文件成功: {}", summary.getAudioFilePath());
            }
        } catch (IOException e) {
            log.warn("删除录音文件失败: {}", e.getMessage());
        }
        
        // 清空数据库中的录音文件路径
        summary.setAudioFilePath(null);
        summary.setAudioDuration(null);
        classSummaryMapper.update(summary);
        log.info("删除录音文件成功，总结ID: {}", summaryId);
    }
    
    @Override
    public void updateTranscriptText(Long summaryId, String transcriptText) {
        classSummaryMapper.updateTranscriptText(summaryId, transcriptText);
        log.info("更新转录文本成功，总结ID: {}, 文本长度: {}", summaryId, transcriptText != null ? transcriptText.length() : 0);
    }
} 