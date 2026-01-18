package com.experiment.service.Impl;

import com.experiment.service.AIQuestionGenerationService;
import com.experiment.service.AIService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * AI题目生成服务实现类
 * 从AIController中提取的核心业务逻辑
 */
@Slf4j
@Service
public class AIQuestionGenerationServiceImpl implements AIQuestionGenerationService {
    
    @Autowired
    private AIService aiService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public Map<String, Object> generatePracticeQuestions(
            String topic, 
            String difficulty, 
            List<String> questionTypes,
            Map<String, Integer> typeConfig,
            Integer count) {
        
        log.info("开始生成练习题目: topic={}, difficulty={}, types={}, count={}", 
                 topic, difficulty, questionTypes, count);
        
        // 如果没有指定题目类型，默认为选择题
        if (questionTypes == null || questionTypes.isEmpty()) {
            questionTypes = Arrays.asList("choice");
        }
        
        // AI生成不同类型的题目
        List<Map<String, Object>> questions = new ArrayList<>();
        
        // 根据题目类型分别生成
        for (String questionType : questionTypes) {
            int typeCount = typeConfig != null && typeConfig.containsKey(questionType) ? 
                typeConfig.get(questionType) : 
                Math.max(1, count / questionTypes.size());
            
            List<Map<String, Object>> typeQuestions = generateQuestionsByType(
                topic, difficulty, questionType, typeCount);
            questions.addAll(typeQuestions);
        }
        
        // 如果指定了总数但没有具体配置，则调整题目数量
        if (typeConfig == null || typeConfig.isEmpty()) {
            while (questions.size() > count) {
                questions.remove(questions.size() - 1);
            }
            while (questions.size() < count) {
                // 随机选择一种类型补充
                String randomType = questionTypes.get((int) (Math.random() * questionTypes.size()));
                List<Map<String, Object>> additionalQuestions = generateQuestionsByType(
                    topic, difficulty, randomType, 1);
                if (!additionalQuestions.isEmpty()) {
                    questions.addAll(additionalQuestions);
                }
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("topic", topic);
        result.put("difficulty", difficulty);
        result.put("questions", questions);
        
        log.info("生成练习题目成功，共{}道题目", questions.size());
        return result;
    }
    
    @Override
    public Map<String, Object> generateExam(Map<String, Object> data) {
        log.info("开始生成考试题目: {}", data);
        
        String subject = (String) data.get("subject");
        String difficulty = (String) data.get("difficulty");
        Integer questionCount = (Integer) data.get("questionCount");
        
        // 构建系统提示词
        String systemPrompt = buildExamSystemPrompt(subject, difficulty, questionCount);
        String userMessage = String.format("请生成一份关于'%s'的考试，难度为%s，共%d道题目。", 
                                          subject, difficulty, questionCount);
        
        try {
            String response = aiService.chatWithSystem(systemPrompt, userMessage);
            Map<String, Object> exam = parseExamFromResponse(response);
            
            log.info("生成考试成功");
            return exam;
        } catch (Exception e) {
            log.error("生成考试失败", e);
            throw new RuntimeException("生成考试失败: " + e.getMessage());
        }
    }
    
    @Override
    public List<Map<String, Object>> generateQuestionsByType(
            String topic, 
            String difficulty, 
            String questionType, 
            int count) {
        
        List<Map<String, Object>> questions = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            try {
                String systemPrompt = getSystemPromptByType(questionType, topic, difficulty);
                String userMessage = getUserMessageByType(questionType, topic, difficulty);
                systemPrompt += " 这是第" + (i + 1) + "道题目，请确保与之前的题目完全不同。";
                
                String response = aiService.chatWithSystem(systemPrompt, userMessage);
                Map<String, Object> question = parseQuestionFromResponse(response, questionType);
                
                if (question != null && !isDuplicateQuestion(question, questions)) {
                    question.put("type", questionType);
                    question.put("difficulty", difficulty);
                    questions.add(question);
                } else {
                    // 如果解析失败或重复，创建一个简单的fallback
                    Map<String, Object> fallbackQuestion = createFallbackQuestion(questionType, topic, difficulty);
                    questions.add(fallbackQuestion);
                }
            } catch (Exception e) {
                log.error("生成{}类型题目失败 (第{}道)", questionType, i + 1, e);
                Map<String, Object> fallbackQuestion = createFallbackQuestion(questionType, topic, difficulty);
                questions.add(fallbackQuestion);
            }
        }
        
        return questions;
    }
    
    /**
     * 根据题目类型获取系统提示词
     */
    private String getSystemPromptByType(String questionType, String topic, String difficulty) {
        StringBuilder prompt = new StringBuilder();
        
        // ===== 最重要：开头就强调中文 =====
        prompt.append("【核心要求】你必须使用中文生成所有内容！\n");
        prompt.append("【核心要求】题目标题、题目描述、选项、答案、解析都必须是中文！\n");
        prompt.append("【核心要求】即使是编程题，题目描述也必须用中文！\n\n");
        
        prompt.append("你是一个专业的中文教学题目生成助手。请严格按照要求生成高质量的").append(getQuestionTypeName(questionType)).append("。");
        
        // 添加随机性指令，避免重复
        prompt.append("重要要求：每道题目必须独特，不能与之前生成的题目重复。请从不同角度、不同知识点、不同应用场景来设计题目。");
        
        switch (questionType) {
            case "choice":
                prompt.append("\n\n【选择题要求】\n");
                prompt.append("1. ✅ 题目内容：中文\n");
                prompt.append("2. ✅ 所有选项：中文\n");
                prompt.append("3. ✅ 答案解析：中文\n");
                prompt.append("请生成选择题，包含1个题目、4个选项(A、B、C、D)、1个正确答案和解析。");
                break;
            case "fill":
                prompt.append("\n\n【填空题要求】\n");
                prompt.append("1. ✅ 题目内容：中文\n");
                prompt.append("2. ✅ 答案：中文\n");
                prompt.append("请生成填空题，在题目中用____表示空白处，提供标准答案。");
                break;
            case "short":
                prompt.append("\n\n【简答题要求】\n");
                prompt.append("1. ✅ 题目内容：中文\n");
                prompt.append("2. ✅ 参考答案：中文\n");
                prompt.append("请生成简答题，要求学生用简洁的语言回答问题。");
                break;
            case "coding":
                prompt.append("\n\n【编程题要求 - 特别重要】\n");
                prompt.append("1. ✅ 题目标题：必须是中文\n");
                prompt.append("2. ✅ 题目描述：必须是中文\n");
                prompt.append("3. ✅ 编程要求：必须用中文说明\n");
                prompt.append("4. ✅ 输入格式：必须用中文说明\n");
                prompt.append("5. ✅ 输出格式：必须用中文说明\n");
                prompt.append("6. ✅ 示例说明：必须是中文\n");
                prompt.append("7. ✅ 提示信息：必须是中文\n");
                prompt.append("请生成编程题，包含问题描述、输入输出示例、参考代码。");
                break;
            case "essay":
                prompt.append("\n\n【论述题要求】\n");
                prompt.append("1. ✅ 题目内容：中文\n");
                prompt.append("2. ✅ 参考答案：中文\n");
                prompt.append("请生成论述题，要求深入分析和论证。");
                break;
        }
        
        prompt.append("\n\n题目难度为").append(difficulty).append("，主题是").append(topic).append("。");
        
        prompt.append("\n\n【输出格式】\n");
        prompt.append("请严格按照以下JSON格式输出：\n\n");
        
        switch (questionType) {
            case "choice":
                prompt.append("{\n");
                prompt.append("  \"title\": \"题目内容（中文）\",\n");
                prompt.append("  \"options\": [\"A. 选项1（中文）\", \"B. 选项2（中文）\", \"C. 选项3（中文）\", \"D. 选项4（中文）\"],\n");
                prompt.append("  \"answer\": \"A\",\n");
                prompt.append("  \"explanation\": \"解析内容（中文）\"\n");
                prompt.append("}");
                break;
            case "coding":
                prompt.append("{\n");
                prompt.append("  \"title\": \"题目标题（中文）\",\n");
                prompt.append("  \"requirements\": \"题目描述（中文）\",\n");
                prompt.append("  \"examples\": [{\"input\": \"输入示例\", \"output\": \"输出示例\"}],\n");
                prompt.append("  \"answer\": \"参考代码\"\n");
                prompt.append("}");
                break;
            default:
                prompt.append("{\n");
                prompt.append("  \"title\": \"题目内容（中文）\",\n");
                prompt.append("  \"answer\": \"答案（中文）\"\n");
                prompt.append("}");
        }
        
        prompt.append("\n\n⚠️ 最后提醒：所有内容必须是中文！禁止使用英文标题和描述！\n");
        
        return prompt.toString();
    }
    
    /**
     * 根据题目类型获取用户消息
     */
    private String getUserMessageByType(String questionType, String topic, String difficulty) {
        String[] variations = {
            String.format("【重要】请用中文生成！请生成1道关于'%s'的%s难度%s。", topic, difficulty, getQuestionTypeName(questionType)),
            String.format("【重要】请用中文生成！创建1道%s难度的%s，主题是'%s'。", difficulty, getQuestionTypeName(questionType), topic),
            String.format("【重要】请用中文生成！设计1道关于'%s'的%s，难度为%s。", topic, getQuestionTypeName(questionType), difficulty)
        };
        return variations[(int)(Math.random() * variations.length)];
    }
    
    /**
     * 从AI响应中解析题目
     */
    private Map<String, Object> parseQuestionFromResponse(String response, String questionType) {
        try {
            // 去除markdown代码块
            response = response.replaceAll("```json\\s*", "").replaceAll("```\\s*", "").trim();
            
            // 尝试JSON解析
            Map<String, Object> question = objectMapper.readValue(response, Map.class);
            
            // 补充type字段
            if (question != null && !question.containsKey("type")) {
                question.put("type", questionType);
            }
            
            return question;
        } catch (Exception e) {
            log.warn("JSON解析失败: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * 从响应中解析考试
     */
    private Map<String, Object> parseExamFromResponse(String response) {
        try {
            response = response.replaceAll("```json\\s*", "").replaceAll("```\\s*", "").trim();
            return objectMapper.readValue(response, Map.class);
        } catch (Exception e) {
            log.error("解析考试失败", e);
            throw new RuntimeException("解析考试失败");
        }
    }
    
    /**
     * 构建考试系统提示词
     */
    private String buildExamSystemPrompt(String subject, String difficulty, Integer questionCount) {
        return "你是一个专业的考试出题专家。请生成一份完整的考试，包含" + questionCount + "道题目。" +
               "主题是" + subject + "，难度为" + difficulty + "。" +
               "请返回JSON格式，包含题目列表、总分、时长等信息。";
    }
    
    /**
     * 检查题目是否重复
     */
    private boolean isDuplicateQuestion(Map<String, Object> newQuestion, List<Map<String, Object>> existingQuestions) {
        if (newQuestion == null || !newQuestion.containsKey("title")) {
            return true;
        }
        
        String newTitle = (String) newQuestion.get("title");
        for (Map<String, Object> existingQuestion : existingQuestions) {
            if (existingQuestion.containsKey("title")) {
                String existingTitle = (String) existingQuestion.get("title");
                if (calculateSimilarity(newTitle, existingTitle) > 0.8) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * 计算两个字符串的相似度
     */
    private double calculateSimilarity(String str1, String str2) {
        if (str1 == null || str2 == null) {
            return 0.0;
        }
        
        String longer = str1.length() > str2.length() ? str1 : str2;
        String shorter = str1.length() > str2.length() ? str2 : str1;
        
        if (longer.length() == 0) {
            return 1.0;
        }
        
        int commonChars = 0;
        for (int i = 0; i < shorter.length(); i++) {
            if (longer.contains(String.valueOf(shorter.charAt(i)))) {
                commonChars++;
            }
        }
        
        return (double) commonChars / longer.length();
    }
    
    /**
     * 创建简单的fallback题目
     */
    private Map<String, Object> createFallbackQuestion(String type, String topic, String difficulty) {
        Map<String, Object> question = new HashMap<>();
        question.put("type", type);
        question.put("title", "关于" + topic + "的" + getQuestionTypeName(type));
        question.put("difficulty", difficulty);
        question.put("answer", "请参考相关资料");
        return question;
    }
    
    /**
     * 获取题目类型名称
     */
    private String getQuestionTypeName(String type) {
        switch (type) {
            case "choice": return "选择题";
            case "fill": return "填空题";
            case "short": return "简答题";
            case "coding": return "编程题";
            case "essay": return "论述题";
            default: return "题目";
        }
    }
}
