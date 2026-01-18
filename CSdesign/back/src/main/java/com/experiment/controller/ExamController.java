package com.experiment.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.experiment.pojo.Exam;
import com.experiment.result.PageResult;
import com.experiment.result.Result;
import com.experiment.service.ExamService;

@RestController
@RequestMapping("/api/exam")
@CrossOrigin
public class   ExamController {
    
    @Autowired
    private ExamService examService;
    
    // 创建考试
    @PostMapping
    public Result<Exam> createExam(@RequestBody Exam exam) {
        try {
            Exam createdExam = examService.createExam(exam);
            return Result.success("考试创建成功", createdExam);
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("创建考试失败: " + e.getMessage());
        }
    }
    
    // 根据ID查询考试
    @GetMapping("/{id}")
    public Result<Exam> getExamById(@PathVariable Long id) {
        try {
            Exam exam = examService.getExamById(id);
            if (exam != null) {
                return Result.success("查询考试成功", exam);
            } else {
                return Result.error("考试不存在");
            }
        } catch (Exception e) {
            return Result.error("查询考试失败: " + e.getMessage());
        }
    }
    
    // 根据教师ID查询考试列表
    @GetMapping("/teacher/{teacherId}")
    public Result<List<Exam>> getExamsByTeacherId(@PathVariable Long teacherId) {
        try {
            List<Exam> exams = examService.getExamsByTeacherId(teacherId);
            return Result.success("查询教师考试列表成功", exams);
        } catch (Exception e) {
            return Result.error("查询教师考试列表失败: " + e.getMessage());
        }
    }
    
    // 根据课程ID查询考试列表
    @GetMapping("/course/{courseId}")
    public Result<List<Exam>> getExamsByCourseId(@PathVariable Long courseId) {
        try {
            List<Exam> exams = examService.getExamsByCourseId(courseId);
            return Result.success("查询课程考试列表成功", exams);
        } catch (Exception e) {
            return Result.error("查询课程考试列表失败: " + e.getMessage());
        }
    }
    
    // 根据课程ID查询已发布的考试列表（学生端使用）
    @GetMapping("/course/{courseId}/published")
    public Result<List<Exam>> getPublishedExamsByCourseId(@PathVariable Long courseId) {
        try {
            List<Exam> exams = examService.getPublishedExamsByCourseId(courseId);
            return Result.success("查询课程已发布考试列表成功", exams);
        } catch (Exception e) {
            return Result.error("查询课程已发布考试列表失败: " + e.getMessage());
        }
    }
    
    // 分页查询考试
    @GetMapping("/page")
    public Result<PageResult<Exam>> getExamsByPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        try {
            PageResult<Exam> pageResult = examService.getExamsByPage(page, size);
            return Result.success("分页查询考试成功", pageResult);
        } catch (Exception e) {
            return Result.error("分页查询考试失败: " + e.getMessage());
        }
    }
    
    // 更新考试
    @PutMapping
    public Result<Exam> updateExam(@RequestBody Exam exam) {
        try {
            Exam updatedExam = examService.updateExam(exam);
            return Result.success("更新考试成功", updatedExam);
        } catch (Exception e) {
            return Result.error("更新考试失败: " + e.getMessage());
        }
    }
    
    // 删除考试
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteExam(@PathVariable Long id) {
        try {
            boolean success = examService.deleteExam(id);
            if (success) {
                return Result.success("删除考试成功", true);
            } else {
                return Result.error("删除考试失败");
            }
        } catch (Exception e) {
            return Result.error("删除考试失败: " + e.getMessage());
        }
    }
    
    // 发布考试
    @PutMapping("/{id}/publish")
    public Result<Boolean> publishExam(@PathVariable Long id) {
        try {
            boolean success = examService.publishExam(id);
            if (success) {
                return Result.success("发布考试成功", true);
            } else {
                return Result.error("发布考试失败");
            }
        } catch (Exception e) {
            return Result.error("发布考试失败: " + e.getMessage());
        }
    }
    
    // 归档考试
    @PutMapping("/{id}/archive")
    public Result<Boolean> archiveExam(@PathVariable Long id) {
        try {
            boolean success = examService.archiveExam(id);
            if (success) {
                return Result.success("归档考试成功", true);
            } else {
                return Result.error("归档考试失败");
            }
        } catch (Exception e) {
            return Result.error("归档考试失败: " + e.getMessage());
        }
    }
    
    // 智能生成考试
    @PostMapping("/generate")
    public Result<Object> generateExam(@RequestBody java.util.Map<String, Object> data) {
        try {
            // 参数验证
            if (data.get("courseId") == null) {
                return Result.error("课程ID不能为空");
            }
            
            Long courseId = Long.valueOf(String.valueOf(data.get("courseId")));
            String examType = (String) data.get("examType");
            Integer questionCount = Integer.valueOf(String.valueOf(data.get("questionCount")));
            Integer duration = data.get("duration") != null ? Integer.valueOf(String.valueOf(data.get("duration"))) : 60;
            Integer totalScore = data.get("totalScore") != null ? Integer.valueOf(String.valueOf(data.get("totalScore"))) : 100;
            
            // 构建模拟的考试数据
            java.util.Map<String, Object> result = new java.util.HashMap<>();
            java.util.List<java.util.Map<String, Object>> questions = new java.util.ArrayList<>();
            
            for (int i = 1; i <= questionCount; i++) {
                java.util.Map<String, Object> question = new java.util.HashMap<>();
                question.put("id", i);
                question.put("type", examType);
                question.put("content", String.format("这是第%d题，类型为%s", i, examType));
                question.put("score", totalScore / questionCount);
                question.put("answer", String.format("第%d题的参考答案", i));
                question.put("explanation", String.format("第%d题的详细解析", i));
                
                if ("choice".equals(examType) || "multiple".equals(examType)) {
                    java.util.List<java.util.Map<String, String>> options = new java.util.ArrayList<>();
                    options.add(java.util.Map.of("key", "A", "content", "选项A"));
                    options.add(java.util.Map.of("key", "B", "content", "选项B"));
                    options.add(java.util.Map.of("key", "C", "content", "选项C"));
                    options.add(java.util.Map.of("key", "D", "content", "选项D"));
                    question.put("options", options);
                }
                
                questions.add(question);
            }
            
            result.put("questions", questions);
            result.put("totalQuestions", questionCount);
            result.put("totalScore", totalScore);
            result.put("duration", duration);
            result.put("examType", examType);
            result.put("courseId", courseId);
            
            return Result.success("考试生成成功", result);
        } catch (NumberFormatException e) {
            return Result.error("参数格式错误: " + e.getMessage());
        } catch (Exception e) {
            return Result.error("考试生成失败: " + e.getMessage());
        }
    }
} 