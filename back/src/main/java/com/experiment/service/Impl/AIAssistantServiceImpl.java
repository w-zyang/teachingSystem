package com.experiment.service.Impl;

import com.experiment.service.AIAssistantService;
import com.experiment.service.AIService;
import com.experiment.service.XunfeiPPTService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class AIAssistantServiceImpl implements AIAssistantService {
    
    @Autowired
    private AIService aiService;
    
    @Autowired
    private XunfeiPPTService xunfeiPPTService;
    
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
        
        try {
            Map<String, Object> courseInfo = (Map<String, Object>) data.get("courseInfo");
            List<String> options = (List<String>) data.get("options");
            
            if (courseInfo == null || options == null || options.isEmpty()) {
                throw new RuntimeException("课程信息或生成选项不能为空");
            }
            
            String courseName = (String) courseInfo.get("courseName");
            String courseType = (String) courseInfo.get("courseType");
            String outline = (String) courseInfo.get("outline");
            String duration = (String) courseInfo.get("duration");
            String difficulty = (String) courseInfo.get("difficulty");
            String requirements = (String) courseInfo.get("requirements");
            
            log.info("课程名称: {}, 类型: {}, 时长: {}, 难度: {}", courseName, courseType, duration, difficulty);
            log.info("生成选项: {}", options);
        
            String systemPrompt = buildEnhancedCourseDesignSystemPrompt(options);
            String userMessage = buildEnhancedCourseDesignUserMessage(
                courseName, courseType, outline, duration, difficulty, requirements
            );
        
            log.info("调用AI生成课程设计内容...");
            String aiResponse = aiService.chatWithSystem(systemPrompt, userMessage);
            log.info("AI生成完成，响应长度: {}", aiResponse.length());
            
            Map<String, Object> result = parseEnhancedCourseDesignResponse(aiResponse, options);
            
            if (options.contains("ppt")) {
                log.info("开始生成PPT...");
                try {
                    String pptContent = buildPPTContent(result, courseName, outline);
                    String taskId = xunfeiPPTService.generatePPT(pptContent, courseName);
                    
                    result.put("pptTaskId", taskId);
                    result.put("pptGenerating", true);
                    
                    log.info("PPT生成任务已创建，任务ID: {}", taskId);
                    
                    Map<String, Object> status = xunfeiPPTService.checkPPTStatus(taskId);
                    if ((Boolean) status.getOrDefault("completed", false)) {
                        if (status.containsKey("downloadUrl")) {
                            result.put("pptDownloadUrl", status.get("downloadUrl"));
                            result.put("pptGenerating", false);
                            log.info("PPT已生成完成: {}", status.get("downloadUrl"));
                        } else if (status.containsKey("error")) {
                            log.error("PPT生成失败: {}", status.get("error"));
                        }
                    }
                } catch (Exception e) {
                    log.error("PPT生成失败", e);
                    result.put("pptError", "PPT生成暂时不可用，讯飞API访问受限，请稍后重试或联系客服");
                    result.put("pptGenerating", false);
                }
            }
            
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
    
    private String buildEnhancedCourseDesignSystemPrompt(List<String> options) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是一位经验丰富的教学设计专家。请根据课程要求，设计完整的教学方案。\n\n");
        prompt.append("请按照以下格式返回JSON：\n");
        prompt.append("{\n");
        
        if (options.contains("ppt")) {
            prompt.append("  \"ppt\": {\n");
            prompt.append("    \"slides\": [\n");
            prompt.append("      {\"title\": \"标题\", \"content\": \"内容\"},\n");
            prompt.append("      ...\n");
            prompt.append("    ]\n");
            prompt.append("  },\n");
        }
        
        if (options.contains("lesson-plan")) {
            prompt.append("  \"lessonPlan\": {\n");
            prompt.append("    \"content\": \"教案内容\"\n");
            prompt.append("  },\n");
        }
        
        if (options.contains("content-design")) {
            prompt.append("  \"content\": [\n");
            prompt.append("    {\"title\": \"章节标题\", \"duration\": \"时长\", \"keyPoints\": \"重点\", \"description\": \"详细内容\"},\n");
            prompt.append("    ...\n");
            prompt.append("  ],\n");
        }
        
        if (options.contains("practice-exercises")) {
            prompt.append("  \"practice\": [\n");
            prompt.append("    {\"title\": \"练习标题\", \"type\": \"类型\", \"difficulty\": \"难度\", \"duration\": \"时长\", \"content\": \"练习内容\"},\n");
            prompt.append("    ...\n");
            prompt.append("  ],\n");
        }
        
        if (options.contains("exam-questions")) {
            prompt.append("  \"exam\": [\n");
            prompt.append("    {\"title\": \"题目\", \"type\": \"题型\", \"difficulty\": \"难度\", \"score\": \"分值\", \"options\": [\"选项\"], \"answer\": \"答案\"},\n");
            prompt.append("    ...\n");
            prompt.append("  ],\n");
        }
        
        if (options.contains("time-distribution")) {
            prompt.append("  \"schedule\": [\n");
            prompt.append("    {\"week\": \"第X周\", \"title\": \"标题\", \"description\": \"描述\", \"duration\": \"时长\", \"type\": \"类型\"},\n");
            prompt.append("    ...\n");
            prompt.append("  ],\n");
        }
        
        if (options.contains("teaching-suggestions")) {
            prompt.append("  \"suggestions\": [\n");
            prompt.append("    {\"title\": \"建议标题\", \"content\": \"建议内容\"},\n");
            prompt.append("    ...\n");
            prompt.append("  ]\n");
        }
        
        prompt.append("}\n\n");
        prompt.append("请确保返回的是有效的JSON格式，不要包含任何其他文本。");
        
        return prompt.toString();
    }
    
    private String buildEnhancedCourseDesignUserMessage(
            String courseName, String courseType, String outline, 
            String duration, String difficulty, String requirements) {
        StringBuilder message = new StringBuilder();
        message.append("请为以下课程设计教学方案：\n\n");
        message.append("课程名称：").append(courseName).append("\n");
        message.append("课程类型：").append(courseType).append("\n");
        message.append("课程时长：").append(duration).append("课时\n");
        message.append("难度等级：").append(difficulty).append("\n");
        message.append("课程大纲：").append(outline).append("\n");
        
        if (requirements != null && !requirements.isEmpty()) {
            message.append("特殊要求：").append(requirements).append("\n");
    }
    
        return message.toString();
    }
    
    private Map<String, Object> parseEnhancedCourseDesignResponse(String response, List<String> options) {
        try {
            response = response.replaceAll("```json\\s*", "").replaceAll("```\\s*", "").trim();
            Map<String, Object> result = objectMapper.readValue(response, Map.class);
            log.info("成功解析JSON响应");
            return result;
        } catch (Exception e) {
            log.warn("解析JSON失败，尝试从文本中提取内容: {}", e.getMessage());
            
            Map<String, Object> result = new HashMap<>();
            
            if (options.contains("lesson-plan")) {
                Map<String, Object> lessonPlan = new HashMap<>();
                lessonPlan.put("content", response);
                result.put("lessonPlan", lessonPlan);
            }
            
            if (options.contains("ppt")) {
                Map<String, Object> ppt = new HashMap<>();
                List<Map<String, String>> slides = new ArrayList<>();
                
                Map<String, String> slide1 = new HashMap<>();
                slide1.put("title", "课程介绍");
                slide1.put("content", response.substring(0, Math.min(200, response.length())));
                slides.add(slide1);
                
                ppt.put("slides", slides);
                result.put("ppt", ppt);
            }
            
            return result;
        }
    }
    
    private String buildPPTContent(Map<String, Object> designResult, String courseName, String outline) {
        StringBuilder content = new StringBuilder();
        
        content.append("课程名称：").append(courseName).append("\n\n");
        content.append("课程大纲：").append(outline).append("\n\n");
        
        if (designResult.containsKey("ppt")) {
            Map<String, Object> ppt = (Map<String, Object>) designResult.get("ppt");
            List<Map<String, String>> slides = (List<Map<String, String>>) ppt.get("slides");
            
            if (slides != null && !slides.isEmpty()) {
                for (int i = 0; i < slides.size(); i++) {
                    Map<String, String> slide = slides.get(i);
                    content.append("第").append(i + 1).append("页：").append(slide.get("title")).append("\n");
                    content.append(slide.get("content")).append("\n\n");
                }
            }
        }
        
        if (designResult.containsKey("content")) {
            List<Map<String, String>> contentList = (List<Map<String, String>>) designResult.get("content");
            if (contentList != null && !contentList.isEmpty()) {
                content.append("教学内容：\n");
                for (Map<String, String> section : contentList) {
                    content.append(section.get("title")).append("\n");
                    content.append(section.get("description")).append("\n\n");
                }
            }
        }
        
        return content.toString();
    }
    
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
