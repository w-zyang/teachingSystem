package com.experiment.service;

import com.experiment.pojo.CoursewareUpdate;
import com.experiment.pojo.HomeworkGrading;

import java.util.List;
import java.util.Map;

/**
 * 智能备课助手服务接口（精简版 - 避免功能冲突）
 * 
 * 只包含3个核心功能：
 * 1. 课件更新助手 - 更新已有PPT的案例
 * 2. 实验指导书生成 - 生成完整的实验指导书
 * 3. 主观题批阅助手 - AI辅助批阅文本作业
 */
public interface SmartTeachingAssistantService {
    
    // ==================== 1. 课件更新助手 ====================
    
    /**
     * 更新课件案例
     * @param teacherId 教师ID
     * @param courseId 课程ID
     * @param fileUrl 原始PPT文件URL
     * @param fileName 文件名
     * @param updateRequirements 更新要求（如：更新2024-2025年信息安全案例）
     * @return 更新记录ID
     */
    Long updateCoursewareCases(Long teacherId, Long courseId, String fileUrl, String fileName, String updateRequirements);
    
    /**
     * 获取课件更新建议
     * @param updateId 更新记录ID
     * @return 更新建议
     */
    Map<String, Object> getCoursewareUpdateSuggestions(Long updateId);
    
    /**
     * 获取课件更新记录
     * @param teacherId 教师ID
     * @return 更新记录列表
     */
    List<CoursewareUpdate> getCoursewareUpdates(Long teacherId);
    
    /**
     * 删除课件更新记录
     * @param updateId 更新记录ID
     * @return 是否成功
     */
    boolean deleteCoursewareUpdate(Long updateId);
    
    // ==================== 2. 实验指导书生成 ====================
    
    /**
     * 生成实验指导书
     * @param teacherId 教师ID
     * @param courseId 课程ID
     * @param chapterName 章节名称
     * @param knowledgePoints 知识点列表
     * @param difficultyLevel 难度等级
     * @return 实验指导书ID
     */
    Long generateExperimentGuide(Long teacherId, Long courseId, String chapterName, 
                                 List<String> knowledgePoints, String difficultyLevel);
    
    /**
     * 获取实验指导书内容
     * @param guideId 指导书ID
     * @return 指导书详情
     */
    Map<String, Object> getExperimentGuideContent(Long guideId);
    
    /**
     * 获取实验指导书列表
     * @param teacherId 教师ID
     * @return 指导书列表
     */
    List<Map<String, Object>> getExperimentGuides(Long teacherId);
    
    /**
     * 删除实验指导书
     * @param guideId 指导书ID
     * @return 是否成功
     */
    boolean deleteExperimentGuide(Long guideId);
    
    // ==================== 3. 主观题批阅助手 ====================
    
    /**
     * AI批阅作业（主观题）
     * @param homeworkId 作业ID
     * @return 批阅结果
     */
    Map<String, Object> gradeHomeworkByAI(Long homeworkId);
    
    /**
     * 提交作业
     * @param teacherId 教师ID
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @param homeworkTitle 作业标题
     * @param fileUrl 文件URL
     * @param fileName 文件名
     * @param content 文本内容
     * @return 作业ID
     */
    Long submitHomework(Long teacherId, Long studentId, Long courseId, String homeworkTitle, 
                       String fileUrl, String fileName, String content);
    
    /**
     * 获取教师的所有作业批阅记录
     * @param teacherId 教师ID
     * @return 作业列表
     */
    List<HomeworkGrading> getTeacherHomeworks(Long teacherId);
    
    /**
     * 教师审阅AI批阅结果
     * @param homeworkId 作业ID
     * @param teacherScore 教师评分
     * @param teacherComments 教师评语
     * @return 是否成功
     */
    boolean reviewHomework(Long homeworkId, Double teacherScore, String teacherComments);
    
    /**
     * 删除作业记录
     * @param homeworkId 作业ID
     * @return 是否成功
     */
    boolean deleteHomework(Long homeworkId);
}
