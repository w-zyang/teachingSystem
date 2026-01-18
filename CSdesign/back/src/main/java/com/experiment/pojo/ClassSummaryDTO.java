package com.experiment.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 课堂重点整理数据传输对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassSummaryDTO {
    
    /**
     * 课程ID
     */
    private Long courseId;
    
    /**
     * 教师ID
     */
    private Long teacherId;
    
    /**
     * 课堂标题
     */
    private String title;
    
    /**
     * 课堂描述
     */
    private String description;
    
    /**
     * 录音文件（Base64编码或文件路径）
     */
    private String audioData;
    
    /**
     * 录音时长（秒）
     */
    private Integer audioDuration;
    
    /**
     * 课件文件路径
     */
    private String coursewareFilePath;
    
    /**
     * 教师编辑的最终文档内容
     */
    private String finalContent;
    
    /**
     * 关键词标签
     */
    private String keywords;
    
    /**
     * 操作类型：CREATE-创建，UPDATE-更新，PUBLISH-发布
     */
    private String action;
}

/**
 * 音频处理请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
class AudioProcessRequestDTO {
    
    /**
     * 课堂总结ID
     */
    private Long summaryId;
    
    /**
     * 音频文件路径
     */
    private String audioFilePath;
    
    /**
     * 课件文件路径
     */
    private String coursewareFilePath;
    
    /**
     * 处理选项：TRANSCRIPT_ONLY-仅转录，FULL_ANALYSIS-完整分析
     */
    private String processOption;
} 