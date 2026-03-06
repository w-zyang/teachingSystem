package com.experiment.service.Impl;

import com.experiment.service.AIEvaluationService;
import com.experiment.service.AIService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * AI评测分析服务实现类
 * 从AIController中提取的评测相关业务逻辑
 */
@Slf4j
@Service
public class AIEvaluationServiceImpl implements AIEvaluationService {
    
    @Autowired
    private AIService aiService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public Map<String, Object> evaluatePractice(Map<String, Object> data) {
        log.info("开始练习评测");

        // 兼容两种模式：
        // 1）整套练习批阅：questions + answers 列表
        // 2）单题实时批阅：question + studentAnswer + correctAnswer

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> questions = (List<Map<String, Object>>) data.get("questions");

        // ===== 单题模式：前端 PracticeSession.vue 传的是 question / studentAnswer / correctAnswer =====
        if (questions == null) {
            log.info("检测到单题评测模式");
            @SuppressWarnings("unchecked")
            Map<String, Object> question = (Map<String, Object>) data.get("question");
            String studentAnswer = (String) data.get("studentAnswer");
            String correctAnswer = (String) data.get("correctAnswer");

            if (question == null) {
                throw new IllegalArgumentException("评测请求中缺少 question 字段");
            }

            if (studentAnswer == null) {
                studentAnswer = "";
            }
            if (correctAnswer == null) {
                correctAnswer = "";
            }

            // 基础判分：简单字符串对比，忽略首尾空格
            boolean isCorrect = studentAnswer.trim().equalsIgnoreCase(correctAnswer.trim());

            Map<String, Object> result = new HashMap<>();
            result.put("isCorrect", isCorrect);

            // 尝试调用 AI 做一句话点评（失败就用本地文案）
            try {
                String topic = (String) data.get("topic");
                String systemPrompt = buildDetailedAnalysisSystemPrompt();
                String userMessage = buildDetailedAnalysisUserMessage(
                        question, studentAnswer, correctAnswer, isCorrect, topic
                );
                String response = aiService.chatWithSystem(systemPrompt, userMessage);
                Map<String, Object> detail = parseDetailedAnalysisResponse(response);
                String evaluation = (String) detail.getOrDefault("detailedAnalysis", "");
                if (evaluation == null || evaluation.isBlank()) {
                    evaluation = isCorrect
                            ? "回答正确，继续保持。"
                            : "回答有误，建议回顾本题考察的知识点并多做几道类似题巩固。";
                }
                result.put("evaluation", evaluation);
            } catch (Exception e) {
                log.warn("单题AI评测失败，使用本地评价文案: {}", e.getMessage());
                String fallback = isCorrect
                        ? "回答正确，继续保持。"
                        : "回答有误，注意对比参考答案，总结错误原因。";
                result.put("evaluation", fallback);
            }

            log.info("单题练习评测完成");
            return result;
        }

        // ===== 整套练习批阅模式 =====
        @SuppressWarnings("unchecked")
        List<String> answers = (List<String>) data.get("answers");
        String topic = (String) data.get("topic");
        Integer timeUsed = (Integer) data.get("timeUsed");

        // 空保护，避免 NPE
        if (questions == null || questions.isEmpty() || answers == null) {
            log.warn("整套评测请求中 questions 或 answers 为空，返回基础空结果");
            Map<String, Object> empty = new HashMap<>();
            empty.put("score", 0);
            empty.put("correctCount", 0);
            empty.put("accuracy", 0);
            empty.put("analysis", Collections.emptyList());
            empty.put("timeUsed", timeUsed);
            return empty;
        }

        // 构建评测提示词
        String systemPrompt = buildEvaluationSystemPrompt();
        String userMessage = buildEvaluationUserMessage(questions, answers, topic);

        try {
            String response = aiService.chatWithSystem(systemPrompt, userMessage);
            Map<String, Object> result = parseEvaluationResponse(response);

            // 补充时间信息
            result.put("timeUsed", timeUsed);

            log.info("练习评测完成");
            return result;
        } catch (Exception e) {
            log.error("练习评测失败，使用基础规则评测", e);
            // 返回基础评测结果
            return createBasicEvaluation(questions, answers);
        }
    }
    
