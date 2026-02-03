package com.experiment.service.Impl;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.aigc.generation.models.QwenParam;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.experiment.config.DashScopeConfig;
import com.experiment.mapper.*;
import com.experiment.pojo.*;
import com.experiment.service.SmartTeachingAssistantService;
import com.experiment.utils.PPTDocumentReader;
import com.experiment.utils.WordDocumentReader;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 智能备课助手服务实现类（精简版 - 避免功能冲突）
 */
@Slf4j
@Service
public class SmartTeachingAssistantServiceImpl implements SmartTeachingAssistantService, InitializingBean {

    @Autowired
    private CoursewareUpdateMapper coursewareUpdateMapper;

    @Autowired
    private ExperimentGuideGenerationMapper experimentGuideGenerationMapper;

    @Autowired
    private HomeworkGradingMapper homeworkGradingMapper;

    @Autowired
    private DashScopeConfig dashScopeConfig;

    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // 构造函数 - 用于检查配置
    public SmartTeachingAssistantServiceImpl() {
        log.info("SmartTeachingAssistantServiceImpl 初始化");
    }
    
    // 实现 InitializingBean 接口的方法
    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("=== DashScope配置检查 ===");
        log.info("API Key: {}", dashScopeConfig.getApiKey() != null ? "已配置(长度:" + dashScopeConfig.getApiKey().length() + ")" : "未配置");
        log.info("Model: {}", dashScopeConfig.getModel());
        log.info("App ID: {}", dashScopeConfig.getAppId());
        log.info("========================");
    }

    // ==================== 1. 课件更新助手 ====================

    @Override
    public Long updateCoursewareCases(Long teacherId, Long courseId, String fileUrl, String fileName, String updateRequirements) {
        log.info("开始分析课件更新需求 - 教师ID: {}, 文件: {}", teacherId, fileName);

        try {
            // 1. 创建更新记录
            CoursewareUpdate record = new CoursewareUpdate();
            record.setTeacherId(teacherId);
            record.setCourseId(courseId);
            record.setOriginalFileUrl(fileUrl);
            record.setOriginalFileName(fileName);
            record.setUpdateType("case_update");
            record.setUpdateRequirements(updateRequirements);
            record.setStatus("analyzing");

            coursewareUpdateMapper.insert(record);
            Long recordId = record.getId();

            // 2. 异步生成更新建议
            new Thread(() -> {
                try {
                    // 读取PPT内容
                    log.info("开始读取PPT文件内容: {}", fileUrl);
                    String pptContent = PPTDocumentReader.readPPTDocument(fileUrl);
                    
                    if (pptContent == null || pptContent.trim().isEmpty()) {
                        log.warn("PPT内容为空，使用文件名生成建议");
                        pptContent = "课件文件：" + fileName;
                    } else {
                        log.info("PPT内容读取成功，长度: {}", pptContent.length());
                    }
                    
                    String prompt = buildCoursewareUpdatePrompt(fileName, pptContent, updateRequirements);
                    String suggestions = callAI(prompt);

                    CoursewareUpdate update = new CoursewareUpdate();
                    update.setId(recordId);
                    update.setAiSuggestions(suggestions);
                    update.setStatus("suggestions_ready");
                    coursewareUpdateMapper.updateSuggestions(update);

                    log.info("课件更新建议已生成，记录ID: {}", recordId);
                } catch (Exception e) {
                    log.error("生成更新建议失败", e);
                    CoursewareUpdate update = new CoursewareUpdate();
                    update.setId(recordId);
                    update.setStatus("failed");
                    coursewareUpdateMapper.updateSuggestions(update);
                }
            }).start();

            return recordId;

        } catch (Exception e) {
            log.error("创建课件更新记录失败", e);
            throw new RuntimeException("创建课件更新记录失败: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> getCoursewareUpdateSuggestions(Long updateId) {
        CoursewareUpdate record = coursewareUpdateMapper.selectById(updateId);
        if (record == null) {
            throw new RuntimeException("更新记录不存在");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("id", record.getId());
        result.put("status", record.getStatus());
        result.put("fileName", record.getOriginalFileName());
        result.put("updateRequirements", record.getUpdateRequirements());
        result.put("suggestions", record.getAiSuggestions());

        return result;
    }

    @Override
    public List<CoursewareUpdate> getCoursewareUpdates(Long teacherId) {
        return coursewareUpdateMapper.selectByTeacherId(teacherId);
    }

    @Override
    public boolean deleteCoursewareUpdate(Long updateId) {
        try {
            int rows = coursewareUpdateMapper.deleteById(updateId);
            return rows > 0;
        } catch (Exception e) {
            log.error("删除课件更新记录失败", e);
            return false;
        }
    }

    // ==================== 2. 实验指导书生成 ====================

    @Override
    public Long generateExperimentGuide(Long teacherId, Long courseId, String chapterName,
                                       List<String> knowledgePoints, String difficultyLevel) {
        log.info("开始生成实验指导书 - 教师ID: {}, 章节: {}", teacherId, chapterName);

        try {
            // 1. 创建记录
            ExperimentGuideGeneration record = new ExperimentGuideGeneration();
            record.setTeacherId(teacherId);
            record.setCourseId(courseId);
            record.setChapterName(chapterName);
            record.setKnowledgePoints(objectMapper.writeValueAsString(knowledgePoints));
            record.setDifficultyLevel(difficultyLevel);
            record.setStatus("generating");

            experimentGuideGenerationMapper.insert(record);
            Long recordId = record.getId();

            // 2. 异步生成实验指导书
            new Thread(() -> {
                try {
                    String prompt = buildExperimentPrompt(chapterName, knowledgePoints, difficultyLevel);
                    String experimentContent = callAI(prompt);
                    
                    // 解析AI响应并更新记录
                    Map<String, Object> content = parseExperimentContent(experimentContent);
                    
                    ExperimentGuideGeneration update = new ExperimentGuideGeneration();
                    update.setId(recordId);
                    update.setExperimentTitle(content.getOrDefault("title", chapterName + "实验").toString());
                    update.setExperimentObjectives(objectMapper.writeValueAsString(content.get("objectives")));
                    
                    // 正确处理environment字段（可能是对象或字符串）
                    Object envObj = content.get("environment");
                    String envStr = (envObj instanceof String) ? (String) envObj : objectMapper.writeValueAsString(envObj);
                    update.setExperimentEnvironment(envStr);
                    
                    update.setExperimentSteps(objectMapper.writeValueAsString(content.get("steps")));
                    update.setExperimentCode(content.getOrDefault("code", "").toString());
                    
                    // 正确处理expectedResults字段（可能是数组或字符串）
                    Object resultsObj = content.get("expectedResults");
                    String resultsStr = (resultsObj instanceof String) ? (String) resultsObj : objectMapper.writeValueAsString(resultsObj);
                    update.setExpectedResults(resultsStr);
                    
                    update.setThinkingQuestions(objectMapper.writeValueAsString(content.get("questions")));
                    update.setReferenceAnswers(objectMapper.writeValueAsString(content.get("answers")));
                    update.setStatus("completed");
                    
                    experimentGuideGenerationMapper.updateContent(update);
                    
                    log.info("实验指导书生成完成，记录ID: {}", recordId);
                } catch (Exception e) {
                    log.error("生成实验指导书失败", e);
                    ExperimentGuideGeneration update = new ExperimentGuideGeneration();
                    update.setId(recordId);
                    update.setStatus("failed");
                    experimentGuideGenerationMapper.updateContent(update);
                }
            }).start();

            return recordId;

        } catch (Exception e) {
            log.error("创建实验指导书记录失败", e);
            throw new RuntimeException("创建实验指导书记录失败: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> getExperimentGuideContent(Long guideId) {
        ExperimentGuideGeneration record = experimentGuideGenerationMapper.selectById(guideId);
        if (record == null) {
            throw new RuntimeException("实验指导书不存在");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("id", record.getId());
        result.put("status", record.getStatus());
        result.put("chapterName", record.getChapterName());
        result.put("title", record.getExperimentTitle());
        result.put("objectives", record.getExperimentObjectives());
        result.put("environment", record.getExperimentEnvironment());
        result.put("steps", record.getExperimentSteps());
        result.put("code", record.getExperimentCode());
        result.put("expectedResults", record.getExpectedResults());
        result.put("questions", record.getThinkingQuestions());
        result.put("answers", record.getReferenceAnswers());

        return result;
    }

    @Override
    public List<Map<String, Object>> getExperimentGuides(Long teacherId) {
        List<ExperimentGuideGeneration> records = experimentGuideGenerationMapper.selectByTeacherId(teacherId);
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (ExperimentGuideGeneration record : records) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", record.getId());
            item.put("chapterName", record.getChapterName());
            item.put("difficultyLevel", record.getDifficultyLevel());
            item.put("status", record.getStatus());
            item.put("createTime", record.getCreateTime());
            result.add(item);
        }
        
        return result;
    }

    @Override
    public boolean deleteExperimentGuide(Long guideId) {
        try {
            int rows = experimentGuideGenerationMapper.deleteById(guideId);
            return rows > 0;
        } catch (Exception e) {
            log.error("删除实验指导书失败", e);
            return false;
        }
    }

    // ==================== 3. 主观题批阅助手 ====================

    @Override
    public Map<String, Object> gradeHomeworkByAI(Long homeworkId) {
        log.info("开始AI批阅作业 - 作业ID: {}", homeworkId);

        try {
            HomeworkGrading record = homeworkGradingMapper.selectById(homeworkId);
            if (record == null) {
                throw new RuntimeException("作业不存在");
            }

            // 读取学生作业内容
            String studentAnswer = "";
            
            // 1. 如果有上传文件，优先读取文件内容
            if (record.getSubmissionFileUrl() != null && !record.getSubmissionFileUrl().isEmpty()) {
                log.info("读取作业文件: {}", record.getSubmissionFileName());
                String fileContent = WordDocumentReader.readWordDocument(record.getSubmissionFileUrl());
                if (fileContent != null && !fileContent.isEmpty()) {
                    studentAnswer = fileContent;
                    log.info("成功读取文件内容，长度: {}", fileContent.length());
                } else {
                    log.warn("文件内容为空，使用文本框内容");
                    studentAnswer = record.getSubmissionContent();
                }
            } else {
                // 2. 如果没有文件，使用文本框内容
                studentAnswer = record.getSubmissionContent();
                log.info("使用文本框内容，长度: {}", studentAnswer != null ? studentAnswer.length() : 0);
            }
            
            if (studentAnswer == null || studentAnswer.trim().isEmpty()) {
                throw new RuntimeException("作业内容为空，无法批阅");
            }

            // 构建批阅提示词（题目内容 + 学生答案）
            String prompt = buildGradingPrompt(record.getHomeworkTitle(), record.getSubmissionContent(), studentAnswer);
            String aiResponse = callAI(prompt);

            // 解析AI响应
            Map<String, Object> grading = parseGradingResponse(aiResponse);

            // 更新批阅结果
            record.setAiScore(new BigDecimal(grading.get("score").toString()));
            record.setAiComments(grading.get("comments").toString());
            record.setAiAnalysis(objectMapper.writeValueAsString(grading.get("analysis")));
            record.setStatus("ai_graded");
            record.setAiGradeTime(LocalDateTime.now());

            homeworkGradingMapper.updateAiGrading(record);

            Map<String, Object> result = new HashMap<>();
            result.put("homeworkId", homeworkId);
            result.put("score", record.getAiScore());
            result.put("comments", record.getAiComments());
            result.put("analysis", grading.get("analysis"));

            return result;

        } catch (Exception e) {
            log.error("AI批阅作业失败", e);
            throw new RuntimeException("AI批阅作业失败: " + e.getMessage());
        }
    }

    @Override
    public Long submitHomework(Long teacherId, Long studentId, Long courseId, String homeworkTitle,
                              String fileUrl, String fileName, String content) {
        HomeworkGrading record = new HomeworkGrading();
        record.setTeacherId(teacherId);
        record.setStudentId(studentId);
        record.setCourseId(courseId);
        record.setHomeworkTitle(homeworkTitle);
        record.setSubmissionFileUrl(fileUrl);
        record.setSubmissionFileName(fileName);
        record.setSubmissionContent(content);
        record.setStatus("submitted");
        record.setSubmitTime(LocalDateTime.now());

        homeworkGradingMapper.insert(record);
        return record.getId();
    }

    @Override
    public List<HomeworkGrading> getTeacherHomeworks(Long teacherId) {
        return homeworkGradingMapper.selectByTeacherId(teacherId);
    }

    @Override
    public boolean reviewHomework(Long homeworkId, Double teacherScore, String teacherComments) {
        try {
            HomeworkGrading record = homeworkGradingMapper.selectById(homeworkId);
            if (record == null) {
                return false;
            }

            record.setTeacherScore(new BigDecimal(teacherScore));
            record.setTeacherComments(teacherComments);
            record.setStatus("teacher_reviewed");
            record.setTeacherReviewTime(LocalDateTime.now());

            homeworkGradingMapper.updateTeacherReview(record);
            return true;
        } catch (Exception e) {
            log.error("教师审阅失败", e);
            return false;
        }
    }

    @Override
    public boolean deleteHomework(Long homeworkId) {
        try {
            int rows = homeworkGradingMapper.deleteById(homeworkId);
            return rows > 0;
        } catch (Exception e) {
            log.error("删除作业记录失败", e);
            return false;
        }
    }

    // ==================== 私有辅助方法 ====================

    private String callAI(String prompt) {
        try {
            log.info("准备调用AI - API Key: {}", dashScopeConfig.getApiKey() != null ? "已配置" : "未配置");
            log.info("API Key长度: {}", dashScopeConfig.getApiKey() != null ? dashScopeConfig.getApiKey().length() : 0);
            
            // 正确的方式：使用 apiKey 参数创建 Generation 实例
            Generation gen = new Generation();
            log.info("Generation实例创建成功");
            
            Message systemMsg = Message.builder()
                    .role(Role.SYSTEM.getValue())
                    .content("你是一位经验丰富的大学教师，擅长课程设计、教学资源开发和作业批阅。")
                    .build();
            Message userMsg = Message.builder()
                    .role(Role.USER.getValue())
                    .content(prompt)
                    .build();
            log.info("消息构建完成");

            QwenParam param = QwenParam.builder()
                    .model("qwen-plus")
                    .messages(Arrays.asList(systemMsg, userMsg))
                    .resultFormat(QwenParam.ResultFormat.MESSAGE)
                    .topP(0.8)
                    .enableSearch(true)
                    .apiKey(dashScopeConfig.getApiKey())  // 直接在参数中设置 API Key
                    .build();
            log.info("参数构建完成，准备调用API");

            GenerationResult result = gen.call(param);
            log.info("API调用成功，准备解析结果");
            
            String content = result.getOutput().getChoices().get(0).getMessage().getContent();
            log.info("AI响应内容长度: {}", content.length());
            return content;

        } catch (Exception e) {
            log.error("调用AI失败 - 异常类型: {}", e.getClass().getName(), e);
            log.error("异常消息: {}", e.getMessage());
            throw new RuntimeException("调用AI失败: " + e.getMessage());
        }
    }

    private String buildCoursewareUpdatePrompt(String fileName, String pptContent, String updateRequirements) {
        // 如果PPT内容太长，截取前5000字
        String truncatedContent = pptContent;
        if (pptContent.length() > 5000) {
            truncatedContent = pptContent.substring(0, 5000) + "\n...(内容过长，已截断)";
            log.info("PPT内容过长，已截断至5000字符");
        }
        
        return String.format(
            "我有一份课件「%s」，以下是课件的主要内容：\n\n" +
            "=== 课件内容 ===\n%s\n=== 内容结束 ===\n\n" +
            "更新要求：\n%s\n\n" +
            "请根据课件的实际内容，提供详细的更新建议，包括：\n" +
            "1. 分析课件中哪些页面或章节的案例已经过时，需要更新\n" +
            "2. 针对每个需要更新的地方，推荐2024-2025年的最新案例\n" +
            "3. 需要补充的前沿知识点\n" +
            "4. 具体的更新内容建议\n\n" +
            "请以JSON格式输出，包含updates数组，每个元素包含：\n" +
            "- page（页码，例如：\"第12页\"）\n" +
            "- section（章节标题，从课件内容中提取）\n" +
            "- oldContent（需要更新的旧内容摘要）\n" +
            "- suggestion（更新建议）\n" +
            "- newContent（推荐的新内容，包括具体案例和数据）",
            fileName, truncatedContent, updateRequirements
        );
    }

    private String buildExperimentPrompt(String chapterName, List<String> knowledgePoints, String difficultyLevel) {
        return String.format(
            "请为章节「%s」生成一份完整的实验指导书。\n\n" +
            "知识点：%s\n" +
            "难度：%s\n\n" +
            "要求：\n" +
            "1. 实验目的（3-5条）\n" +
            "2. 实验环境和工具\n" +
            "3. 实验步骤（详细，包含代码示例）\n" +
            "4. 预期结果\n" +
            "5. 思考题（3道）\n" +
            "6. 参考答案\n\n" +
            "**重要：必须严格按照以下JSON格式输出，不要添加任何其他文字说明：**\n\n" +
            "```json\n" +
            "{\n" +
            "  \"title\": \"实验标题\",\n" +
            "  \"objectives\": [\"目的1\", \"目的2\", \"目的3\"],\n" +
            "  \"environment\": {\n" +
            "    \"os\": \"操作系统\",\n" +
            "    \"language\": \"编程语言\",\n" +
            "    \"ide\": \"开发环境\",\n" +
            "    \"tools\": [\"工具1\", \"工具2\"]\n" +
            "  },\n" +
            "  \"steps\": [\"步骤1\", \"步骤2\", \"步骤3\"],\n" +
            "  \"expectedResults\": [\"结果1\", \"结果2\"],\n" +
            "  \"questions\": [\"问题1\", \"问题2\", \"问题3\"],\n" +
            "  \"answers\": [\"答案1\", \"答案2\", \"答案3\"]\n" +
            "}\n" +
            "```\n\n" +
            "请确保输出的是纯JSON格式，可以被JSON解析器直接解析。",
            chapterName, String.join("、", knowledgePoints), difficultyLevel
        );
    }

    private String buildGradingPrompt(String homeworkTitle, String questionContent, String studentAnswer) {
        return String.format(
            "请批阅以下主观题作业：\n\n" +
            "作业标题：%s\n\n" +
            "题目内容：\n%s\n\n" +
            "学生答案：\n%s\n\n" +
            "批阅要求：\n" +
            "1. 给出0-100分的客观评分\n" +
            "2. 写出自然流畅的评语（150-250字），像老师批改作业一样，用亲切的语气指出优点和不足\n" +
            "3. 详细列出优点（至少4-6条），包括内容、结构、表达等方面\n" +
            "4. 详细列出不足（至少4-6条），指出具体问题所在\n" +
            "5. 给出具体可行的改进建议（至少5-8条），帮助学生提升\n\n" +
            "**重要：请严格按照以下JSON格式输出：**\n\n" +
            "```json\n" +
            "{\n" +
            "  \"score\": 85,\n" +
            "  \"comments\": \"这里写自然流畅的评语，不要用JSON格式，就像老师手写批语一样。评语应该包含对整体的评价、主要优点、主要不足和鼓励的话。\",\n" +
            "  \"analysis\": {\n" +
            "    \"strengths\": [\n" +
            "      \"优点1：具体描述\",\n" +
            "      \"优点2：具体描述\",\n" +
            "      \"优点3：具体描述\",\n" +
            "      \"优点4：具体描述\",\n" +
            "      \"优点5：具体描述\",\n" +
            "      \"优点6：具体描述\"\n" +
            "    ],\n" +
            "    \"weaknesses\": [\n" +
            "      \"不足1：具体问题\",\n" +
            "      \"不足2：具体问题\",\n" +
            "      \"不足3：具体问题\",\n" +
            "      \"不足4：具体问题\",\n" +
            "      \"不足5：具体问题\",\n" +
            "      \"不足6：具体问题\"\n" +
            "    ],\n" +
            "    \"suggestions\": [\n" +
            "      \"建议1：具体可行的改进方法\",\n" +
            "      \"建议2：具体可行的改进方法\",\n" +
            "      \"建议3：具体可行的改进方法\",\n" +
            "      \"建议4：具体可行的改进方法\",\n" +
            "      \"建议5：具体可行的改进方法\",\n" +
            "      \"建议6：具体可行的改进方法\",\n" +
            "      \"建议7：具体可行的改进方法\",\n" +
            "      \"建议8：具体可行的改进方法\"\n" +
            "    ]\n" +
            "  }\n" +
            "}\n" +
            "```\n\n" +
            "注意：comments字段应该是自然的文字评语，不要包含JSON格式的内容。",
            homeworkTitle, questionContent, studentAnswer
        );
    }

    private Map<String, Object> parseGradingResponse(String aiResponse) {
        try {
            // 尝试提取JSON内容（AI可能在JSON前后添加了说明文字）
            String jsonContent = aiResponse;
            
            // 如果响应包含```json标记，提取其中的JSON
            if (aiResponse.contains("```json")) {
                int startIdx = aiResponse.indexOf("```json") + 7;
                int endIdx = aiResponse.indexOf("```", startIdx);
                if (endIdx > startIdx) {
                    jsonContent = aiResponse.substring(startIdx, endIdx).trim();
                }
            } else if (aiResponse.contains("```")) {
                // 如果只有```标记
                int startIdx = aiResponse.indexOf("```") + 3;
                int endIdx = aiResponse.indexOf("```", startIdx);
                if (endIdx > startIdx) {
                    jsonContent = aiResponse.substring(startIdx, endIdx).trim();
                }
            }
            
            // 尝试找到第一个{和最后一个}
            int firstBrace = jsonContent.indexOf('{');
            int lastBrace = jsonContent.lastIndexOf('}');
            if (firstBrace >= 0 && lastBrace > firstBrace) {
                jsonContent = jsonContent.substring(firstBrace, lastBrace + 1);
            }
            
            log.info("批阅响应JSON解析: {}", jsonContent.substring(0, Math.min(200, jsonContent.length())));
            
            Map<String, Object> result = objectMapper.readValue(jsonContent, Map.class);
            log.info("批阅结果解析成功 - 分数: {}, 评语长度: {}", 
                result.get("score"), 
                result.get("comments") != null ? result.get("comments").toString().length() : 0);
            
            return result;
            
        } catch (Exception e) {
            log.error("批阅响应解析失败，使用默认结构。原始响应: {}", 
                aiResponse.substring(0, Math.min(500, aiResponse.length())), e);
            
            // 如果不是JSON格式，手动解析
            Map<String, Object> result = new HashMap<>();
            result.put("score", 85);
            result.put("comments", aiResponse);
            
            Map<String, Object> analysis = new HashMap<>();
            analysis.put("strengths", Arrays.asList(
                "内容较为完整",
                "结构清晰",
                "表达流畅"
            ));
            analysis.put("weaknesses", Arrays.asList(
                "部分细节需要补充",
                "案例不够充分",
                "深度分析不足"
            ));
            analysis.put("suggestions", Arrays.asList(
                "建议补充更多实际案例",
                "加强理论深度分析",
                "完善细节描述",
                "增加图表说明",
                "参考更多文献资料"
            ));
            result.put("analysis", analysis);
            
            return result;
        }
    }

    private Map<String, Object> parseExperimentContent(String aiResponse) {
        try {
            // 尝试提取JSON内容（AI可能在JSON前后添加了说明文字）
            String jsonContent = aiResponse;
            
            // 如果响应包含```json标记，提取其中的JSON
            if (aiResponse.contains("```json")) {
                int startIdx = aiResponse.indexOf("```json") + 7;
                int endIdx = aiResponse.indexOf("```", startIdx);
                if (endIdx > startIdx) {
                    jsonContent = aiResponse.substring(startIdx, endIdx).trim();
                }
            } else if (aiResponse.contains("```")) {
                // 如果只有```标记
                int startIdx = aiResponse.indexOf("```") + 3;
                int endIdx = aiResponse.indexOf("```", startIdx);
                if (endIdx > startIdx) {
                    jsonContent = aiResponse.substring(startIdx, endIdx).trim();
                }
            }
            
            // 尝试找到第一个{和最后一个}
            int firstBrace = jsonContent.indexOf('{');
            int lastBrace = jsonContent.lastIndexOf('}');
            if (firstBrace >= 0 && lastBrace > firstBrace) {
                jsonContent = jsonContent.substring(firstBrace, lastBrace + 1);
            }
            
            log.info("提取的JSON内容: {}", jsonContent.substring(0, Math.min(200, jsonContent.length())));
            
            Map<String, Object> result = objectMapper.readValue(jsonContent, Map.class);
            log.info("JSON解析成功");
            return result;
            
        } catch (Exception e) {
            log.error("JSON解析失败，使用默认模板。原始响应: {}", aiResponse.substring(0, Math.min(500, aiResponse.length())), e);
            
            // 如果不是JSON格式，返回默认结构
            Map<String, Object> result = new HashMap<>();
            result.put("title", "实验指导书");
            result.put("objectives", Arrays.asList("掌握基本概念", "理解核心原理", "能够实际应用"));
            result.put("environment", "开发环境：JDK 1.8+, IDE: IntelliJ IDEA");
            result.put("steps", Arrays.asList(
                "步骤1：环境准备",
                "步骤2：代码编写",
                "步骤3：运行测试",
                "步骤4：结果分析"
            ));
            result.put("code", aiResponse);
            result.put("expectedResults", Arrays.asList("程序正常运行，输出符合预期"));
            result.put("questions", Arrays.asList(
                "思考题1：本实验的核心原理是什么？",
                "思考题2：如何优化代码性能？",
                "思考题3：实际应用场景有哪些？"
            ));
            result.put("answers", Arrays.asList(
                "答案1：核心原理是...",
                "答案2：可以通过...优化",
                "答案3：应用场景包括..."
            ));
            return result;
        }
    }
}
