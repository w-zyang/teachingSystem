package com.experiment.pojo;

import java.util.List;

import lombok.Data;

@Data
public class PracticeAnswerDTO {
    private Long practiceId;
    private Long studentId;
    private List<AnswerItem> answers;
    
    @Data
    public static class AnswerItem {
        private Long questionId;
        private String answer;
        private String questionType; // choice, fill, short, coding
        private List<String> selectedOptions; // 多选题选中的选项
    }
} 