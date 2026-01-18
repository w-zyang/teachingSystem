package com.experiment.service.Impl;

import com.experiment.mapper.QuestionBankMapper;
import com.experiment.pojo.Question;
import com.experiment.pojo.QuestionBank;
import com.experiment.pojo.QuestionOption;
import com.experiment.service.AIService;
import com.experiment.service.KnowledgeBaseService;
import com.experiment.service.QuestionGenerationService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 智能出题服务实现类（AI+知识库方案）
 * 
 * 核心思路：直接将知识库内容注入到大模型提示词中，无需本地缓存
 * 优势：
 * 1. 无需维护题库表
 * 2. 知识库更新立即生效
 * 3. 题目多样性高
 * 4. 架构简单
 */
@Slf4j
@Service
public class QuestionGenerationServiceImpl implements QuestionGenerationService {
    
    @Autowired(required = false)
    private QuestionBankMapper questionBankMapper;
    
    @Autowired
    private AIService aiService;
    
    @Autowired
    private KnowledgeBaseService knowledgeBaseService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public List<Question> generateQuestions(String subject, String knowledgePoint, 
                                           String type, String difficulty, Integer count) {
        log.info("🎯 开始智能出题：subject={}, knowledgePoint={}, type={}, difficulty={}, count={}", 
                 subject, knowledgePoint, type, difficulty, count);
        
        // 方案1：直接使用AI+知识库（推荐，无需本地缓存）
        log.info("📚 使用AI+知识库方案（知识库内容直接注入大模型）");
        List<Question> aiQuestions = generateQuestionsWithAI(subject, knowledgePoint, type, difficulty, count);
        
        if (!aiQuestions.isEmpty()) {
            log.info("✅ AI成功生成{}道题目（基于知识库内容）", aiQuestions.size());
            return aiQuestions;
        }
        
        // 方案2：如果AI失败，尝试从题库获取（备用方案）
        log.warn("⚠️ AI生成失败，尝试从题库获取...");
        List<Question> questionsFromBank = getQuestionsFromBank(subject, knowledgePoint, type, difficulty, count);
        
        if (!questionsFromBank.isEmpty()) {
            log.info("✅ 从题库获取到{}道题目", questionsFromBank.size());
            return questionsFromBank;
        }
        
        // 方案3：都失败了，返回空列表
        log.error("❌ 所有方案都失败，无法生成题目");
        return new ArrayList<>();
    }
    
    @Override
    @Transactional
    public int generateAndCacheQuestions(String subject, String knowledgePoint, 
                                        String type, String difficulty, Integer count) {
        log.info("批量生成并缓存题目到题库（可选功能，用于离线场景）");
        
        List<Question> questions = generateQuestionsWithAI(subject, knowledgePoint, type, difficulty, count);
        
        if (questions.isEmpty()) {
            log.warn("AI生成题目失败");
            return 0;
        }
        
        return cacheQuestionsToBank(questions, subject, knowledgePoint);
    }
    
    @Override
    public List<Question> getQuestionsFromBank(String subject, String knowledgePoint, 
                                              String type, String difficulty, Integer count) {
        // 如果题库Mapper不可用，返回空列表
        if (questionBankMapper == null) {
            log.warn("题库功能未启用（question_bank表不存在）");
            return new ArrayList<>();
        }
        
        try {
            List<QuestionBank> bankQuestions = questionBankMapper.selectByConditions(
                subject, knowledgePoint, type, difficulty, count);
            
            List<Question> questions = new ArrayList<>();
            for (QuestionBank bankQuestion : bankQuestions) {
                Question question = convertToQuestion(bankQuestion);
                questions.add(question);
                
                // 更新使用次数
                questionBankMapper.incrementUseCount(bankQuestion.getId());
            }
            
            log.info("从题库获取到{}道题目", questions.size());
            return questions;
        } catch (Exception e) {
            log.warn("从题库获取题目失败（可能表不存在）: {}", e.getMessage());
            return new ArrayList<>();
        }
    }
    
