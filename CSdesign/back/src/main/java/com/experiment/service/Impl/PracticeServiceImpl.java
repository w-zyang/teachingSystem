package com.experiment.service.Impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.experiment.constant.PracticeConstants;
import com.experiment.exception.PracticeException;
import com.experiment.mapper.PracticeMapper;
import com.experiment.mapper.StudentPracticeMapper;
import com.experiment.pojo.Practice;
import com.experiment.pojo.StudentPractice;
import com.experiment.result.PageResult;
import com.experiment.service.PracticeService;

@Service
public class PracticeServiceImpl implements PracticeService {
    
    @Autowired
    private PracticeMapper practiceMapper;
    
    @Autowired
    private StudentPracticeMapper studentPracticeMapper;
    
    @Override
    public List<Map<String, Object>> getPracticeTypes() {
        List<Map<String, Object>> types = new ArrayList<>();
        
        Map<String, Object> algorithm = new HashMap<>();
        algorithm.put("id", 1);
        algorithm.put("name", "算法练习");
        algorithm.put("description", "数据结构与算法基础练习");
        algorithm.put("icon", "💻");
        algorithm.put("duration", 30);
        algorithm.put("questionCount", 10);
        algorithm.put("type", PracticeConstants.TYPE_ALGORITHM);
        types.add(algorithm);
        
        Map<String, Object> programming = new HashMap<>();
        programming.put("id", 2);
        programming.put("name", "编程实践");
        programming.put("description", "实际编程项目练习");
        programming.put("icon", "🔧");
        programming.put("duration", 45);
        programming.put("questionCount", 8);
        programming.put("type", PracticeConstants.TYPE_PROGRAMMING);
        types.add(programming);
        
        Map<String, Object> systemDesign = new HashMap<>();
        systemDesign.put("id", 3);
        systemDesign.put("name", "系统设计");
        systemDesign.put("description", "软件系统设计练习");
        systemDesign.put("icon", "🏗️");
        systemDesign.put("duration", 60);
        systemDesign.put("questionCount", 15);
        systemDesign.put("type", PracticeConstants.TYPE_SYSTEM_DESIGN);
        types.add(systemDesign);
        
        Map<String, Object> database = new HashMap<>();
        database.put("id", 4);
        database.put("name", "数据库操作");
        database.put("description", "SQL和数据库管理练习");
        database.put("icon", "🗄️");
        database.put("duration", 90);
        database.put("questionCount", 5);
        database.put("type", PracticeConstants.TYPE_DATABASE);
        types.add(database);
        
        return types;
    }
    
    @Override
    public List<Map<String, Object>> getRecommendedPractices() {
        List<Map<String, Object>> practices = new ArrayList<>();
        
        Map<String, Object> practice1 = new HashMap<>();
        practice1.put("id", 1);
        practice1.put("title", "数组和链表操作");
        practice1.put("description", "练习数组和链表的基本操作和算法");
        practice1.put("type", "算法练习");
        practice1.put("difficulty", PracticeConstants.DIFFICULTY_EASY);
        practice1.put("difficultyText", "简单");
        practice1.put("duration", 15);
        practices.add(practice1);
        
        Map<String, Object> practice2 = new HashMap<>();
        practice2.put("id", 2);
        practice2.put("title", "栈和队列应用");
        practice2.put("description", "学习栈和队列在实际问题中的应用");
        practice2.put("type", "算法练习");
        practice2.put("difficulty", PracticeConstants.DIFFICULTY_MEDIUM);
        practice2.put("difficultyText", "中等");
        practice2.put("duration", 25);
        practices.add(practice2);
        
        Map<String, Object> practice3 = new HashMap<>();
        practice3.put("id", 3);
        practice3.put("title", "树结构遍历");
        practice3.put("description", "练习二叉树的各种遍历算法");
        practice3.put("type", "算法练习");
        practice3.put("difficulty", PracticeConstants.DIFFICULTY_MEDIUM);
        practice3.put("difficultyText", "中等");
        practice3.put("duration", 30);
        practices.add(practice3);
        
        Map<String, Object> practice4 = new HashMap<>();
        practice4.put("id", 4);
        practice4.put("title", "图论算法");
        practice4.put("description", "学习图的表示和基本算法");
        practice4.put("type", "算法练习");
        practice4.put("difficulty", PracticeConstants.DIFFICULTY_HARD);
        practice4.put("difficultyText", "困难");
        practice4.put("duration", 40);
        practices.add(practice4);
        
        return practices;
    }
    
