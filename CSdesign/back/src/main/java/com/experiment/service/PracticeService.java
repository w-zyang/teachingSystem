package com.experiment.service;

import java.util.List;
import java.util.Map;

import com.experiment.pojo.Practice;
import com.experiment.pojo.StudentPractice;
import com.experiment.result.PageResult;

public interface PracticeService {
    
    // 获取练习类型列表
    List<Map<String, Object>> getPracticeTypes();
    
    // 获取推荐练习
    List<Map<String, Object>> getRecommendedPractices();
    
    // 获取练习历史
    List<Map<String, Object>> getPracticeHistory(Long studentId);
    
    // 开始练习
    Map<String, Object> startPractice(Long practiceId, Long studentId);
    
    // 提交练习答案
    Map<String, Object> submitPractice(Long practiceId, Long studentId, List<Map<String, Object>> answers);
    
    // 根据ID查询练习
    Practice getPracticeById(Long id);
    
    // 根据课程ID查询练习列表
    List<Practice> getPracticesByCourseId(Long courseId);
    
    // 根据类型查询练习列表
    List<Practice> getPracticesByType(String type);
    
    // 根据难度查询练习列表
    List<Practice> getPracticesByDifficulty(String difficulty);
    
    // 分页查询练习
    PageResult<Practice> getPracticesByPage(Integer page, Integer size);
    
    // 创建练习
    boolean createPractice(Practice practice);
    
    // 更新练习
    boolean updatePractice(Practice practice);
    
    // 删除练习
    boolean deletePractice(Long id);
    
    // 获取学生练习记录
    StudentPractice getStudentPractice(Long studentId, Long practiceId);
    
    // 获取学生的所有练习记录
    List<StudentPractice> getStudentPracticeHistory(Long studentId);
    
    // 获取练习的平均分
    Double getPracticeAverageScore(Long practiceId);
    
    // 获取学生的平均分
    Double getStudentAverageScore(Long studentId);
} 