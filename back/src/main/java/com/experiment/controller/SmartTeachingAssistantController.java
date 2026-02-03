package com.experiment.controller;

import com.experiment.pojo.*;
import com.experiment.result.Result;
import com.experiment.service.SmartTeachingAssistantService;
import com.experiment.mapper.ChapterMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 智能备课助手控制器（精简版 - 避免功能冲突）
 * 
 * 只包含3个核心功能：
 * 1. 课件更新助手
 * 2. 实验指导书生成
 * 3. 主观题批阅助手
 */
@Slf4j
@RestController
@RequestMapping("/api/smart-teaching")
@CrossOrigin
public class SmartTeachingAssistantController {

    @Autowired
    private SmartTeachingAssistantService smartTeachingService;
    
    @Autowired
    private ChapterMapper chapterMapper;

    // ==================== 1. 课件更新助手 ====================

    /**
     * 更新课件案例
     */
    @PostMapping("/update-courseware")
    public Result<Map<String, Object>> updateCourseware(@RequestBody Map<String, Object> request) {
        try {
            Long teacherId = Long.valueOf(request.get("teacherId").toString());
            Long courseId = request.get("courseId") != null ? Long.valueOf(request.get("courseId").toString()) : null;
            String fileUrl = request.get("fileUrl").toString();
            String fileName = request.get("fileName").toString();
            String updateRequirements = request.get("updateRequirements").toString();

            Long updateId = smartTeachingService.updateCoursewareCases(teacherId, courseId, fileUrl, fileName, updateRequirements);

            Map<String, Object> resultData = new HashMap<>();
            resultData.put("updateId", updateId);
            resultData.put("message", "正在分析课件，请稍后查看更新建议");
            
            return Result.success("提交成功", resultData);
        } catch (Exception e) {
            log.error("更新课件失败", e);
            return Result.error("更新课件失败: " + e.getMessage());
        }
    }

    /**
     * 获取课件更新建议
     */
    @GetMapping("/courseware-suggestions/{updateId}")
    public Result<Map<String, Object>> getCoursewareSuggestions(@PathVariable Long updateId) {
        try {
            Map<String, Object> suggestions = smartTeachingService.getCoursewareUpdateSuggestions(updateId);
            return Result.success("获取成功", suggestions);
        } catch (Exception e) {
            log.error("获取更新建议失败", e);
            return Result.error("获取更新建议失败: " + e.getMessage());
        }
    }

    /**
     * 获取课件更新记录
     */
    @GetMapping("/courseware-updates/{teacherId}")
    public Result<List<CoursewareUpdate>> getCoursewareUpdates(@PathVariable Long teacherId) {
        try {
            List<CoursewareUpdate> list = smartTeachingService.getCoursewareUpdates(teacherId);
            return Result.success("获取成功", list);
        } catch (Exception e) {
            log.error("获取更新记录失败", e);
            return Result.error("获取更新记录失败: " + e.getMessage());
        }
    }

    /**
     * 删除课件更新记录
     */
    @DeleteMapping("/courseware-update/{updateId}")
    public Result<String> deleteCoursewareUpdate(@PathVariable Long updateId) {
        try {
            boolean success = smartTeachingService.deleteCoursewareUpdate(updateId);
            if (success) {
                return Result.success("删除成功");
            } else {
                return Result.error("删除失败");
            }
        } catch (Exception e) {
            log.error("删除课件更新记录失败", e);
            return Result.error("删除课件更新记录失败: " + e.getMessage());
        }
    }

    // ==================== 2. 实验指导书生成 ====================

    /**
     * 获取课程的章节列表
     */
    @GetMapping("/chapters/{courseId}")
    public Result<List<Chapter>> getChaptersByCourseId(@PathVariable Long courseId) {
        try {
            List<Chapter> chapters = chapterMapper.selectByCourseId(courseId);
            return Result.success("获取成功", chapters);
        } catch (Exception e) {
            log.error("获取章节列表失败", e);
            return Result.error("获取章节列表失败: " + e.getMessage());
        }
    }

    /**
     * 生成实验指导书
     */
    @PostMapping("/generate-experiment-guide")
    public Result<Long> generateExperimentGuide(@RequestBody Map<String, Object> request) {
        try {
            Long teacherId = Long.valueOf(request.get("teacherId").toString());
            Long courseId = request.get("courseId") != null ? Long.valueOf(request.get("courseId").toString()) : null;
            String chapterName = request.get("chapterName").toString();
            List<String> knowledgePoints = (List<String>) request.get("knowledgePoints");
            String difficultyLevel = request.getOrDefault("difficultyLevel", "medium").toString();

            Long guideId = smartTeachingService.generateExperimentGuide(
                teacherId, courseId, chapterName, knowledgePoints, difficultyLevel
            );
            
            // 直接返回 guideId
            return Result.success("提交成功", guideId);
        } catch (Exception e) {
            log.error("生成实验指导书失败", e);
            return Result.error("生成实验指导书失败: " + e.getMessage());
        }
    }

