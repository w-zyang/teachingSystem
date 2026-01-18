package com.experiment.service.Impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.experiment.mapper.ExamMapper;
import com.experiment.mapper.QuestionMapper;
import com.experiment.mapper.QuestionOptionMapper;
import com.experiment.mapper.StudentExamMapper;
import com.experiment.mapper.UserMapper;
import com.experiment.pojo.Exam;
import com.experiment.pojo.Question;
import com.experiment.pojo.QuestionOption;
import com.experiment.pojo.StudentExam;
import com.experiment.pojo.User;
import com.experiment.result.PageResult;
import com.experiment.service.ExamService;

@Service
public class ExamServiceImpl implements ExamService {
    
    @Autowired
    private ExamMapper examMapper;
    
    @Autowired
    private QuestionMapper questionMapper;
    
    @Autowired
    private QuestionOptionMapper questionOptionMapper;
    
    @Autowired
    private StudentExamMapper studentExamMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    @Transactional
    public Exam createExam(Exam exam) {
        if (exam.getCourseId() == null) {
            throw new IllegalArgumentException("创建考试失败：courseId不能为空，请选择课程后再创建考试。");
        }
        exam.setCreateTime(LocalDateTime.now());
        exam.setUpdateTime(LocalDateTime.now());
        exam.setStatus("draft");
        examMapper.insert(exam);
        
        // 保存题目和选项
        if (exam.getQuestions() != null && !exam.getQuestions().isEmpty()) {
            for (Question question : exam.getQuestions()) {
                question.setExamId(exam.getId());
                questionMapper.insert(question);
                
                // 保存选项
                if (question.getOptions() != null && !question.getOptions().isEmpty()) {
                    for (QuestionOption option : question.getOptions()) {
                        option.setQuestionId(question.getId());
                    }
                    questionOptionMapper.batchInsert(question.getOptions());
                }
            }
        }
        
        return exam;
    }
    
    @Override
    public Exam getExamById(Long id) {
        Exam exam = examMapper.selectById(id);
        if (exam != null) {
            List<Question> questions = questionMapper.selectByExamId(id);
            // 为每个题目加载选项
            for (Question question : questions) {
                if ("choice".equals(question.getType()) || "multiple".equals(question.getType())) {
                    List<QuestionOption> options = questionOptionMapper.selectByQuestionId(question.getId());
                    question.setOptions(options);
                }
            }
            exam.setQuestions(questions);
        }
        return exam;
    }
    
    @Override
    public List<Exam> getExamsByTeacherId(Long teacherId) {
        return examMapper.selectByTeacherId(teacherId);
    }
    
    @Override
    public List<Exam> getExamsByCourseId(Long courseId) {
        return examMapper.selectByCourseId(courseId);
    }
    
    @Override
    public List<Exam> getPublishedExamsByCourseId(Long courseId) {
        return examMapper.selectPublishedByCourseId(courseId);
    }
    
    @Override
    @Transactional
    public Exam updateExam(Exam exam) {
        exam.setUpdateTime(LocalDateTime.now());
        examMapper.update(exam);
        return exam;
    }
    
    @Override
    @Transactional
    public boolean deleteExam(Long id) {
        // 先删除题目
        questionMapper.deleteByExamId(id);
        // 再删除考试
        return examMapper.deleteById(id) > 0;
    }
    
    @Override
    public PageResult<Exam> getExamsByPage(Integer page, Integer size) {
        int offset = (page - 1) * size;
        List<Exam> exams = examMapper.selectByPage(offset, size);
        int total = examMapper.countTotal();
        return new PageResult<>(total, exams);
    }
    
    @Override
    @Transactional
    public boolean publishExam(Long id) {
        try {
            System.out.println("=== 开始发布考试 ===");
            System.out.println("考试ID: " + id);
            
            // 先验证考试是否存在
            Exam existingExam = examMapper.selectById(id);
            if (existingExam == null) {
                System.err.println("发布失败：考试ID " + id + " 不存在");
                return false;
            }
            
            System.out.println("找到考试: " + existingExam.getName() + ", 当前状态: " + existingExam.getStatus());
            
            // 使用专门的状态更新方法
            LocalDateTime now = LocalDateTime.now();
            System.out.println("准备更新考试状态为: published");
            
            int updateResult = examMapper.updateStatus(id, "published", now);
            System.out.println("数据库更新结果: " + updateResult);
            
            if (updateResult > 0) {
                System.out.println("✅ 考试发布成功！ID: " + id);
                return true;
            } else {
                System.err.println("❌ 数据库更新失败，受影响行数: " + updateResult);
                return false;
            }
            
        } catch (Exception e) {
            System.err.println("❌ 发布考试发生异常:");
            System.err.println("异常类型: " + e.getClass().getName());
            System.err.println("异常消息: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    
    @Override
    @Transactional
    public boolean archiveExam(Long id) {
        Exam exam = new Exam();
        exam.setId(id);
        exam.setStatus("archived");
        exam.setUpdateTime(LocalDateTime.now());
        return examMapper.update(exam) > 0;
    }
    
    @Override
    @Transactional
    public Exam generateExam(Long courseId, String examType, Integer questionCount) {
        // 模拟智能生成考试
        Exam exam = new Exam();
        exam.setCourseId(courseId);
        exam.setType(examType);
        exam.setName("智能生成的" + getExamTypeName(examType));
        exam.setDuration(60);
        exam.setTotalScore(100);
        exam.setStatus("draft");
        exam.setCreateTime(LocalDateTime.now());
        exam.setUpdateTime(LocalDateTime.now());
        
        examMapper.insert(exam);
        
        // 生成题目
        List<Question> questions = generateQuestions(exam.getId(), questionCount);
        questionMapper.batchInsert(questions);
        exam.setQuestions(questions);
        
        return exam;
    }
    
    private String getExamTypeName(String type) {
        switch (type) {
            case "quiz": return "小测验";
            case "midterm": return "期中考试";
            case "final": return "期末考试";
            case "homework": return "作业";
            default: return "考试";
        }
    }
    
    private List<Question> generateQuestions(Long examId, Integer count) {
        // 模拟生成题目
        List<Question> questions = new java.util.ArrayList<>();
        Random random = new Random();
        
        for (int i = 0; i < count; i++) {
            Question question = new Question();
            question.setExamId(examId);
            question.setType("single_choice");
            question.setContent("这是第" + (i + 1) + "道题目");
            question.setAnswer("A");
            question.setScore(100 / count);
            question.setDifficulty("medium");
            question.setKnowledgePoint("知识点" + (i + 1));
            question.setSortOrder(i + 1);
            questions.add(question);
        }
        
        return questions;
    }
} 