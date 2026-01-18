package com.experiment.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.experiment.pojo.ClassSummary;
import com.experiment.pojo.ClassSummaryDTO;

/**
 * 课堂重点整理服务接口
 */
public interface ClassSummaryService {
    
    /**
     * 创建课堂总结
     */
    ClassSummary createClassSummary(ClassSummaryDTO dto);
    
    /**
     * 上传录音文件
     */
    String uploadAudioFile(MultipartFile audioFile, Long courseId, Long summaryId, Integer audioDuration);
    
    /**
     * 处理录音（语音转文字）
     * @param summaryId 总结ID
     * @param audioUrl 音频文件 OSS URL
     * @return 转录后的文字
     */
    String processAudioToText(Long summaryId, String audioUrl);
    
    /**
     * 生成AI重点整理
     */
    String generateSummaryWithAI(Long summaryId, String transcriptText, String coursewareContent);
    
    /**
     * 更新最终文档内容
     */
    void updateFinalContent(Long summaryId, String finalContent);
    
    /**
     * 发布课堂总结
     */
    void publishClassSummary(Long summaryId);
    
    /**
     * 获取教师的课堂总结列表
     */
    List<ClassSummary> getTeacherSummaries(Long teacherId);
    
    /**
     * 获取课程的已发布总结列表
     */
    List<ClassSummary> getPublishedSummaries(Long courseId);
    
    /**
     * 获取所有已发布的总结（学生端）
     */
    List<ClassSummary> getAllPublishedSummaries();
    
    /**
     * 根据ID获取课堂总结详情
     */
    ClassSummary getSummaryById(Long summaryId);
    
    /**
     * 增加浏览次数
     */
    void incrementViewCount(Long summaryId);
    
    /**
     * 搜索已发布的课堂总结
     */
    List<ClassSummary> searchPublishedSummaries(String keyword);
    
    /**
     * 删除课堂总结
     */
    void deleteClassSummary(Long summaryId, Long teacherId);
    
    /**
     * 获取课堂总结统计信息
     */
    Map<String, Object> getSummaryStatistics(Long teacherId);
    
    /**
     * 删除录音文件
     */
    void deleteAudioFile(Long summaryId, Long teacherId);
    
    /**
     * 更新转录文本
     */
    void updateTranscriptText(Long summaryId, String transcriptText);
} 