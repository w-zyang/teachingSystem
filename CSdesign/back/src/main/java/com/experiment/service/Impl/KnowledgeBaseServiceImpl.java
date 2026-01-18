package com.experiment.service.Impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.experiment.service.KnowledgeBaseService;

import lombok.extern.slf4j.Slf4j;

/**
 * 知识库服务实现类
 */
@Slf4j
@Service
public class KnowledgeBaseServiceImpl implements KnowledgeBaseService {
    
    private static final String KNOWLEDGE_BASE_PATH = "front(2)/front(1)/public/knowledge-base";
    private static final Map<String, String> CATEGORIES = new HashMap<>();
    
    static {
        CATEGORIES.put("tensorflow-js", "TensorFlow.js");
        CATEGORIES.put("computer-science", "计算机科学");
        CATEGORIES.put("programming", "编程技术");
        CATEGORIES.put("algorithms", "算法与数据结构");
    }
    
    @Override
    public List<String> searchKnowledge(String query) {
        List<String> results = new ArrayList<>();
        String lowerQuery = query.toLowerCase();
        
        try {
            // 遍历知识库目录
            Path knowledgePath = Paths.get(KNOWLEDGE_BASE_PATH);
            if (!Files.exists(knowledgePath)) {
                log.warn("知识库目录不存在: {}", knowledgePath);
                return results;
            }
            
            // 递归搜索所有markdown文件
            Files.walk(knowledgePath)
                .filter(path -> path.toString().endsWith(".md"))
                .forEach(path -> {
                    try {
                        String content = Files.readString(path);
                        if (content.toLowerCase().contains(lowerQuery)) {
                            // 提取包含查询关键词的段落
                            String[] paragraphs = content.split("\n\n");
                            for (String paragraph : paragraphs) {
                                if (paragraph.toLowerCase().contains(lowerQuery) && 
                                    paragraph.trim().length() > 50) {
                                    results.add(paragraph.trim());
                                }
                            }
                        }
                    } catch (IOException e) {
                        log.error("读取知识库文件失败: {}", path, e);
                    }
                });
            
            // 限制返回结果数量
            return results.stream()
                .distinct()
                .limit(10)
                .collect(Collectors.toList());
                
        } catch (IOException e) {
            log.error("搜索知识库失败", e);
            return results;
        }
    }
    
    @Override
    public Map<String, String> getCategories() {
        return new HashMap<>(CATEGORIES);
    }
    
    @Override
    public List<String> getCategoryContent(String category) {
        List<String> contents = new ArrayList<>();
        
        try {
            Path categoryPath = Paths.get(KNOWLEDGE_BASE_PATH, category);
            if (!Files.exists(categoryPath)) {
                log.warn("知识库分类目录不存在: {}", categoryPath);
                return contents;
            }
            
            // 读取该分类下的所有markdown文件
            Files.walk(categoryPath)
                .filter(path -> path.toString().endsWith(".md"))
                .forEach(path -> {
                    try {
                        String content = Files.readString(path);
                        // 提取标题和主要内容
                        String[] lines = content.split("\n");
                        StringBuilder summary = new StringBuilder();
                        
                        for (String line : lines) {
                            if (line.startsWith("#")) {
                                summary.append(line).append("\n");
                            } else if (line.trim().length() > 0 && summary.length() < 500) {
                                summary.append(line).append("\n");
                            }
                        }
                        
                        if (summary.length() > 0) {
                            contents.add(summary.toString().trim());
                        }
                    } catch (IOException e) {
                        log.error("读取分类内容失败: {}", path, e);
                    }
                });
                
        } catch (IOException e) {
            log.error("获取分类内容失败", e);
        }
        
        return contents;
    }
    
    @Override
    public String getKnowledgeSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("知识库包含以下内容：\n\n");
        
        for (Map.Entry<String, String> entry : CATEGORIES.entrySet()) {
            summary.append("**").append(entry.getValue()).append("**\n");
            List<String> contents = getCategoryContent(entry.getKey());
            summary.append("包含 ").append(contents.size()).append(" 个文档\n\n");
        }
        
        return summary.toString();
    }
    
    /**
     * 获取知识库上下文，用于AI回答
     */
    public String getKnowledgeContext(String query) {
        List<String> searchResults = searchKnowledge(query);
        if (searchResults.isEmpty()) {
            return "未找到相关知识库内容。";
        }
        
        StringBuilder context = new StringBuilder();
        context.append("根据知识库检索到的相关信息：\n\n");
        
        for (int i = 0; i < Math.min(searchResults.size(), 5); i++) {
            context.append("**片段 ").append(i + 1).append("：**\n");
            context.append(searchResults.get(i)).append("\n\n");
        }
        
        return context.toString();
    }
} 