    @Override
    public Map<String, Object> analyzeStudentData(Map<String, Object> data) {
        log.info("开始分析学生数据");
        
        String systemPrompt = "你是一位专业的教育数据分析专家。请分析学生的答题数据，给出详细的分析报告。";
        String userMessage = "请分析以下学生答题数据：" + data.toString();
        
        try {
            String response = aiService.chatWithSystem(systemPrompt, userMessage);
            Map<String, Object> result = new HashMap<>();
            result.put("analysis", response);
            
            log.info("学生数据分析完成");
            return result;
        } catch (Exception e) {
            log.error("学生数据分析失败", e);
            throw new RuntimeException("学生数据分析失败: " + e.getMessage());
        }
    }
    
    @Override
    public Map<String, Object> generateDetailedAnalysis(Map<String, Object> data) {
        log.info("开始生成详细分析");
        
        Map<String, Object> question = (Map<String, Object>) data.get("question");
        String userAnswer = (String) data.get("userAnswer");
        String correctAnswer = (String) data.get("correctAnswer");
        Boolean isCorrect = (Boolean) data.get("isCorrect");
        String topic = (String) data.get("topic");
        
        String systemPrompt = buildDetailedAnalysisSystemPrompt();
        String userMessage = buildDetailedAnalysisUserMessage(question, userAnswer, correctAnswer, isCorrect, topic);
        
        try {
            String response = aiService.chatWithSystem(systemPrompt, userMessage);
            Map<String, Object> result = parseDetailedAnalysisResponse(response);
            
            log.info("详细分析生成完成");
            return result;
        } catch (Exception e) {
            log.error("生成详细分析失败", e);
            throw new RuntimeException("生成详细分析失败: " + e.getMessage());
        }
    }
    
