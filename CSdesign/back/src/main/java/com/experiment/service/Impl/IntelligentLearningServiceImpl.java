package com.experiment.service.Impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.experiment.mapper.CompanionMapper;
import com.experiment.mapper.EmotionMapper;
import com.experiment.mapper.LearningPathMapper;
import com.experiment.pojo.EmotionRecord;
import com.experiment.pojo.LearningCompanion;
import com.experiment.pojo.LearningPath;
import com.experiment.pojo.StudyGroup;
import com.experiment.service.IntelligentLearningService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class IntelligentLearningServiceImpl implements IntelligentLearningService {
    
    @Autowired
    private LearningPathMapper learningPathMapper;
    
    @Autowired
    private EmotionMapper emotionMapper;
    
    @Autowired
    private CompanionMapper companionMapper;
    
    // ============ 智能学习路径规划实现 ============
    
    @Override
    public Map<String, Object> generatePersonalizedPath(Long studentId, String subject, String targetLevel) {
        try {
            log.info("为学生 {} 生成 {} 学科的个性化学习路径，目标级别: {}", studentId, subject, targetLevel);
            
            // 1. 获取学生能力评估
            List<Map<String, Object>> recommendations = learningPathMapper.generateRecommendedPath(studentId, subject);
            
            // 2. 获取路径模板
            List<Map<String, Object>> templates = learningPathMapper.getPathTemplates(subject, targetLevel);
            
            // 3. 创建个性化路径
            LearningPath path = new LearningPath();
            path.setStudentId(studentId);
            path.setTemplateId(templates.isEmpty() ? null : (Long) templates.get(0).get("id"));
            path.setName(subject + "个性化学习路径");
            path.setCurrentStep(1);
            path.setCompletionRate(BigDecimal.ZERO);
            path.setStartTime(LocalDateTime.now());
            path.setStatus("active");
            
            // 计算预计完成时间（基于难度和学生能力）
            LocalDateTime estimatedCompletion = LocalDateTime.now().plusDays(30); // 默认30天
            path.setEstimatedCompletion(estimatedCompletion);
            
            int result = learningPathMapper.createLearningPath(path);
            
            if (result > 0) {
                Map<String, Object> response = new HashMap<>();
                response.put("pathId", path.getId());
                response.put("pathName", path.getName());
                response.put("estimatedCompletion", estimatedCompletion);
                response.put("recommendations", recommendations);
                response.put("message", "学习路径生成成功");
                return response;
            }
            
            return null;
        } catch (Exception e) {
            log.error("生成学习路径失败", e);
            return null;
        }
    }
    
    @Override
    public List<Map<String, Object>> getStudentLearningPaths(Long studentId) {
        try {
            List<LearningPath> paths = learningPathMapper.findByStudentId(studentId);
            List<Map<String, Object>> result = new ArrayList<>();
            
            for (LearningPath path : paths) {
                Map<String, Object> pathInfo = new HashMap<>();
                pathInfo.put("id", path.getId());
                pathInfo.put("name", path.getName());
                pathInfo.put("currentStep", path.getCurrentStep());
                pathInfo.put("completionRate", path.getCompletionRate());
                pathInfo.put("status", path.getStatus());
                pathInfo.put("startTime", path.getStartTime());
                pathInfo.put("estimatedCompletion", path.getEstimatedCompletion());
                result.add(pathInfo);
            }
            
            return result;
        } catch (Exception e) {
            log.error("获取学生学习路径失败", e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public boolean updateLearningProgress(Long pathId, Integer stepNumber, Double masteryLevel, Integer studyDuration) {
        try {
            String completeTime = masteryLevel >= 0.8 ? LocalDateTime.now().toString() : null;
            String status = masteryLevel >= 0.8 ? "completed" : "in_progress";
            
            int result = learningPathMapper.updateStepStatus(pathId, stepNumber, status, masteryLevel, studyDuration, completeTime);
            
            // 更新整体路径进度
            if (result > 0 && masteryLevel >= 0.8) {
                // 这里可以添加计算整体完成率的逻辑
                return true;
            }
            
            return result > 0;
        } catch (Exception e) {
            log.error("更新学习进度失败", e);
            return false;
        }
    }
    
    @Override
    public Map<String, Object> getLearningPathDetails(Long pathId) {
        try {
            List<Map<String, Object>> details = learningPathMapper.getPathDetails(pathId);
            
            if (!details.isEmpty()) {
                Map<String, Object> result = new HashMap<>();
                result.put("pathInfo", details.get(0));
                result.put("steps", details);
                return result;
            }
            
            return null;
        } catch (Exception e) {
            log.error("获取学习路径详情失败", e);
            return null;
        }
    }
    
    @Override
    public Map<String, Object> getNextLearningStep(Long studentId, Long pathId) {
        try {
            List<Map<String, Object>> details = learningPathMapper.getPathDetails(pathId);
            
            // 找到下一个待学习的步骤
            for (Map<String, Object> step : details) {
                String stepStatus = (String) step.get("step_status");
                if ("pending".equals(stepStatus) || stepStatus == null) {
                    Map<String, Object> nextStep = new HashMap<>();
                    nextStep.put("stepNumber", step.get("step_number"));
                    nextStep.put("knowledgeName", step.get("knowledge_name"));
                    nextStep.put("difficultyLevel", step.get("difficulty_level"));
                    nextStep.put("recommendation", "建议先复习相关基础知识，然后进行练习");
                    return nextStep;
                }
            }
            
            return null;
        } catch (Exception e) {
            log.error("获取下一步学习内容失败", e);
            return null;
        }
    }
    
    // ============ 情绪感知学习助手实现 ============
    
    @Override
    public boolean recordEmotionState(Long studentId, String sessionId, String emotionType, Double intensity, String context) {
        try {
            EmotionRecord record = new EmotionRecord();
            record.setStudentId(studentId);
            record.setSessionId(sessionId);
            record.setDetectionType("behavioral");
            record.setEmotionType(emotionType);
            record.setConfidence(BigDecimal.valueOf(0.8)); // 默认置信度
            record.setIntensity(BigDecimal.valueOf(intensity));
            record.setContext(context);
            record.setDetectionTime(LocalDateTime.now());
            
            int result = emotionMapper.recordEmotion(record);
            return result > 0;
        } catch (Exception e) {
            log.error("记录情绪状态失败", e);
            return false;
        }
    }
    
    @Override
    public Map<String, Object> analyzeEmotionAndSuggest(Long studentId, String sessionId) {
        try {
            // 获取最近的情绪记录
            List<EmotionRecord> recentEmotions = emotionMapper.getRecentEmotions(studentId, sessionId, 5);
            
            if (recentEmotions.isEmpty()) {
                return null;
            }
            
            EmotionRecord latestEmotion = recentEmotions.get(0);
            
            // 获取干预建议
            List<Map<String, Object>> suggestions = emotionMapper.getSuggestions(
                latestEmotion.getEmotionType(), 
                latestEmotion.getIntensity().doubleValue()
            );
            
            Map<String, Object> result = new HashMap<>();
            result.put("currentEmotion", latestEmotion.getEmotionType());
            result.put("intensity", latestEmotion.getIntensity());
            result.put("suggestions", suggestions);
            result.put("detectionTime", latestEmotion.getDetectionTime());
            
            // 如果情绪强度较高且为负面情绪，记录干预
            if (latestEmotion.getIntensity().doubleValue() > 0.7 && 
                Arrays.asList("frustrated", "stressed", "bored", "confused").contains(latestEmotion.getEmotionType())) {
                
                if (!suggestions.isEmpty()) {
                    Map<String, Object> topSuggestion = suggestions.get(0);
                    emotionMapper.recordIntervention(
                        studentId, 
                        latestEmotion.getId(),
                        (String) topSuggestion.get("suggestion_type"),
                        (String) topSuggestion.get("suggestion_content"),
                        0.7
                    );
                }
            }
            
            return result;
        } catch (Exception e) {
            log.error("分析情绪并提供建议失败", e);
            return null;
        }
    }
    
    @Override
    public Map<String, Object> getEmotionTrendAnalysis(Long studentId, Integer days) {
        try {
            List<Map<String, Object>> trends = emotionMapper.analyzeEmotionTrend(studentId, days);
            
            Map<String, Object> result = new HashMap<>();
            result.put("trends", trends);
            result.put("analysisDate", LocalDateTime.now());
            result.put("period", days + "天");
            
            // 计算情绪摘要
            Map<String, Integer> emotionSummary = new HashMap<>();
            for (Map<String, Object> trend : trends) {
                String emotion = (String) trend.get("emotion_type");
                Long count = (Long) trend.get("count");
                emotionSummary.put(emotion, emotionSummary.getOrDefault(emotion, 0) + count.intValue());
            }
            result.put("emotionSummary", emotionSummary);
            
            return result;
        } catch (Exception e) {
            log.error("获取情绪趋势分析失败", e);
            return null;
        }
    }
    
    @Override
    public boolean recordLearningStatus(Long studentId, String sessionId, Map<String, Object> statusData) {
        try {
            return emotionMapper.recordLearningStatus(
                studentId, sessionId,
                (Double) statusData.getOrDefault("focusLevel", 0.8),
                (Double) statusData.getOrDefault("engagementScore", 0.7),
                (Double) statusData.getOrDefault("fatigueLevel", 0.2),
                (Double) statusData.getOrDefault("learningEfficiency", 0.75),
                (Integer) statusData.getOrDefault("interactionFrequency", 0),
                (Integer) statusData.getOrDefault("pauseDuration", 0),
                (Double) statusData.getOrDefault("scrollSpeed", 0.0),
                (String) statusData.get("clickPattern"),
                (String) statusData.get("typingPattern"),
                (Long) statusData.get("courseId")
            ) > 0;
        } catch (Exception e) {
            log.error("记录学习状态失败", e);
            return false;
        }
    }
    
    @Override
    public Map<String, Object> getLearningStatusStats(Long studentId, Integer hours) {
        try {
            return emotionMapper.getLearningStatusStats(studentId, hours);
        } catch (Exception e) {
            log.error("获取学习状态统计失败", e);
            return new HashMap<>();
        }
    }
    
    @Override
    public boolean updateInterventionFeedback(Long interventionId, Double effectivenessScore, String feedback) {
        try {
            return emotionMapper.updateInterventionFeedback(interventionId, effectivenessScore, feedback) > 0;
        } catch (Exception e) {
            log.error("更新干预反馈失败", e);
            return false;
        }
    }
    
    // ============ 智能同伴学习实现 ============
    
    @Override
    public List<Map<String, Object>> findLearningCompanions(Long studentId, Integer limit) {
        try {
            return companionMapper.findPotentialCompanions(studentId, limit);
        } catch (Exception e) {
            log.error("查找学习伙伴失败", e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public boolean createCompanionRelationship(Long student1Id, Long student2Id) {
        try {
            LearningCompanion companion = new LearningCompanion();
            companion.setStudent1Id(student1Id);
            companion.setStudent2Id(student2Id);
            companion.setMatchingScore(BigDecimal.valueOf(0.75)); // 默认匹配度
            companion.setStatus("pending");
            companion.setMatchTime(LocalDateTime.now());
            
            return companionMapper.createCompanionMatch(companion) > 0;
        } catch (Exception e) {
            log.error("创建学习伙伴关系失败", e);
            return false;
        }
    }
    
    @Override
    public List<Map<String, Object>> getMyLearningCompanions(Long studentId) {
        try {
            return companionMapper.findCompanionMatches(studentId, "active");
        } catch (Exception e) {
            log.error("获取学习伙伴列表失败", e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public boolean updateCompanionStatus(Long matchId, String status) {
        try {
            String activationTime = "active".equals(status) ? LocalDateTime.now().toString() : null;
            return companionMapper.updateCompanionStatus(matchId, status, activationTime) > 0;
        } catch (Exception e) {
            log.error("更新伙伴关系状态失败", e);
            return false;
        }
    }
    
    @Override
    public Map<String, Object> createStudyGroup(Map<String, Object> groupData) {
        try {
            StudyGroup group = new StudyGroup();
            group.setGroupName((String) groupData.get("groupName"));
            group.setGroupDescription((String) groupData.get("groupDescription"));
            group.setSubject((String) groupData.get("subject"));
            group.setLeaderId((Long) groupData.get("leaderId"));
            group.setMaxMembers((Integer) groupData.getOrDefault("maxMembers", 6));
            group.setGroupType((String) groupData.getOrDefault("groupType", "study"));
            group.setPrivacyLevel((String) groupData.getOrDefault("privacyLevel", "public"));
            group.setLearningGoals((String) groupData.get("learningGoals"));
            group.setGroupRules((String) groupData.get("groupRules"));
            group.setStatus("active");
            
            int result = companionMapper.createStudyGroup(group);
            
            if (result > 0) {
                // 创建者自动加入小组
                companionMapper.joinStudyGroup(group.getId(), group.getLeaderId(), "leader");
                
                Map<String, Object> response = new HashMap<>();
                response.put("groupId", group.getId());
                response.put("message", "学习小组创建成功");
                return response;
            }
            
            return null;
        } catch (Exception e) {
            log.error("创建学习小组失败", e);
            return null;
        }
    }
    
    @Override
    public List<Map<String, Object>> getStudyGroups(Long studentId, String subject, Integer limit) {
        try {
            String searchCondition = "";
            if (subject != null && !subject.isEmpty()) {
                searchCondition = "AND sg.subject = '" + subject + "'";
            }
            return companionMapper.getStudyGroups(studentId, searchCondition, limit);
        } catch (Exception e) {
            log.error("获取学习小组列表失败", e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public boolean joinStudyGroup(Long groupId, Long studentId) {
        try {
            return companionMapper.joinStudyGroup(groupId, studentId, "member") > 0;
        } catch (Exception e) {
            log.error("加入学习小组失败", e);
            return false;
        }
    }
    
    @Override
    public List<Map<String, Object>> getGroupMembers(Long groupId) {
        try {
            return companionMapper.getGroupMembers(groupId);
        } catch (Exception e) {
            log.error("获取小组成员失败", e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public Map<String, Object> createCollaborativeSession(Map<String, Object> sessionData) {
        try {
            int result = companionMapper.createCollaborativeSession(
                (String) sessionData.get("sessionName"),
                (String) sessionData.get("sessionType"),
                (Long) sessionData.get("creatorId"),
                (String) sessionData.get("subject"),
                (String) sessionData.get("topic"),
                (Integer) sessionData.getOrDefault("maxParticipants", 4),
                (String) sessionData.get("sessionDescription"),
                (String) sessionData.get("startTime"),
                (Integer) sessionData.get("estimatedDuration"),
                (String) sessionData.get("learningGoals")
            );
            
            if (result > 0) {
                Map<String, Object> response = new HashMap<>();
                response.put("sessionId", (long) result);
                response.put("message", "协作学习会话创建成功");
                return response;
            }
            
            return null;
        } catch (Exception e) {
            log.error("创建协作学习会话失败", e);
            return null;
        }
    }
    
    @Override
    public List<Map<String, Object>> getUpcomingSessions(Integer limit) {
        try {
            return companionMapper.getUpcomingSessions(limit);
        } catch (Exception e) {
            log.error("获取即将到来的协作会话失败", e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public boolean recordCollaborativeInteraction(Long sessionId, Long studentId, String interactionType, String content) {
        try {
            return companionMapper.recordInteraction(sessionId, studentId, interactionType, content, null, null) > 0;
        } catch (Exception e) {
            log.error("记录协作互动失败", e);
            return false;
        }
    }
    
    @Override
    public boolean rateCollaborativePartner(Long matchId, Double rating) {
        try {
            return companionMapper.updateCollaborationRating(matchId, rating) > 0;
        } catch (Exception e) {
            log.error("评价协作伙伴失败", e);
            return false;
        }
    }
} 