    /**
     * 获取实验指导书内容
     */
    @GetMapping("/experiment-guide/{guideId}")
    public Result<Map<String, Object>> getExperimentGuide(@PathVariable Long guideId) {
        try {
            Map<String, Object> content = smartTeachingService.getExperimentGuideContent(guideId);
            return Result.success("获取成功", content);
        } catch (Exception e) {
            log.error("获取实验指导书失败", e);
            return Result.error("获取实验指导书失败: " + e.getMessage());
        }
    }

    /**
     * 获取实验指导书列表
     */
    @GetMapping("/experiment-guides/{teacherId}")
    public Result<List<Map<String, Object>>> getExperimentGuides(@PathVariable Long teacherId) {
        try {
            List<Map<String, Object>> list = smartTeachingService.getExperimentGuides(teacherId);
            return Result.success("获取成功", list);
        } catch (Exception e) {
            log.error("获取实验指导书列表失败", e);
            return Result.error("获取实验指导书列表失败: " + e.getMessage());
        }
    }

    /**
     * 删除实验指导书
     */
    @DeleteMapping("/experiment-guide/{guideId}")
    public Result<String> deleteExperimentGuide(@PathVariable Long guideId) {
        try {
            boolean success = smartTeachingService.deleteExperimentGuide(guideId);
            if (success) {
                return Result.success("删除成功");
            } else {
                return Result.error("删除失败");
            }
        } catch (Exception e) {
            log.error("删除实验指导书失败", e);
            return Result.error("删除实验指导书失败: " + e.getMessage());
        }
    }

    // ==================== 3. 主观题批阅助手 ====================

    /**
     * 提交作业
     */
    @PostMapping("/submit-homework")
    public Result<Map<String, Object>> submitHomework(@RequestBody Map<String, Object> request) {
        try {
            Long teacherId = Long.valueOf(request.get("teacherId").toString());
            Long studentId = Long.valueOf(request.get("studentId").toString());
            Long courseId = request.get("courseId") != null ? Long.valueOf(request.get("courseId").toString()) : null;
            String homeworkTitle = request.get("homeworkTitle").toString();
            String fileUrl = request.getOrDefault("fileUrl", "").toString();
            String fileName = request.getOrDefault("fileName", "").toString();
            String content = request.get("content").toString();

            Long homeworkId = smartTeachingService.submitHomework(
                teacherId, studentId, courseId, homeworkTitle, fileUrl, fileName, content
            );

            Map<String, Object> resultData = new HashMap<>();
            resultData.put("homeworkId", homeworkId);
            resultData.put("message", "作业提交成功");
            
            return Result.success("提交成功", resultData);
        } catch (Exception e) {
            log.error("提交作业失败", e);
            return Result.error("提交作业失败: " + e.getMessage());
        }
    }

    /**
     * AI批阅作业
     */
    @PostMapping("/grade-homework/{homeworkId}")
    public Result<Map<String, Object>> gradeHomework(@PathVariable Long homeworkId) {
        try {
            Map<String, Object> result = smartTeachingService.gradeHomeworkByAI(homeworkId);
            return Result.success("批阅成功", result);
        } catch (Exception e) {
            log.error("批阅作业失败", e);
            return Result.error("批阅作业失败: " + e.getMessage());
        }
    }

    /**
     * 获取教师的作业批阅列表
     */
    @GetMapping("/homework-list/{teacherId}")
    public Result<List<HomeworkGrading>> getHomeworkList(@PathVariable Long teacherId) {
        try {
            List<HomeworkGrading> list = smartTeachingService.getTeacherHomeworks(teacherId);
            return Result.success("获取成功", list);
        } catch (Exception e) {
            log.error("获取作业列表失败", e);
            return Result.error("获取作业列表失败: " + e.getMessage());
        }
    }

    /**
     * 教师审阅AI批阅结果
     */
    @PostMapping("/review-homework")
    public Result<String> reviewHomework(@RequestBody Map<String, Object> request) {
        try {
            Long homeworkId = Long.valueOf(request.get("homeworkId").toString());
            Double teacherScore = Double.valueOf(request.get("teacherScore").toString());
            String teacherComments = request.get("teacherComments").toString();

            boolean success = smartTeachingService.reviewHomework(homeworkId, teacherScore, teacherComments);
            
            if (success) {
                return Result.success("审阅成功");
            } else {
                return Result.error("审阅失败");
            }
        } catch (Exception e) {
            log.error("审阅失败", e);
            return Result.error("审阅失败: " + e.getMessage());
        }
    }

    /**
     * 删除作业记录
     */
    @DeleteMapping("/homework/{homeworkId}")
    public Result<String> deleteHomework(@PathVariable Long homeworkId) {
        try {
            boolean success = smartTeachingService.deleteHomework(homeworkId);
            if (success) {
                return Result.success("删除成功");
            } else {
                return Result.error("删除失败");
            }
        } catch (Exception e) {
            log.error("删除作业记录失败", e);
            return Result.error("删除作业记录失败: " + e.getMessage());
        }
    }
}
