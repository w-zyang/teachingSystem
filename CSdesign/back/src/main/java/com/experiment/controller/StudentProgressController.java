package com.experiment.controller;

import com.experiment.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/student-progress")
@CrossOrigin
public class StudentProgressController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 更新学习进度
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @param progressData 进度数据
     */
    @PostMapping("/update")
    public Result<String> updateProgress(
            @RequestParam Long studentId,
            @RequestParam Long courseId,
            @RequestBody Map<String, Object> progressData) {
        
        try {
            // 获取当前进度
            String sql = "SELECT progress, study_duration FROM student_progress WHERE student_id = ? AND course_id = ?";
            Map<String, Object> current = jdbcTemplate.queryForMap(sql, studentId, courseId);
            
            int currentProgress = (Integer) current.get("progress");
            int currentDuration = (Integer) current.get("study_duration");
            
            // 计算新进度
            Integer increment = (Integer) progressData.get("increment");
            Integer duration = (Integer) progressData.get("duration");
            
            int newProgress = Math.min(100, currentProgress + (increment != null ? increment : 0));
            int newDuration = currentDuration + (duration != null ? duration : 0);
            
            // 更新进度
            String updateSql = "UPDATE student_progress SET progress = ?, study_duration = ?, update_time = NOW() WHERE student_id = ? AND course_id = ?";
            jdbcTemplate.update(updateSql, newProgress, newDuration, studentId, courseId);
            
            log.info("更新学习进度成功 - 学生ID: {}, 课程ID: {}, 新进度: {}%", studentId, courseId, newProgress);
            
            return Result.success("学习进度已更新", String.valueOf(newProgress));
            
        } catch (Exception e) {
            log.error("更新学习进度失败", e);
            return Result.error("更新学习进度失败: " + e.getMessage());
        }
    }

    /**
     * 记录资源学习
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @param resourceId 资源ID
     */
    @PostMapping("/record-resource")
    public Result<Map<String, Object>> recordResourceStudy(
            @RequestParam Long studentId,
            @RequestParam Long courseId,
            @RequestParam Long resourceId) {
        
        try {
            // 检查是否已经学习过这个资源
            String checkSql = "SELECT COUNT(*) FROM student_resource_progress WHERE student_id = ? AND resource_id = ?";
            Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, studentId, resourceId);
            
            if (count == 0) {
                // 第一次学习这个资源,记录并增加进度
                String insertSql = "INSERT INTO student_resource_progress (student_id, resource_id, status, view_time, view_count) VALUES (?, ?, 'viewed', NOW(), 1)";
                jdbcTemplate.update(insertSql, studentId, resourceId);
                
                // 获取课程总资源数
                String countResourcesSql = "SELECT COUNT(*) FROM course_resource WHERE course_id = ? AND status = 'published'";
                Integer totalResources = jdbcTemplate.queryForObject(countResourcesSql, Integer.class, courseId);
                
                // 计算进度增量 (每个资源占总进度的一部分)
                int progressIncrement = totalResources > 0 ? (100 / totalResources) : 5;
                
                // 更新课程进度
                String updateSql = "UPDATE student_progress SET progress = LEAST(100, progress + ?), study_duration = study_duration + 5, update_time = NOW() WHERE student_id = ? AND course_id = ?";
                jdbcTemplate.update(updateSql, progressIncrement, studentId, courseId);
                
                // 获取更新后的进度信息
                String getProgressSql = "SELECT progress FROM student_progress WHERE student_id = ? AND course_id = ?";
                Integer currentProgress = jdbcTemplate.queryForObject(getProgressSql, Integer.class, studentId, courseId);
                log.info("查询到的当前进度: {}", currentProgress);
                
                // 获取已学习的资源数
                String studiedCountSql = "SELECT COUNT(*) FROM student_resource_progress WHERE student_id = ? AND resource_id IN (SELECT id FROM course_resource WHERE course_id = ?)";
                Integer studiedCount = jdbcTemplate.queryForObject(studiedCountSql, Integer.class, studentId, courseId);
                log.info("已学习资源数: {}, 总资源数: {}", studiedCount, totalResources);
                
                log.info("首次学习资源 - 学生ID: {}, 资源ID: {}, 进度增加: {}%", studentId, resourceId, progressIncrement);
                
                // 返回进度数据给前端
                Map<String, Object> progressData = new java.util.HashMap<>();
                progressData.put("progress", currentProgress);
                progressData.put("studiedResourceCount", studiedCount);
                progressData.put("totalResourceCount", totalResources);
                progressData.put("progressIncrement", progressIncrement);
                
                return Result.success("学习记录已保存,进度已更新", progressData);
            } else {
                // 已经学习过,检查上次学习时间
                String getLastViewSql = "SELECT view_time, view_count FROM student_resource_progress WHERE student_id = ? AND resource_id = ?";
                Map<String, Object> lastView = jdbcTemplate.queryForMap(getLastViewSql, studentId, resourceId);
                
                // 修复类型转换问题：使用 Object 然后转换为时间戳
                Object viewTimeObj = lastView.get("view_time");
                long lastViewTimeMillis;
                
                if (viewTimeObj instanceof java.sql.Timestamp) {
                    lastViewTimeMillis = ((java.sql.Timestamp) viewTimeObj).getTime();
                } else if (viewTimeObj instanceof java.time.LocalDateTime) {
                    lastViewTimeMillis = java.sql.Timestamp.valueOf((java.time.LocalDateTime) viewTimeObj).getTime();
                } else {
                    lastViewTimeMillis = System.currentTimeMillis();
                }
                
                Integer viewCount = (Integer) lastView.get("view_count");
                
                // 计算距离上次学习的时间(分钟)
                long minutesSinceLastView = (System.currentTimeMillis() - lastViewTimeMillis) / (1000 * 60);
                
                // 只有距离上次学习超过5分钟,才更新学习时长和次数
                if (minutesSinceLastView >= 5) {
                    // 更新查看次数和时间
                    String updateViewSql = "UPDATE student_resource_progress SET view_time = NOW(), view_count = view_count + 1 WHERE student_id = ? AND resource_id = ?";
                    jdbcTemplate.update(updateViewSql, studentId, resourceId);
                    
                    // 只增加学习时长,不增加进度
                    String updateDurationSql = "UPDATE student_progress SET study_duration = study_duration + 2, update_time = NOW() WHERE student_id = ? AND course_id = ?";
                    jdbcTemplate.update(updateDurationSql, studentId, courseId);
                    
                    log.info("重复学习资源 - 学生ID: {}, 资源ID: {}, 查看次数: {}, 距上次: {}分钟", 
                            studentId, resourceId, viewCount + 1, minutesSinceLastView);
                }
                
                // 无论是否更新，都返回当前进度信息
                String getProgressSql = "SELECT progress FROM student_progress WHERE student_id = ? AND course_id = ?";
                Integer currentProgress = jdbcTemplate.queryForObject(getProgressSql, Integer.class, studentId, courseId);
                
                String countResourcesSql = "SELECT COUNT(*) FROM course_resource WHERE course_id = ? AND status = 'published'";
                Integer totalResources = jdbcTemplate.queryForObject(countResourcesSql, Integer.class, courseId);
                
                String studiedCountSql = "SELECT COUNT(*) FROM student_resource_progress WHERE student_id = ? AND resource_id IN (SELECT id FROM course_resource WHERE course_id = ?)";
                Integer studiedCount = jdbcTemplate.queryForObject(studiedCountSql, Integer.class, studentId, courseId);
                
                Map<String, Object> result = new java.util.HashMap<>();
                result.put("progress", currentProgress);
                result.put("studiedResourceCount", studiedCount);
                result.put("totalResourceCount", totalResources);
                result.put("message", minutesSinceLastView >= 5 ? "学习时长已更新" : "已记录");
                
                log.info("返回进度信息 - 进度: {}%, 已学: {}/{}", currentProgress, studiedCount, totalResources);
                
                return Result.success(minutesSinceLastView >= 5 ? "学习时长已更新" : "已记录", result);
            }
            
        } catch (Exception e) {
            log.error("记录资源学习失败", e);
            // 如果表不存在,先创建表
            if (e.getMessage().contains("doesn't exist")) {
                try {
                    createResourceProgressTable();
                    return recordResourceStudy(studentId, courseId, resourceId);
                } catch (Exception ex) {
                    return Result.error("记录学习失败: " + ex.getMessage());
                }
            }
            return Result.error("记录学习失败: " + e.getMessage());
        }
    }

    /**
     * 创建资源学习进度表
     */
    private void createResourceProgressTable() {
        String sql = "CREATE TABLE IF NOT EXISTS student_resource_progress (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                "student_id BIGINT NOT NULL," +
                "resource_id BIGINT NOT NULL," +
                "status VARCHAR(20) DEFAULT 'not_started'," +
                "view_time DATETIME," +
                "view_count INT DEFAULT 0," +
                "complete_time DATETIME," +
                "create_time DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                "UNIQUE KEY uk_student_resource (student_id, resource_id)" +
                ")";
        jdbcTemplate.execute(sql);
        log.info("创建student_resource_progress表成功");
    }

    /**
     * 获取学生的课程进度
     */
    @GetMapping("/course/{studentId}/{courseId}")
    public Result<Map<String, Object>> getCourseProgress(
            @PathVariable Long studentId,
            @PathVariable Long courseId) {
        
        try {
            String sql = "SELECT * FROM student_progress WHERE student_id = ? AND course_id = ?";
            Map<String, Object> progress = jdbcTemplate.queryForMap(sql, studentId, courseId);
            return Result.success("获取进度成功", progress);
        } catch (Exception e) {
            log.error("获取进度失败", e);
            return Result.error("获取进度失败: " + e.getMessage());
        }
    }
}

