package com.experiment.controller;

import java.util.List;
import java.util.Map;

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

import com.experiment.pojo.Practice;
import com.experiment.pojo.PracticeAnswerDTO;
import com.experiment.result.PageResult;
import com.experiment.result.Result;
import com.experiment.service.PracticeService;

@RestController
@RequestMapping("/api/practice")
@CrossOrigin
public class PracticeController {
    
    @Autowired
    private PracticeService practiceService;
    
    // 获取练习类型列表
    @GetMapping("/types")
    public Result getPracticeTypes() {
        List<Map<String, Object>> types = practiceService.getPracticeTypes();
        return Result.success("获取练习类型成功", types);
    }
    
    // 获取推荐练习
    @GetMapping("/recommended")
    public Result getRecommendedPractices() {
        List<Map<String, Object>> practices = practiceService.getRecommendedPractices();
        return Result.success("获取推荐练习成功", practices);
    }
    
    // 获取练习历史
    @GetMapping("/history/{studentId}")
    public Result getPracticeHistory(@PathVariable Long studentId) {
        List<Map<String, Object>> history = practiceService.getPracticeHistory(studentId);
        return Result.success("获取练习历史成功", history);
    }
    
    // 开始练习
    @PostMapping("/start")
    public Result startPractice(@RequestBody Map<String, Object> request) {
        try {
            Long practiceId = Long.valueOf(request.get("practiceId").toString());
            Long studentId = Long.valueOf(request.get("studentId").toString());
            
            Map<String, Object> result = practiceService.startPractice(practiceId, studentId);
            return Result.success("开始练习成功", result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    // 提交练习答案（使用DTO）
    @PostMapping("/submit")
    public Result submitPractice(@RequestBody PracticeAnswerDTO practiceAnswerDTO) {
        try {
            // 转换DTO为Service需要的格式
            List<Map<String, Object>> answers = new java.util.ArrayList<>();
            for (PracticeAnswerDTO.AnswerItem item : practiceAnswerDTO.getAnswers()) {
                Map<String, Object> answer = new java.util.HashMap<>();
                answer.put("questionId", item.getQuestionId());
                answer.put("answer", item.getAnswer());
                answer.put("questionType", item.getQuestionType());
                answer.put("selectedOptions", item.getSelectedOptions());
                answers.add(answer);
            }
            
            Map<String, Object> result = practiceService.submitPractice(
                practiceAnswerDTO.getPracticeId(), 
                practiceAnswerDTO.getStudentId(), 
                answers
            );
            return Result.success("提交练习成功", result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    // 提交练习答案（兼容旧格式）
    @PostMapping("/submit/legacy")
    public Result submitPracticeLegacy(@RequestBody Map<String, Object> request) {
        try {
            Long practiceId = Long.valueOf(request.get("practiceId").toString());
            Long studentId = Long.valueOf(request.get("studentId").toString());
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> answers = (List<Map<String, Object>>) request.get("answers");
            
            Map<String, Object> result = practiceService.submitPractice(practiceId, studentId, answers);
            return Result.success("提交练习成功", result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    // 根据ID查询练习
    @GetMapping("/{id}")
    public Result getPracticeById(@PathVariable Long id) {
        Practice practice = practiceService.getPracticeById(id);
        if (practice != null) {
            return Result.success("获取练习成功", practice);
        } else {
            return Result.error("练习不存在");
        }
    }
    
    // 根据课程ID查询练习列表
    @GetMapping("/course/{courseId}")
    public Result getPracticesByCourseId(@PathVariable Long courseId) {
        List<Practice> practices = practiceService.getPracticesByCourseId(courseId);
        return Result.success("获取课程练习成功", practices);
    }
    
    // 根据类型查询练习列表
    @GetMapping("/type/{type}")
    public Result getPracticesByType(@PathVariable String type) {
        List<Practice> practices = practiceService.getPracticesByType(type);
        return Result.success("获取类型练习成功", practices);
    }
    
    // 根据难度查询练习列表
    @GetMapping("/difficulty/{difficulty}")
    public Result getPracticesByDifficulty(@PathVariable String difficulty) {
        List<Practice> practices = practiceService.getPracticesByDifficulty(difficulty);
        return Result.success("获取难度练习成功", practices);
    }
    
    // 分页查询练习
    @GetMapping("/page")
    public Result getPracticesByPage(@RequestParam(defaultValue = "1") Integer page,
                                   @RequestParam(defaultValue = "10") Integer size) {
        PageResult<Practice> result = practiceService.getPracticesByPage(page, size);
        return Result.success("分页查询练习成功", result);
    }
    
    // 创建练习
    @PostMapping
    public Result createPractice(@RequestBody Practice practice) {
        boolean success = practiceService.createPractice(practice);
        if (success) {
            return Result.success("练习创建成功");
        } else {
            return Result.error("练习创建失败");
        }
    }
    
    // 更新练习
    @PutMapping("/{id}")
    public Result updatePractice(@PathVariable Long id, @RequestBody Practice practice) {
        practice.setId(id);
        boolean success = practiceService.updatePractice(practice);
        if (success) {
            return Result.success("练习更新成功");
        } else {
            return Result.error("练习更新失败");
        }
    }
    
    // 删除练习
    @DeleteMapping("/{id}")
    public Result deletePractice(@PathVariable Long id) {
        boolean success = practiceService.deletePractice(id);
        if (success) {
            return Result.success("练习删除成功");
        } else {
            return Result.error("练习删除失败");
        }
    }
    
    // 获取学生练习记录
    @GetMapping("/student/{studentId}/practice/{practiceId}")
    public Result getStudentPractice(@PathVariable Long studentId, @PathVariable Long practiceId) {
        com.experiment.pojo.StudentPractice studentPractice = practiceService.getStudentPractice(studentId, practiceId);
        if (studentPractice != null) {
            return Result.success("获取学生练习记录成功", studentPractice);
        } else {
            return Result.error("练习记录不存在");
        }
    }
    
    // 获取学生的所有练习记录
    @GetMapping("/student/{studentId}/history")
    public Result getStudentPracticeHistory(@PathVariable Long studentId) {
        List<com.experiment.pojo.StudentPractice> history = practiceService.getStudentPracticeHistory(studentId);
        return Result.success("获取学生练习历史成功", history);
    }
    
    // 获取练习的平均分
    @GetMapping("/{practiceId}/average-score")
    public Result getPracticeAverageScore(@PathVariable Long practiceId) {
        Double averageScore = practiceService.getPracticeAverageScore(practiceId);
        return Result.success("获取练习平均分成功", averageScore);
    }
    
    // 获取学生的平均分
    @GetMapping("/student/{studentId}/average-score")
    public Result getStudentAverageScore(@PathVariable Long studentId) {
        Double averageScore = practiceService.getStudentAverageScore(studentId);
        return Result.success("获取学生平均分成功", averageScore);
    }
    
    // 获取练习统计信息
    @GetMapping("/{practiceId}/stats")
    public Result getPracticeStats(@PathVariable Long practiceId) {
        try {
            Practice practice = practiceService.getPracticeById(practiceId);
            Double averageScore = practiceService.getPracticeAverageScore(practiceId);
            
            Map<String, Object> stats = new java.util.HashMap<>();
            stats.put("practice", practice);
            stats.put("averageScore", averageScore);
            stats.put("participantCount", 0); // 这里可以添加参与人数统计
            
            return Result.success("获取练习统计信息成功", stats);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
} 