    @Override
    public boolean hasEnoughQuestions(String subject, String knowledgePoint, 
                                     String type, String difficulty, Integer requiredCount) {
        if (questionBankMapper == null) {
            return false;
        }
        
        try {
            int count = questionBankMapper.countByConditions(subject, knowledgePoint, type, difficulty);
            return count >= requiredCount;
        } catch (Exception e) {
            log.warn("检查题库失败: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 使用AI生成题目（直接传知识库内容给大模型）
     */
    private List<Question> generateQuestionsWithAI(String subject, String knowledgePoint, 
                                                   String type, String difficulty, Integer count) {
        try {
            long startTime = System.currentTimeMillis();
            
            // 1. 从知识库获取相关内容（直接读取markdown文件）
            log.info("📖 正在从知识库读取'{}'相关内容...", knowledgePoint);
            String knowledgeContext = knowledgeBaseService.getKnowledgeContext(knowledgePoint);
            
            log.info("📚 知识库上下文长度: {} 字符", knowledgeContext.length());
            
            // 2. 构建AI提示词（包含完整知识库内容）
            String systemPrompt = buildSystemPromptWithKnowledge(subject, knowledgePoint, type, difficulty, count, knowledgeContext);
            String userMessage = String.format("请基于上述知识库内容，生成%d道关于'%s'的%s题目，难度为%s。题目必须紧扣知识库内容，确保准确性。", 
                                              count, knowledgePoint, getTypeText(type), getDifficultyText(difficulty));
            
            // 3. 调用AI服务（知识库内容已在systemPrompt中）
            log.info("🤖 调用AI生成题目（知识库已注入提示词）...");
            String aiResponse = aiService.chatWithSystem(systemPrompt, userMessage);
            
            long duration = System.currentTimeMillis() - startTime;
            log.info("⏱️ AI响应耗时: {}ms", duration);
            
            // 4. 解析AI返回的JSON
            List<Question> questions = parseAIResponse(aiResponse, type);
            
            if (!questions.isEmpty()) {
                log.info("✅ 成功解析{}道题目", questions.size());
            }
            
            return questions;
            
        } catch (Exception e) {
            log.error("❌ AI生成题目失败", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * 构建AI提示词（包含完整知识库内容）
     */
    private String buildSystemPromptWithKnowledge(String subject, String knowledgePoint, String type, 
                                                  String difficulty, Integer count, String knowledgeContext) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是一位专业的中文教育专家和出题老师。\n\n");
        
        // ===== 核心：将知识库内容直接注入到提示词中 =====
        if (knowledgeContext != null && !knowledgeContext.isEmpty() && 
            !knowledgeContext.contains("未找到相关知识库内容")) {
            prompt.append("=".repeat(50)).append("\n");
            prompt.append("📚 知识库内容（请基于以下内容出题）\n");
            prompt.append("=".repeat(50)).append("\n\n");
            prompt.append(knowledgeContext).append("\n\n");
            prompt.append("=".repeat(50)).append("\n");
            prompt.append("以上是完整的知识库内容，请严格基于这些内容出题！\n");
            prompt.append("=".repeat(50)).append("\n\n");
        } else {
            prompt.append("⚠️ 注意：未找到相关知识库内容，请基于'").append(knowledgePoint).append("'的通用知识出题。\n\n");
        }
        
        prompt.append("**出题任务**：\n");
        prompt.append("- 学科/主题：").append(subject).append("\n");
        prompt.append("- 知识点：").append(knowledgePoint).append("\n");
        prompt.append("- 题目类型：").append(getTypeText(type)).append("\n");
        prompt.append("- 难度等级：").append(getDifficultyText(difficulty)).append("\n");
        prompt.append("- 题目数量：").append(count).append("道\n\n");
        
        prompt.append("**重要要求**：\n");
        prompt.append("1. ✅ 所有题目内容、选项、解析必须使用中文\n");
        prompt.append("2. ✅ 题目内容必须来自上述知识库，不要编造\n");
        prompt.append("3. ✅ 选项要有迷惑性，但答案必须准确\n");
        prompt.append("4. ✅ 解析要引用知识库中的具体内容\n");
        prompt.append("5. ✅ 难度要符合").append(getDifficultyText(difficulty)).append("级别\n");
        prompt.append("6. ✅ 题目要有实际应用价值\n");
        prompt.append("7. ✅ 编程题的题目描述、输入输出说明必须用中文\n\n");
        
        prompt.append("**输出格式**（严格遵守JSON格式）：\n");
        prompt.append("```json\n");
        prompt.append("{\n");
        prompt.append("  \"questions\": [\n");
        prompt.append("    {\n");
        prompt.append("      \"content\": \"题目内容（必须是中文，基于知识库）\",\n");
        
        if ("single_choice".equals(type) || "multiple_choice".equals(type)) {
            prompt.append("      \"options\": [\n");
            prompt.append("        {\"label\": \"A\", \"content\": \"选项A（中文）\"},\n");
            prompt.append("        {\"label\": \"B\", \"content\": \"选项B（中文）\"},\n");
            prompt.append("        {\"label\": \"C\", \"content\": \"选项C（中文）\"},\n");
            prompt.append("        {\"label\": \"D\", \"content\": \"选项D（中文）\"}\n");
            prompt.append("      ],\n");
        }
        
        prompt.append("      \"answer\": \"").append("single_choice".equals(type) ? "A" : "正确答案（中文）").append("\",\n");
        prompt.append("      \"analysis\": \"详细解析（中文，引用知识库内容）\",\n");
        prompt.append("      \"score\": 10\n");
        prompt.append("    }\n");
        prompt.append("  ]\n");
        prompt.append("}\n");
        prompt.append("```\n\n");
        
        prompt.append("⚠️ 注意：只输出JSON，不要有其他文字！所有内容必须是中文！\n");
        
        return prompt.toString();
    }
    
    /**
     * 解析AI返回的JSON
     */
    private List<Question> parseAIResponse(String aiResponse, String type) {
        List<Question> questions = new ArrayList<>();
        
        try {
            // 清理响应文本
            String cleanedResponse = aiResponse.trim();
            if (cleanedResponse.startsWith("```json")) {
                cleanedResponse = cleanedResponse.substring(7);
            }
            if (cleanedResponse.startsWith("```")) {
                cleanedResponse = cleanedResponse.substring(3);
            }
            if (cleanedResponse.endsWith("```")) {
                cleanedResponse = cleanedResponse.substring(0, cleanedResponse.length() - 3);
            }
            cleanedResponse = cleanedResponse.trim();
            
            log.debug("清理后的AI响应: {}", cleanedResponse.substring(0, Math.min(200, cleanedResponse.length())));
            
            // 解析JSON
            JsonNode root = objectMapper.readTree(cleanedResponse);
            JsonNode questionsNode = root.get("questions");
            
            if (questionsNode != null && questionsNode.isArray()) {
                for (JsonNode questionNode : questionsNode) {
                    Question question = new Question();
                    question.setType(type);
                    question.setContent(questionNode.get("content").asText());
                    question.setAnswer(questionNode.get("answer").asText());
                    question.setAnalysis(questionNode.has("analysis") ? 
                                        questionNode.get("analysis").asText() : "");
                    question.setScore(questionNode.has("score") ? 
                                     questionNode.get("score").asInt() : 10);
                    
                    // 解析选项
                    if (questionNode.has("options")) {
                        List<QuestionOption> options = new ArrayList<>();
                        JsonNode optionsNode = questionNode.get("options");
                        for (JsonNode optionNode : optionsNode) {
                            QuestionOption option = new QuestionOption();
                            option.setKey(optionNode.get("label").asText());
                            option.setContent(optionNode.get("content").asText());
                            options.add(option);
                        }
                        question.setOptions(options);
                    }
                    
                    questions.add(question);
                }
            }
            
            log.info("✅ 成功解析{}道题目", questions.size());
            
        } catch (Exception e) {
            log.error("❌ 解析AI响应失败", e);
            log.error("原始响应: {}", aiResponse);
        }
        
        return questions;
    }
    
    /**
     * 将题目缓存到题库（可选功能，用于离线场景）
     */
    private int cacheQuestionsToBank(List<Question> questions, String subject, String knowledgePoint) {
        if (questionBankMapper == null) {
            log.info("题库功能未启用，跳过缓存");
            return 0;
        }
        
        try {
            List<QuestionBank> bankQuestions = new ArrayList<>();
            
            for (Question question : questions) {
                QuestionBank bankQuestion = new QuestionBank();
                bankQuestion.setSubject(subject);
                bankQuestion.setKnowledgePoint(knowledgePoint);
                bankQuestion.setType(question.getType());
                bankQuestion.setDifficulty(question.getDifficulty());
                bankQuestion.setContent(question.getContent());
                bankQuestion.setAnswer(question.getAnswer());
                bankQuestion.setAnalysis(question.getAnalysis());
                bankQuestion.setScore(question.getScore());
                bankQuestion.setSource("ai_generated");
                bankQuestion.setUseCount(0);
                bankQuestion.setAvgScore(0.0);
                bankQuestion.setCreateTime(LocalDateTime.now());
                bankQuestion.setUpdateTime(LocalDateTime.now());
                
                // 将选项转换为JSON字符串
                if (question.getOptions() != null && !question.getOptions().isEmpty()) {
                    try {
                        String optionsJson = objectMapper.writeValueAsString(question.getOptions());
                        bankQuestion.setOptions(optionsJson);
                    } catch (Exception e) {
                        log.error("选项序列化失败", e);
                    }
                }
                
                bankQuestions.add(bankQuestion);
            }
            
            if (!bankQuestions.isEmpty()) {
                int count = questionBankMapper.batchInsert(bankQuestions);
                log.info("✅ 成功缓存{}道题目到题库", count);
                return count;
            }
        } catch (Exception e) {
            log.warn("缓存题目到题库失败: {}", e.getMessage());
        }
        
        return 0;
    }
    
    /**
     * 将题库题目转换为Question对象
     */
    private Question convertToQuestion(QuestionBank bankQuestion) {
        Question question = new Question();
        question.setType(bankQuestion.getType());
        question.setContent(bankQuestion.getContent());
        question.setAnswer(bankQuestion.getAnswer());
        question.setAnalysis(bankQuestion.getAnalysis());
        question.setScore(bankQuestion.getScore());
        question.setDifficulty(bankQuestion.getDifficulty());
        question.setKnowledgePoint(bankQuestion.getKnowledgePoint());
        
        // 解析选项
        if (bankQuestion.getOptions() != null && !bankQuestion.getOptions().isEmpty()) {
            try {
                List<QuestionOption> options = objectMapper.readValue(
                    bankQuestion.getOptions(), 
                    new TypeReference<List<QuestionOption>>() {});
                question.setOptions(options);
            } catch (Exception e) {
                log.error("选项反序列化失败", e);
            }
        }
        
        return question;
    }
    
    private String getTypeText(String type) {
        switch (type) {
            case "single_choice": return "单选题";
            case "multiple_choice": return "多选题";
            case "fill": return "填空题";
            case "short_answer": return "简答题";
            case "coding": return "编程题";
            default: return "选择题";
        }
    }
    
    private String getDifficultyText(String difficulty) {
        switch (difficulty) {
            case "easy": return "简单";
            case "medium": return "中等";
            case "hard": return "困难";
            default: return "中等";
        }
    }
}
