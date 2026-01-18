package com.experiment.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 课堂重点整理实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassSummary {
    
    /**
     * 主键ID
     */
    private Long id;
    
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
     * 录音文件路径
     */
    private String audioFilePath;
    
    /**
     * 录音时长（秒）
     */
    private Integer audioDuration;
    
    /**
     * 课件文件路径
     */
    private String coursewareFilePath;
    
    /**
     * 转录的文本内容
     */
    private String transcriptText;
    
    /**
     * AI生成的重点整理文档
     */
    private String summaryContent;
    
    /**
     * 教师编辑后的最终文档
     */
    private String finalContent;
    
    /**
     * 状态：DRAFT-草稿，REVIEWING-审核中，PUBLISHED-已发布
     */
    private String status;
    
    /**
     * 发布时间
     */
    private LocalDateTime publishTime;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    // 额外字段
    /**
     * 课程名称（联表查询）
     */
    private String courseName;
    
    /**
     * 教师姓名（联表查询）
     */
    private String teacherName;
    
    /**
     * 浏览次数
     */
    private Integer viewCount;
    
    /**
     * 关键词标签
     */
    private String keywords;
} 