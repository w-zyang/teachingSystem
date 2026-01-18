package com.experiment.service;

import com.experiment.pojo.Exam;
import com.experiment.result.PageResult;
import java.util.List;

public interface ExamService {
    // 创建考试
    Exam createExam(Exam exam);
    
    // 根据ID查询考试
    Exam getExamById(Long id);
    
    // 根据教师ID查询考试列表
    List<Exam> getExamsByTeacherId(Long teacherId);
    
    // 根据课程ID查询考试列表
    List<Exam> getExamsByCourseId(Long courseId);
    
    // 根据课程ID查询已发布的考试列表（学生端使用）
    List<Exam> getPublishedExamsByCourseId(Long courseId);
    
    // 更新考试
    Exam updateExam(Exam exam);
    
    // 删除考试
    boolean deleteExam(Long id);
    
    // 分页查询考试
    PageResult<Exam> getExamsByPage(Integer page, Integer size);
    
    // 发布考试
    boolean publishExam(Long id);
    
    // 归档考试
    boolean archiveExam(Long id);
    
    // 智能生成考试
    Exam generateExam(Long courseId, String examType, Integer questionCount);
} 