    @Override
    public List<Map<String, Object>> getPracticeHistory(Long studentId) {
        List<StudentPractice> studentPractices = studentPracticeMapper.selectByStudentId(studentId);
        List<Map<String, Object>> history = new ArrayList<>();
        
        for (StudentPractice sp : studentPractices) {
            if (PracticeConstants.STUDENT_STATUS_COMPLETED.equals(sp.getStatus())) {
                Practice practice = practiceMapper.selectById(sp.getPracticeId());
                if (practice != null) {
                    Map<String, Object> record = new HashMap<>();
                    record.put("id", sp.getId());
                    record.put("title", practice.getTitle());
                    record.put("score", sp.getScore());
                    record.put("totalScore", sp.getTotalScore());
                    record.put("accuracy", sp.getAccuracy());
                    record.put("completedTime", sp.getCompleteTime());
                    history.add(record);
                }
            }
        }
        
        return history;
    }
    
    @Override
    public Map<String, Object> startPractice(Long practiceId, Long studentId) {
        // 检查练习是否存在
        Practice practice = practiceMapper.selectById(practiceId);
        if (practice == null) {
            throw new PracticeException(PracticeConstants.ERROR_PRACTICE_NOT_FOUND);
        }
        
        // 检查是否已经开始练习
        StudentPractice existingPractice = studentPracticeMapper.selectByStudentAndPractice(studentId, practiceId);
        if (existingPractice != null && PracticeConstants.STUDENT_STATUS_IN_PROGRESS.equals(existingPractice.getStatus())) {
            throw new PracticeException(PracticeConstants.ERROR_PRACTICE_ALREADY_STARTED);
        }
        
        // 创建新的练习记录
        StudentPractice studentPractice = new StudentPractice();
        studentPractice.setStudentId(studentId);
        studentPractice.setPracticeId(practiceId);
        studentPractice.setTotalScore(PracticeConstants.DEFAULT_TOTAL_SCORE);
        studentPractice.setStatus(PracticeConstants.STUDENT_STATUS_IN_PROGRESS);
        studentPractice.setStartTime(LocalDateTime.now());
        studentPractice.setCreateTime(LocalDateTime.now());
        studentPractice.setUpdateTime(LocalDateTime.now());
        
        studentPracticeMapper.insert(studentPractice);
        
        Map<String, Object> result = new HashMap<>();
        result.put("practiceId", practiceId);
        result.put("studentId", studentId);
        result.put("status", "started");
        result.put("startTime", studentPractice.getStartTime());
        result.put("practice", practice);
        
        return result;
    }
    
    @Override
    public Map<String, Object> submitPractice(Long practiceId, Long studentId, List<Map<String, Object>> answers) {
        // 检查练习记录是否存在
        StudentPractice studentPractice = studentPracticeMapper.selectByStudentAndPractice(studentId, practiceId);
        if (studentPractice == null) {
            throw new PracticeException(PracticeConstants.ERROR_PRACTICE_RECORD_NOT_FOUND);
        }
        
        if (PracticeConstants.STUDENT_STATUS_COMPLETED.equals(studentPractice.getStatus())) {
            throw new PracticeException(PracticeConstants.ERROR_PRACTICE_ALREADY_COMPLETED);
        }
        
        // 计算得分（这里简化处理，实际应该根据题目答案计算）
        int score = calculateScore(answers);
        int accuracy = calculateAccuracy(answers);
        
        // 更新练习记录
        studentPractice.setScore(score);
        studentPractice.setAccuracy(accuracy);
        studentPractice.setStatus(PracticeConstants.STUDENT_STATUS_COMPLETED);
        studentPractice.setCompleteTime(LocalDateTime.now());
        studentPractice.setUpdateTime(LocalDateTime.now());
        
        // 计算实际用时
        if (studentPractice.getStartTime() != null) {
            long durationMinutes = java.time.Duration.between(studentPractice.getStartTime(), LocalDateTime.now()).toMinutes();
            studentPractice.setDuration((int) durationMinutes);
        }
        
        studentPracticeMapper.update(studentPractice);
        
        Map<String, Object> result = new HashMap<>();
        result.put("practiceId", practiceId);
        result.put("studentId", studentId);
        result.put("score", score);
        result.put("totalScore", studentPractice.getTotalScore());
        result.put("accuracy", accuracy);
        result.put("status", "completed");
        result.put("completeTime", studentPractice.getCompleteTime());
        result.put("duration", studentPractice.getDuration());
        
        return result;
    }
    
