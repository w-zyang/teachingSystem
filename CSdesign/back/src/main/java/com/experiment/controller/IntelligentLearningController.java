package com.experiment.controller;

import com.experiment.service.IntelligentLearningService;
import com.experiment.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/intelligent")
@CrossOrigin
public class IntelligentLearningController {
    
    @Autowired
    private IntelligentLearningService intelligentLearningService;
    
    // ============ 智能学习路径规划API ============
    
    /**
     * 生成个性化学习路径
     */
    @PostMapping("/learning-path/generate")
    public Result<Map<String, Object>> generateLearningPath(@RequestBody Map<String, Object> data) {
        log.info("生成学习路径请求: {}", data);
        try {
            Long studentId = Long.valueOf(data.get("studentId").toString());
            String subject = (String) data.get("subject");
            String targetLevel = (String) data.getOrDefault("targetLevel", "beginner");
            
            Map<String, Object> result = intelligentLearningService.generatePersonalizedPath(studentId, subject, targetLevel);
            
            if (result != null) {
                return Result.success("学习路径生成成功", result);
            } else {
                return Result.error("学习路径生成失败");
            }
        } catch (Exception e) {
            log.error("生成学习路径失败", e);
            return Result.error("系统错误: " + e.getMessage());
        }
    }
    
    /**
     * 获取学生学习路径列表
     */
    @GetMapping("/learning-path/student/{studentId}")
    public Result<List<Map<String, Object>>> getStudentLearningPaths(@PathVariable Long studentId) {
        log.info("获取学生学习路径: {}", studentId);
        try {
            List<Map<String, Object>> paths = intelligentLearningService.getStudentLearningPaths(studentId);
            return Result.success("获取成功", paths);
        } catch (Exception e) {
            log.error("获取学生学习路径失败", e);
            return Result.error("获取失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新学习进度
     */
    @PostMapping("/learning-path/progress")
    public Result<Boolean> updateLearningProgress(@RequestBody Map<String, Object> data) {
        log.info("更新学习进度: {}", data);
        try {
            Long pathId = Long.valueOf(data.get("pathId").toString());
            Integer stepNumber = Integer.valueOf(data.get("stepNumber").toString());
            Double masteryLevel = Double.valueOf(data.get("masteryLevel").toString());
            Integer studyDuration = Integer.valueOf(data.getOrDefault("studyDuration", "0").toString());
            
            boolean success = intelligentLearningService.updateLearningProgress(pathId, stepNumber, masteryLevel, studyDuration);
            
            if (success) {
                return Result.success("进度更新成功", true);
            } else {
                return Result.error("进度更新失败");
            }
        } catch (Exception e) {
            log.error("更新学习进度失败", e);
            return Result.error("更新失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取学习路径详情
     */
    @GetMapping("/learning-path/{pathId}/details")
    public Result<Map<String, Object>> getLearningPathDetails(@PathVariable Long pathId) {
        log.info("获取学习路径详情: {}", pathId);
        try {
            Map<String, Object> details = intelligentLearningService.getLearningPathDetails(pathId);
            if (details != null) {
                return Result.success("获取成功", details);
            } else {
                return Result.error("路径不存在");
            }
        } catch (Exception e) {
            log.error("获取学习路径详情失败", e);
            return Result.error("获取失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取下一步学习建议
     */
    @GetMapping("/learning-path/{pathId}/next-step")
    public Result<Map<String, Object>> getNextLearningStep(@PathVariable Long pathId, @RequestParam Long studentId) {
        log.info("获取下一步学习建议: pathId={}, studentId={}", pathId, studentId);
        try {
            Map<String, Object> nextStep = intelligentLearningService.getNextLearningStep(studentId, pathId);
            if (nextStep != null) {
                return Result.success("获取成功", nextStep);
            } else {
                return Result.error("暂无下一步建议");
            }
        } catch (Exception e) {
            log.error("获取下一步学习建议失败", e);
            return Result.error("获取失败: " + e.getMessage());
        }
    }
    
    // ============ 情绪感知学习助手API ============
    
    /**
     * 记录学生情绪状态
     */
    @PostMapping("/emotion/record")
    public Result<Boolean> recordEmotionState(@RequestBody Map<String, Object> data) {
        log.info("记录情绪状态: {}", data);
        try {
            Long studentId = Long.valueOf(data.get("studentId").toString());
            String sessionId = (String) data.get("sessionId");
            String emotionType = (String) data.get("emotionType");
            Double intensity = Double.valueOf(data.get("intensity").toString());
            String context = (String) data.getOrDefault("context", "");
            
            boolean success = intelligentLearningService.recordEmotionState(studentId, sessionId, emotionType, intensity, context);
            
            if (success) {
                return Result.success("情绪记录成功", true);
            } else {
                return Result.error("情绪记录失败");
            }
        } catch (Exception e) {
            log.error("记录情绪状态失败", e);
            return Result.error("记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 分析情绪并获取干预建议
     */
    @PostMapping("/emotion/analyze")
    public Result<Map<String, Object>> analyzeEmotionAndSuggest(@RequestBody Map<String, Object> data) {
        log.info("分析情绪请求: {}", data);
        try {
            Long studentId = Long.valueOf(data.get("studentId").toString());
            String sessionId = (String) data.get("sessionId");
            
            Map<String, Object> analysis = intelligentLearningService.analyzeEmotionAndSuggest(studentId, sessionId);
            
            if (analysis != null) {
                return Result.success("分析完成", analysis);
            } else {
                return Result.error("暂无数据可分析");
            }
        } catch (Exception e) {
            log.error("分析情绪失败", e);
            return Result.error("分析失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取情绪趋势分析
     */
    @GetMapping("/emotion/trend/{studentId}")
    public Result<Map<String, Object>> getEmotionTrend(@PathVariable Long studentId, @RequestParam(defaultValue = "7") Integer days) {
        log.info("获取情绪趋势: studentId={}, days={}", studentId, days);
        try {
            Map<String, Object> trend = intelligentLearningService.getEmotionTrendAnalysis(studentId, days);
            return Result.success("获取成功", trend);
        } catch (Exception e) {
            log.error("获取情绪趋势失败", e);
            return Result.error("获取失败: " + e.getMessage());
        }
    }
    
    /**
     * 记录学习状态监控数据
     */
    @PostMapping("/emotion/learning-status")
    public Result<Boolean> recordLearningStatus(@RequestBody Map<String, Object> data) {
        log.info("记录学习状态: {}", data);
        try {
            Long studentId = Long.valueOf(data.get("studentId").toString());
            String sessionId = (String) data.get("sessionId");
            
            boolean success = intelligentLearningService.recordLearningStatus(studentId, sessionId, data);
            
            if (success) {
                return Result.success("状态记录成功", true);
            } else {
                return Result.error("状态记录失败");
            }
        } catch (Exception e) {
            log.error("记录学习状态失败", e);
            return Result.error("记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取学习状态统计
     */
    @GetMapping("/emotion/status-stats/{studentId}")
    public Result<Map<String, Object>> getLearningStatusStats(@PathVariable Long studentId, @RequestParam(defaultValue = "24") Integer hours) {
        log.info("获取学习状态统计: studentId={}, hours={}", studentId, hours);
        try {
            Map<String, Object> stats = intelligentLearningService.getLearningStatusStats(studentId, hours);
            return Result.success("获取成功", stats);
        } catch (Exception e) {
            log.error("获取学习状态统计失败", e);
            return Result.error("获取失败: " + e.getMessage());
        }
    }
    
    // ============ 智能同伴学习API ============
    
    /**
     * 智能匹配学习伙伴
     */
    @GetMapping("/companion/find/{studentId}")
    public Result<List<Map<String, Object>>> findLearningCompanions(@PathVariable Long studentId, @RequestParam(defaultValue = "10") Integer limit) {
        log.info("查找学习伙伴: studentId={}, limit={}", studentId, limit);
        try {
            List<Map<String, Object>> companions = intelligentLearningService.findLearningCompanions(studentId, limit);
            return Result.success("查找成功", companions);
        } catch (Exception e) {
            log.error("查找学习伙伴失败", e);
            return Result.error("查找失败: " + e.getMessage());
        }
    }
    
    /**
     * 创建学习伙伴关系
     */
    @PostMapping("/companion/create")
    public Result<Boolean> createCompanionRelationship(@RequestBody Map<String, Object> data) {
        log.info("创建学习伙伴关系: {}", data);
        try {
            Long student1Id = Long.valueOf(data.get("student1Id").toString());
            Long student2Id = Long.valueOf(data.get("student2Id").toString());
            
            boolean success = intelligentLearningService.createCompanionRelationship(student1Id, student2Id);
            
            if (success) {
                return Result.success("伙伴关系创建成功", true);
            } else {
                return Result.error("伙伴关系创建失败");
            }
        } catch (Exception e) {
            log.error("创建学习伙伴关系失败", e);
            return Result.error("创建失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取我的学习伙伴
     */
    @GetMapping("/companion/my/{studentId}")
    public Result<List<Map<String, Object>>> getMyLearningCompanions(@PathVariable Long studentId) {
        log.info("获取我的学习伙伴: {}", studentId);
        try {
            List<Map<String, Object>> companions = intelligentLearningService.getMyLearningCompanions(studentId);
            return Result.success("获取成功", companions);
        } catch (Exception e) {
            log.error("获取学习伙伴失败", e);
            return Result.error("获取失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新伙伴关系状态
     */
    @PostMapping("/companion/status")
    public Result<Boolean> updateCompanionStatus(@RequestBody Map<String, Object> data) {
        log.info("更新伙伴关系状态: {}", data);
        try {
            Long matchId = Long.valueOf(data.get("matchId").toString());
            String status = (String) data.get("status");
            
            boolean success = intelligentLearningService.updateCompanionStatus(matchId, status);
            
            if (success) {
                return Result.success("状态更新成功", true);
            } else {
                return Result.error("状态更新失败");
            }
        } catch (Exception e) {
            log.error("更新伙伴关系状态失败", e);
            return Result.error("更新失败: " + e.getMessage());
        }
    }
    
    /**
     * 创建学习小组
     */
    @PostMapping("/study-group/create")
    public Result<Map<String, Object>> createStudyGroup(@RequestBody Map<String, Object> data) {
        log.info("创建学习小组: {}", data);
        try {
            Map<String, Object> result = intelligentLearningService.createStudyGroup(data);
            
            if (result != null) {
                return Result.success("学习小组创建成功", result);
            } else {
                return Result.error("学习小组创建失败");
            }
        } catch (Exception e) {
            log.error("创建学习小组失败", e);
            return Result.error("创建失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取学习小组列表
     */
    @GetMapping("/study-group/list/{studentId}")
    public Result<List<Map<String, Object>>> getStudyGroups(@PathVariable Long studentId, 
                                                           @RequestParam(required = false) String subject, 
                                                           @RequestParam(defaultValue = "20") Integer limit) {
        log.info("获取学习小组列表: studentId={}, subject={}, limit={}", studentId, subject, limit);
        try {
            List<Map<String, Object>> groups = intelligentLearningService.getStudyGroups(studentId, subject, limit);
            return Result.success("获取成功", groups);
        } catch (Exception e) {
            log.error("获取学习小组列表失败", e);
            return Result.error("获取失败: " + e.getMessage());
        }
    }
    
    /**
     * 加入学习小组
     */
    @PostMapping("/study-group/join")
    public Result<Boolean> joinStudyGroup(@RequestBody Map<String, Object> data) {
        log.info("加入学习小组: {}", data);
        try {
            Long groupId = Long.valueOf(data.get("groupId").toString());
            Long studentId = Long.valueOf(data.get("studentId").toString());
            
            boolean success = intelligentLearningService.joinStudyGroup(groupId, studentId);
            
            if (success) {
                return Result.success("加入成功", true);
            } else {
                return Result.error("加入失败");
            }
        } catch (Exception e) {
            log.error("加入学习小组失败", e);
            return Result.error("加入失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取小组成员
     */
    @GetMapping("/study-group/{groupId}/members")
    public Result<List<Map<String, Object>>> getGroupMembers(@PathVariable Long groupId) {
        log.info("获取小组成员: {}", groupId);
        try {
            List<Map<String, Object>> members = intelligentLearningService.getGroupMembers(groupId);
            return Result.success("获取成功", members);
        } catch (Exception e) {
            log.error("获取小组成员失败", e);
            return Result.error("获取失败: " + e.getMessage());
        }
    }
    
    /**
     * 创建协作学习会话
     */
    @PostMapping("/collaborative-session/create")
    public Result<Map<String, Object>> createCollaborativeSession(@RequestBody Map<String, Object> data) {
        log.info("创建协作学习会话: {}", data);
        try {
            Map<String, Object> result = intelligentLearningService.createCollaborativeSession(data);
            
            if (result != null) {
                return Result.success("会话创建成功", result);
            } else {
                return Result.error("会话创建失败");
            }
        } catch (Exception e) {
            log.error("创建协作学习会话失败", e);
            return Result.error("创建失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取即将到来的协作会话
     */
    @GetMapping("/collaborative-session/upcoming")
    public Result<List<Map<String, Object>>> getUpcomingSessions(@RequestParam(defaultValue = "10") Integer limit) {
        log.info("获取即将到来的协作会话: limit={}", limit);
        try {
            List<Map<String, Object>> sessions = intelligentLearningService.getUpcomingSessions(limit);
            return Result.success("获取成功", sessions);
        } catch (Exception e) {
            log.error("获取协作会话失败", e);
            return Result.error("获取失败: " + e.getMessage());
        }
    }
    
    /**
     * 记录协作互动
     */
    @PostMapping("/collaborative-session/interaction")
    public Result<Boolean> recordCollaborativeInteraction(@RequestBody Map<String, Object> data) {
        log.info("记录协作互动: {}", data);
        try {
            Long sessionId = Long.valueOf(data.get("sessionId").toString());
            Long studentId = Long.valueOf(data.get("studentId").toString());
            String interactionType = (String) data.get("interactionType");
            String content = (String) data.get("content");
            
            boolean success = intelligentLearningService.recordCollaborativeInteraction(sessionId, studentId, interactionType, content);
            
            if (success) {
                return Result.success("互动记录成功", true);
            } else {
                return Result.error("互动记录失败");
            }
        } catch (Exception e) {
            log.error("记录协作互动失败", e);
            return Result.error("记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 评价协作伙伴
     */
    @PostMapping("/companion/rate")
    public Result<Boolean> rateCollaborativePartner(@RequestBody Map<String, Object> data) {
        log.info("评价协作伙伴: {}", data);
        try {
            Long matchId = Long.valueOf(data.get("matchId").toString());
            Double rating = Double.valueOf(data.get("rating").toString());
            
            boolean success = intelligentLearningService.rateCollaborativePartner(matchId, rating);
            
            if (success) {
                return Result.success("评价成功", true);
            } else {
                return Result.error("评价失败");
            }
        } catch (Exception e) {
            log.error("评价协作伙伴失败", e);
            return Result.error("评价失败: " + e.getMessage());
        }
    }
} 