    @Override
    public Map<String, Object> checkAnalysisStatus(Map<String, Object> data) {
        log.info("检查AI分析状态");
        
        List<Map<String, Object>> analysis = (List<Map<String, Object>>) data.get("analysis");
        
        List<Map<String, Object>> statusList = new ArrayList<>();
        boolean allCompleted = true;
        
        for (int i = 0; i < analysis.size(); i++) {
            Map<String, Object> item = analysis.get(i);
            Map<String, Object> status = new HashMap<>();
            status.put("index", i);
            
            // 检查是否需要AI分析
            boolean needsAIAnalysis = item.get("detailedAnalysis") == null || 
                                     item.get("detailedAnalysis").toString().isEmpty();
            status.put("needsAIAnalysis", needsAIAnalysis);
            
            if (needsAIAnalysis) {
                allCompleted = false;
            }
            
            statusList.add(status);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("statusList", statusList);
        result.put("allCompleted", allCompleted);
        
        return result;
    }
    
    @Override
    public Map<String, Object> triggerAIAnalysis(Map<String, Object> data) {
        log.info("手动触发AI分析");
        
        Map<String, Object> question = (Map<String, Object>) data.get("question");
        String userAnswer = (String) data.get("userAnswer");
        String correctAnswer = (String) data.get("correctAnswer");
        Boolean isCorrect = (Boolean) data.get("isCorrect");
        String topic = (String) data.get("topic");
        
        return generateDetailedAnalysis(data);
    }
    
    /**
     * 构建评测系统提示词
     */
    private String buildEvaluationSystemPrompt() {
        return "你是一位专业的教育评测专家。请对学生的练习答案进行评测，给出分数、正确率和详细分析。" +
               "评测要求：\n" +
               "1. 客观公正，准确评分\n" +
               "2. 指出错误原因\n" +
               "3. 给出改进建议\n" +
               "4. 返回JSON格式的评测结果";
    }
    
    /**
     * 构建评测用户消息
     */
    private String buildEvaluationUserMessage(List<Map<String, Object>> questions, List<String> answers, String topic) {
        if (questions == null || questions.isEmpty()) {
            return "本次练习包含若干道题目，请根据学生的整体答题情况给出一个简要的评价和建议。";
        }

        StringBuilder message = new StringBuilder();
        message.append("请评测以下练习：\n\n");
        message.append("主题：").append(topic).append("\n\n");
        
        for (int i = 0; i < questions.size(); i++) {
            Map<String, Object> question = questions.get(i);
            String answer = i < answers.size() ? answers.get(i) : "";
            
            message.append("第").append(i + 1).append("题：\n");
            message.append("题目：").append(question.get("title")).append("\n");
            message.append("学生答案：").append(answer).append("\n");
            message.append("正确答案：").append(question.get("answer")).append("\n\n");
        }
        
        return message.toString();
    }
    
    /**
     * 解析评测响应
     */
    private Map<String, Object> parseEvaluationResponse(String response) {
        try {
            response = response.replaceAll("```json\\s*", "").replaceAll("```\\s*", "").trim();
            return objectMapper.readValue(response, Map.class);
        } catch (Exception e) {
            log.warn("解析评测响应失败，返回默认结果");
            Map<String, Object> result = new HashMap<>();
            result.put("score", 0);
            result.put("correctCount", 0);
            result.put("accuracy", 0);
            result.put("analysis", new ArrayList<>());
            return result;
        }
    }
    
    /**
     * 创建基础评测结果（fallback）
     */
    private Map<String, Object> createBasicEvaluation(List<Map<String, Object>> questions, List<String> answers) {
        int correctCount = 0;
        List<Map<String, Object>> analysis = new ArrayList<>();
        
        for (int i = 0; i < questions.size(); i++) {
            Map<String, Object> question = questions.get(i);
            String userAnswer = i < answers.size() ? answers.get(i) : "";
            String correctAnswer = (String) question.get("answer");
            
            boolean isCorrect = userAnswer.trim().equalsIgnoreCase(correctAnswer.trim());
            if (isCorrect) {
                correctCount++;
            }
            
            Map<String, Object> item = new HashMap<>();
            item.put("title", question.get("title"));
            item.put("userAnswer", userAnswer);
            item.put("correctAnswer", correctAnswer);
            item.put("isCorrect", isCorrect);
            item.put("explanation", question.get("explanation"));
            
            analysis.add(item);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("score", correctCount * 10);
        result.put("correctCount", correctCount);
        result.put("accuracy", questions.size() > 0 ? (correctCount * 100 / questions.size()) : 0);
        result.put("analysis", analysis);
        
        return result;
    }
    
    /**
     * 构建详细分析系统提示词
     */
    private String buildDetailedAnalysisSystemPrompt() {
        return "你是一位专业的教育分析专家。请对学生的答题情况进行深入分析，包括：\n" +
               "1. 知识点掌握情况\n" +
               "2. 错误原因分析\n" +
               "3. 学习建议\n" +
               "4. 相关知识点推荐\n" +
               "请返回JSON格式，包含detailedAnalysis和suggestion字段。";
    }
    
    /**
     * 构建详细分析用户消息
     */
    private String buildDetailedAnalysisUserMessage(Map<String, Object> question, String userAnswer, 
                                                    String correctAnswer, Boolean isCorrect, String topic) {
        return String.format(
            "请分析以下答题情况：\n" +
            "题目：%s\n" +
            "学生答案：%s\n" +
            "正确答案：%s\n" +
            "是否正确：%s\n" +
            "主题：%s\n",
            question.get("title"), userAnswer, correctAnswer, 
            isCorrect ? "正确" : "错误", topic
        );
    }
    
    /**
     * 解析详细分析响应
     */
    private Map<String, Object> parseDetailedAnalysisResponse(String response) {
        try {
            response = response.replaceAll("```json\\s*", "").replaceAll("```\\s*", "").trim();
            return objectMapper.readValue(response, Map.class);
        } catch (Exception e) {
            log.warn("解析详细分析响应失败");
            Map<String, Object> result = new HashMap<>();
            result.put("detailedAnalysis", "分析生成失败，请稍后重试");
            result.put("suggestion", "建议复习相关知识点");
            return result;
        }
    }
}