    @Override
    public Practice getPracticeById(Long id) {
        return practiceMapper.selectById(id);
    }
    
    @Override
    public List<Practice> getPracticesByCourseId(Long courseId) {
        return practiceMapper.selectByCourseId(courseId);
    }
    
    @Override
    public List<Practice> getPracticesByType(String type) {
        return practiceMapper.selectByType(type);
    }
    
    @Override
    public List<Practice> getPracticesByDifficulty(String difficulty) {
        return practiceMapper.selectByDifficulty(difficulty);
    }
    
    @Override
    public PageResult<Practice> getPracticesByPage(Integer page, Integer size) {
        int offset = (page - 1) * size;
        List<Practice> practices = practiceMapper.selectByPage(offset, size);
        int total = practiceMapper.countTotal();
        
        PageResult<Practice> result = new PageResult<>();
        result.setRecords(practices);
        result.setTotal(total);
        
        return result;
    }
    
    @Override
    public boolean createPractice(Practice practice) {
        practice.setCreateTime(LocalDateTime.now());
        practice.setUpdateTime(LocalDateTime.now());
        return practiceMapper.insert(practice) > 0;
    }
    
    @Override
    public boolean updatePractice(Practice practice) {
        practice.setUpdateTime(LocalDateTime.now());
        return practiceMapper.update(practice) > 0;
    }
    
    @Override
    public boolean deletePractice(Long id) {
        return practiceMapper.deleteById(id) > 0;
    }
    
    @Override
    public StudentPractice getStudentPractice(Long studentId, Long practiceId) {
        return studentPracticeMapper.selectByStudentAndPractice(studentId, practiceId);
    }
    
    @Override
    public List<StudentPractice> getStudentPracticeHistory(Long studentId) {
        return studentPracticeMapper.selectByStudentId(studentId);
    }
    
    @Override
    public Double getPracticeAverageScore(Long practiceId) {
        return studentPracticeMapper.selectAverageScoreByPractice(practiceId);
    }
    
    @Override
    public Double getStudentAverageScore(Long studentId) {
        return studentPracticeMapper.selectAverageScoreByStudent(studentId);
    }
    
    // 计算得分的辅助方法
    private int calculateScore(List<Map<String, Object>> answers) {
        // 这里简化处理，实际应该根据题目答案计算
        // 假设每个答案正确得10分
        int correctCount = 0;
        for (Map<String, Object> answer : answers) {
            // 这里应该根据实际题目类型和答案进行判断
            // 暂时返回一个随机分数
            if (Math.random() > 0.3) {
                correctCount++;
            }
        }
        return correctCount * 10;
    }
    
    // 计算正确率的辅助方法
    private int calculateAccuracy(List<Map<String, Object>> answers) {
        if (answers == null || answers.isEmpty()) {
            return 0;
        }
        
        int correctCount = 0;
        for (Map<String, Object> answer : answers) {
            // 这里应该根据实际题目类型和答案进行判断
            // 暂时返回一个随机正确率
            if (Math.random() > 0.3) {
                correctCount++;
            }
        }
        
        return (int) ((double) correctCount / answers.size() * 100);
    }
} 