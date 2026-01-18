package com.experiment.service;

import java.util.List;
import java.util.Map;

/**
 * 知识库服务接口
 */
public interface KnowledgeBaseService {
    
    /**
     * 搜索知识库内容
     * @param query 搜索查询
     * @return 相关的内容片段
     */
    List<String> searchKnowledge(String query);
    
    /**
     * 获取知识库分类
     * @return 分类列表
     */
    Map<String, String> getCategories();
    
    /**
     * 获取指定分类的内容
     * @param category 分类
     * @return 内容列表
     */
    List<String> getCategoryContent(String category);
    
    /**
     * 获取知识库摘要
     * @return 知识库摘要信息
     */
    String getKnowledgeSummary();
    
    /**
     * 获取知识库上下文，用于AI回答
     * @param query 查询内容
     * @return 知识库上下文
     */
    String getKnowledgeContext(String query);
} 