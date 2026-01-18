package com.experiment.service.Impl;

import com.experiment.service.AIAssistantService;
import com.experiment.service.AIService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * AI助手服务实现类
 * 从AIController中提取的AI助手相关业务逻辑
 */
@Slf4j
@Service
public class AIAssistantServiceImpl implements AIAssistantService {
    
    @Autowired
    private AIService aiService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public String learningAssistant(String message) {
        log.info("学习助手收到问题: {}", message);
        
        String systemPrompt = "你是一位专业的学习助手，擅长解答各种学习问题。" +
                             "请用清晰、易懂的语言回答学生的问题，并提供学习建议。";
        
        try {
            String response = aiService.chatWithSystem(systemPrompt, message);
            log.info("学习助手回复完成");
            return response;
        } catch (Exception e) {
            log.error("学习助手处理失败", e);
            return "抱歉，学习助手暂时无法回答您的问题，请稍后重试。";
        }
    }
    
    @Override
    public String codeAssistant(String question) {
        log.info("代码助手收到问题: {}", question);
        
        String systemPrompt = "你是一位专业的编程助手，擅长解答编程问题。" +
                             "请提供清晰的代码示例和详细的解释。" +
                             "代码要规范、易读，并包含必要的注释。";
        
        try {
            String response = aiService.chatWithSystem(systemPrompt, question);
            log.info("代码助手回复完成");
            return response;
        } catch (Exception e) {
            log.error("代码助手处理失败", e);
            return "抱歉，代码助手暂时无法回答您的问题，请稍后重试。";
        }
    }
    
    @Override
    public String writingAssistant(String request) {
        log.info("写作助手收到请求: {}", request);
        
        String systemPrompt = "你是一位专业的写作助手，擅长帮助用户改进文章。" +
                             "请提供具体的修改建议，包括结构、语言、逻辑等方面。";
        
        try {
            String response = aiService.chatWithSystem(systemPrompt, request);
            log.info("写作助手回复完成");
            return response;
        } catch (Exception e) {
            log.error("写作助手处理失败", e);
            return "抱歉，写作助手暂时无法处理您的请求，请稍后重试。";
        }
    }
    
    @Override
    public Map<String, Object> courseDesign(Map<String, Object> data) {
        log.info("开始课程设计: {}", data);
        
        String subject = (String) data.get("subject");
        String level = (String) data.get("level");
        String duration = (String) data.get("duration");
        
        String systemPrompt = buildCourseDesignSystemPrompt();
        String userMessage = buildCourseDesignUserMessage(subject, level, duration);
        
        try {
            String response = aiService.chatWithSystem(systemPrompt, userMessage);
            Map<String, Object> result = parseCourseDesignResponse(response);
            
            log.info("课程设计完成");
            return result;
        } catch (Exception e) {
            log.error("课程设计失败", e);
            throw new RuntimeException("课程设计失败: " + e.getMessage());
        }
    }
    
    @Override
    public Map<String, Object> chatWithDocument(Map<String, Object> data) {
        log.info("开始与文档对话");
        
        String documentContent = (String) data.get("documentContent");
        String question = (String) data.get("question");
        
        String systemPrompt = "你是一位专业的文档分析助手。" +
                             "请基于提供的文档内容回答用户的问题。" +
                             "回答要准确、具体，并引用文档中的相关内容。";
        
        String userMessage = String.format(
            "文档内容：\n%s\n\n问题：%s",
            documentContent, question
        );
        
        try {
            String response = aiService.chatWithSystem(systemPrompt, userMessage);
            
            Map<String, Object> result = new HashMap<>();
            result.put("answer", response);
            
            log.info("文档对话完成");
            return result;
        } catch (Exception e) {
            log.error("文档对话失败", e);
            throw new RuntimeException("文档对话失败: " + e.getMessage());
        }
    }
    
    @Override
    public Map<String, Object> generateMindmap(Map<String, Object> data) {
        log.info("开始生成思维导图");
        
        String topic = (String) data.get("topic");
        String content = (String) data.get("content");
        
        String systemPrompt = "你是一位专业的思维导图设计专家。" +
                             "请根据提供的主题和内容，生成结构化的思维导图。" +
                             "返回JSON格式，包含节点和连接关系。";
        
        String userMessage = String.format(
            "主题：%s\n内容：%s\n\n请生成思维导图",
            topic, content
        );
        
        try {
            String response = aiService.chatWithSystem(systemPrompt, userMessage);
            Map<String, Object> result = parseMindmapResponse(response);
            
            log.info("思维导图生成完成");
            return result;
        } catch (Exception e) {
            log.error("思维导图生成失败", e);
            throw new RuntimeException("思维导图生成失败: " + e.getMessage());
        }
    }
    
    /**
     * 构建课程设计系统提示词
     */
    private String buildCourseDesignSystemPrompt() {
        return "你是一位经验丰富的教学设计专家。" +
               "请根据课程要求，设计完整的教学方案，包括：\n" +
               "1. 教学目标\n" +
               "2. 教学内容\n" +
               "3. 教学方法\n" +
               "4. 教学活动\n" +
               "5. 评估方式\n" +
               "请返回JSON格式的课程设计方案。";
    }
    
    /**
     * 构建课程设计用户消息
     */
    private String buildCourseDesignUserMessage(String subject, String level, String duration) {
        return String.format(
            "请设计一门课程：\n" +
            "学科：%s\n" +
            "难度级别：%s\n" +
            "课程时长：%s\n",
            subject, level, duration
        );
    }
    
    /**
     * 解析课程设计响应
     */
    private Map<String, Object> parseCourseDesignResponse(String response) {
        try {
            response = response.replaceAll("```json\\s*", "").replaceAll("```\\s*", "").trim();
            return objectMapper.readValue(response, Map.class);
        } catch (Exception e) {
            log.warn("解析课程设计响应失败，返回文本格式");
            Map<String, Object> result = new HashMap<>();
            result.put("content", response);
            return result;
        }
    }
    
    /**
     * 解析思维导图响应
     */
    private Map<String, Object> parseMindmapResponse(String response) {
        try {
            response = response.replaceAll("```json\\s*", "").replaceAll("```\\s*", "").trim();
            return objectMapper.readValue(response, Map.class);
        } catch (Exception e) {
            log.warn("解析思维导图响应失败");
            Map<String, Object> result = new HashMap<>();
            result.put("nodes", new HashMap<>());
            result.put("edges", new HashMap<>());
            return result;
        }
    }
}
