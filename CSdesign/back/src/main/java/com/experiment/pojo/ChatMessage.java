package com.experiment.pojo;

import lombok.Data;

/**
 * 聊天消息DTO
 */
@Data
public class ChatMessage {
    
    /**
     * 消息角色：system, user, assistant
     */
    private String role;
    
    /**
     * 消息内容
     */
    private String content;
    
    public ChatMessage() {}
    
    public ChatMessage(String role, String content) {
        this.role = role;
        this.content = content;
    }
} 