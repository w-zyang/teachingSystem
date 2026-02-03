package com.experiment.service.Impl;

import com.experiment.mapper.StudentExamMapper;
import com.experiment.mapper.QuestionMapper;
import com.experiment.mapper.ErrorQuestionRecordMapper;
import com.experiment.pojo.ErrorQuestionAnalysisDTO;
import com.experiment.pojo.ErrorQuestionTrainingDTO;
import com.experiment.pojo.ErrorQuestionRecord;
import com.experiment.pojo.StudentExam;
import com.experiment.pojo.Question;
import com.experiment.service.ErrorQuestionAnalysisService;
import com.experiment.service.AIService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ErrorQuestionAnalysisServiceImpl implements ErrorQuestionAnalysisService {

    @Autowired
    private StudentExamMapper studentExamMapper;
    
    @Autowired
    private QuestionMapper questionMapper;
    
    @Autowired
    private ErrorQuestionRecordMapper errorQuestionRecordMapper;
    
    @Autowired
    private AIService aiService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<ErrorQuestionAnalysisDTO> analyzeStudentErrorQuestions(Long studentId) {
        log.info("开始分析学生 {} 的历史错题", studentId);
        
        List<ErrorQuestionAnalysisDTO> errorAnalysisList = new ArrayList<>();
        
        try {
            // 从错题记录表中查询
            List<ErrorQuestionRecord> errorRecords = errorQuestionRecordMapper.selectByStudentId(studentId);
            
            // 转换为DTO
            for (ErrorQuestionRecord record : errorRecords) {
                ErrorQuestionAnalysisDTO dto = new ErrorQuestionAnalysisDTO();
                
                // 处理questionId
                try {
                    dto.setQuestionId(Long.parseLong(record.getQuestionId().replaceAll("[^0-9]", "")));
                } catch (Exception e) {
                    dto.setQuestionId(record.getId()); // 使用记录ID作为备选
                }
                
                dto.setQuestionContent(record.getQuestionContent());
                dto.setQuestionType(record.getQuestionType());
                dto.setKnowledgePoint(record.getKnowledgePoint());
                dto.setTopic(record.getKnowledgePoint()); // 使用知识点作为主题
                dto.setDifficulty("medium"); // 默认中等难度
                dto.setStudentAnswer(record.getUserAnswer());
                dto.setCorrectAnswer(record.getCorrectAnswer());
                dto.setErrorType(record.getErrorType());
                dto.setErrorReason(record.getErrorReason());
                dto.setErrorCount(record.getErrorCount());
                dto.setErrorRate(record.getErrorRate());
                dto.setLastErrorTime(record.getCreateTime());
                dto.setRelatedConcepts(Arrays.asList(record.getKnowledgePoint()));
                dto.setImprovementSuggestion("建议加强 " + record.getKnowledgePoint() + " 的练习");
                
                // 解析选项（如果有）
                if (record.getQuestionOptions() != null && !record.getQuestionOptions().isEmpty()) {
                    try {
                        List<Map<String, String>> options = objectMapper.readValue(
                            record.getQuestionOptions(), 
                            new TypeReference<List<Map<String, String>>>() {}
                        );
                        dto.setOptions(options);
                    } catch (JsonProcessingException e) {
                        log.warn("解析题目选项失败: {}", e.getMessage());
                        dto.setOptions(null);
                    }
                }
                
                errorAnalysisList.add(dto);
            }
            
            log.info("学生 {} 的错题分析完成，共发现 {} 道错题", studentId, errorAnalysisList.size());
        } catch (Exception e) {
            log.error("分析学生错题失败", e);
        }
        
        return errorAnalysisList;
    }

    @Override
    public ErrorQuestionAnalysisDTO analyzeSpecificErrorQuestion(Long studentId, Long questionId) {
        log.info("分析学生 {} 的特定错题 {}", studentId, questionId);
        
        try {
            // 这里应该查询特定题目的错误记录
            // 目前使用模拟数据
            ErrorQuestionAnalysisDTO analysis = new ErrorQuestionAnalysisDTO();
            analysis.setQuestionId(questionId);
            analysis.setQuestionContent("Linux系统中，下列哪个命令用于查看当前目录下的文件？");
            analysis.setQuestionType("choice");
            analysis.setKnowledgePoint("Linux基础命令");
            analysis.setTopic("操作系统");
            analysis.setDifficulty("easy");
            analysis.setStudentAnswer("B");
            analysis.setCorrectAnswer("A");
            analysis.setErrorType("概念混淆");
            analysis.setErrorReason("将ls命令与cd命令功能混淆");
            analysis.setErrorCount(3);
            analysis.setErrorRate(75.0);
            analysis.setLastErrorTime(LocalDateTime.now().minusDays(2));
            analysis.setRelatedConcepts(Arrays.asList("文件系统", "命令行操作", "目录浏览"));
            analysis.setImprovementSuggestion("加强Linux基础命令的练习，特别是文件操作相关命令");
            
            return analysis;
        } catch (Exception e) {
            log.error("分析特定错题失败", e);
            return null;
        }
    }

    @Override
    public ErrorQuestionTrainingDTO generateSimilarQuestions(Long studentId, Long originalQuestionId, Integer questionCount) {
        log.info("为学生 {} 生成相似题目训练，原题ID: {}, 数量: {}", studentId, originalQuestionId, questionCount);
        
        try {
            // 获取原题信息
            ErrorQuestionAnalysisDTO originalError = analyzeSpecificErrorQuestion(studentId, originalQuestionId);
            
            if (originalError == null) {
                throw new RuntimeException("无法获取原题信息");
            }
            
            // 使用AI生成相似题目
            String prompt = buildSimilarQuestionPrompt(originalError, questionCount);
            String aiResponse = aiService.chatWithSystem(
                "你是一个专业的教育题目生成专家，擅长根据学生的错题情况生成相似的练习题目。",
                prompt
            );
            
            List<Map<String, Object>> questions = parseQuestionsFromAI(aiResponse);
            
            // 创建训练DTO
            ErrorQuestionTrainingDTO training = new ErrorQuestionTrainingDTO();
            training.setStudentId(studentId);
            training.setTrainingType("similar");
            training.setOriginalQuestionId(originalQuestionId);
            training.setKnowledgePoint(originalError.getKnowledgePoint());
            training.setDifficulty(originalError.getDifficulty());
            training.setQuestionCount(questionCount);
            training.setQuestions(questions);
            training.setAnalysisReport(generateAnalysisReport(originalError, "similar"));
            training.setTargetErrorTypes(Arrays.asList(originalError.getErrorType()));
            training.setExpectedImprovement(75.0);
            training.setCreateTime(LocalDateTime.now());
            
            log.info("成功生成 {} 道相似题目", questions.size());
            return training;
            
        } catch (Exception e) {
            log.error("生成相似题目失败", e);
            // 返回备用题目
            return generateFallbackTraining(studentId, originalQuestionId, questionCount, "similar");
        }
    }

    @Override
    public ErrorQuestionTrainingDTO generateKnowledgePointQuestions(Long studentId, String knowledgePoint, Integer questionCount) {
        log.info("为学生 {} 生成知识点训练题目，知识点: {}, 数量: {}", studentId, knowledgePoint, questionCount);
        
        try {
            // 分析该知识点的历史错题
            List<ErrorQuestionAnalysisDTO> relatedErrors = analyzeStudentErrorQuestions(studentId)
                .stream()
                .filter(error -> knowledgePoint.equals(error.getKnowledgePoint()))
                .collect(Collectors.toList());
            
            // 构建AI提示词
            String prompt = buildKnowledgePointPrompt(knowledgePoint, relatedErrors, questionCount);
            String aiResponse = aiService.chatWithSystem(
                "你是一个专业的教育题目生成专家，擅长根据特定知识点生成有针对性的练习题目。",
                prompt
            );
            
            List<Map<String, Object>> questions = parseQuestionsFromAI(aiResponse);
            
            // 创建训练DTO
            ErrorQuestionTrainingDTO training = new ErrorQuestionTrainingDTO();
            training.setStudentId(studentId);
            training.setTrainingType("knowledge_point");
            training.setKnowledgePoint(knowledgePoint);
            training.setDifficulty("medium");
            training.setQuestionCount(questionCount);
            training.setQuestions(questions);
            training.setAnalysisReport(generateKnowledgePointReport(knowledgePoint, relatedErrors));
            training.setTargetErrorTypes(relatedErrors.stream()
                .map(ErrorQuestionAnalysisDTO::getErrorType)
                .distinct()
                .collect(Collectors.toList()));
            training.setExpectedImprovement(80.0);
            training.setCreateTime(LocalDateTime.now());
            
            log.info("成功生成 {} 道知识点训练题目", questions.size());
            return training;
            
        } catch (Exception e) {
            log.error("生成知识点题目失败", e);
            return generateFallbackTraining(studentId, null, questionCount, "knowledge_point");
        }
    }

    @Override
    public ErrorQuestionTrainingDTO generateComprehensiveTraining(Long studentId, Integer questionCount, List<Map<String, Object>> selectedErrorQuestions) {
        log.info("为学生 {} 生成综合错题训练，数量: {}，基于 {} 道选中错题", 
                 studentId, questionCount, selectedErrorQuestions != null ? selectedErrorQuestions.size() : 0);
        
        try {
            List<ErrorQuestionAnalysisDTO> targetErrors;
            
            // 如果有选中的错题，使用选中的错题；否则使用所有错题
            if (selectedErrorQuestions != null && !selectedErrorQuestions.isEmpty()) {
                // 获取学生所有错题
                List<ErrorQuestionAnalysisDTO> allErrors = analyzeStudentErrorQuestions(studentId);
                
                // 筛选出选中的错题
                targetErrors = allErrors.stream()
                    .filter(error -> selectedErrorQuestions.stream()
                        .anyMatch(selected -> {
                            Object selectedId = selected.get("questionId");
                            if (selectedId instanceof Number) {
                                return error.getQuestionId().equals(((Number) selectedId).longValue());
                            }
                            return false;
                        }))
                    .collect(Collectors.toList());
                
                log.info("筛选出 {} 道选中的错题", targetErrors.size());
            } else {
                // 获取学生所有错题
                targetErrors = analyzeStudentErrorQuestions(studentId);
            }
            
            if (targetErrors.isEmpty()) {
                throw new RuntimeException("未发现错题记录");
            }
            
            // 提取知识点和错误类型
            List<String> knowledgePoints = targetErrors.stream()
                .map(ErrorQuestionAnalysisDTO::getKnowledgePoint)
                .distinct()
                .collect(Collectors.toList());
            
            List<String> errorTypes = targetErrors.stream()
                .map(ErrorQuestionAnalysisDTO::getErrorType)
                .distinct()
                .collect(Collectors.toList());
            
            // 构建优化的提示词，强调相似性
            String prompt = buildEnhancedComprehensivePrompt(targetErrors, knowledgePoints, errorTypes, questionCount);
            
            log.info("发送给AI的提示词: {}", prompt);
            
            String aiResponse = aiService.chatWithSystem(
                "你是一个专业的教育题目生成专家。你的任务是根据学生的错题，生成高度相似的训练题目。要求：1) 保持相同的知识点和题型 2) 题目难度和考查方式要相似 3) 只改变具体的数据、场景或表述，核心考点必须一致。4) 必须严格返回JSON数组格式，不要添加任何解释文字。",
                prompt
            );
            
            log.info("收到AI响应，长度: {} 字符", aiResponse != null ? aiResponse.length() : 0);
            
            List<Map<String, Object>> questions = parseQuestionsFromAI(aiResponse);
            
            // 创建训练DTO
            ErrorQuestionTrainingDTO training = new ErrorQuestionTrainingDTO();
            training.setStudentId(studentId);
            training.setTrainingType("comprehensive");
            training.setQuestionCount(questionCount);
            training.setQuestions(questions);
            training.setAnalysisReport(generateEnhancedComprehensiveReport(targetErrors, knowledgePoints));
            training.setTargetErrorTypes(errorTypes);
            training.setExpectedImprovement(85.0);
            training.setCreateTime(LocalDateTime.now());
            
            log.info("成功生成 {} 道综合训练题目", questions.size());
            return training;
            
        } catch (Exception e) {
            log.error("生成综合训练题目失败", e);
            return generateFallbackTraining(studentId, null, questionCount, "comprehensive");
        }
    }

    @Override
    public Map<String, Object> getErrorQuestionStatistics(Long studentId) {
        log.info("获取学生 {} 的错题统计", studentId);
        
        Map<String, Object> statistics = new HashMap<>();
        
        try {
            List<ErrorQuestionAnalysisDTO> errorQuestions = analyzeStudentErrorQuestions(studentId);
            
            statistics.put("totalErrorQuestions", errorQuestions.size());
            statistics.put("averageErrorRate", errorQuestions.stream()
                .mapToDouble(ErrorQuestionAnalysisDTO::getErrorRate)
                .average()
                .orElse(0.0));
            
            // 按知识点分组统计
            Map<String, Long> knowledgePointCount = errorQuestions.stream()
                .collect(Collectors.groupingBy(
                    ErrorQuestionAnalysisDTO::getKnowledgePoint,
                    Collectors.counting()
                ));
            statistics.put("knowledgePointDistribution", knowledgePointCount);
            
            // 按题目类型分组统计
            Map<String, Long> questionTypeCount = errorQuestions.stream()
                .collect(Collectors.groupingBy(
                    ErrorQuestionAnalysisDTO::getQuestionType,
                    Collectors.counting()
                ));
            statistics.put("questionTypeDistribution", questionTypeCount);
            
            // 按错误类型分组统计
            Map<String, Long> errorTypeCount = errorQuestions.stream()
                .collect(Collectors.groupingBy(
                    ErrorQuestionAnalysisDTO::getErrorType,
                    Collectors.counting()
                ));
            statistics.put("errorTypeDistribution", errorTypeCount);
            
        } catch (Exception e) {
            log.error("获取错题统计失败", e);
        }
        
        return statistics;
    }

    @Override
    public Map<String, Object> getErrorQuestionDistribution(Long studentId) {
        Map<String, Object> distribution = new HashMap<>();
        
        try {
            List<ErrorQuestionAnalysisDTO> errorQuestions = analyzeStudentErrorQuestions(studentId);
            
            // 难度分布
            Map<String, Long> difficultyDistribution = errorQuestions.stream()
                .collect(Collectors.groupingBy(
                    ErrorQuestionAnalysisDTO::getDifficulty,
                    Collectors.counting()
                ));
            distribution.put("difficultyDistribution", difficultyDistribution);
            
            // 主题分布
            Map<String, Long> topicDistribution = errorQuestions.stream()
                .collect(Collectors.groupingBy(
                    ErrorQuestionAnalysisDTO::getTopic,
                    Collectors.counting()
                ));
            distribution.put("topicDistribution", topicDistribution);
            
            // 时间分布（最近30天）
            Map<String, Long> timeDistribution = errorQuestions.stream()
                .filter(error -> error.getLastErrorTime().isAfter(LocalDateTime.now().minusDays(30)))
                .collect(Collectors.groupingBy(
                    error -> error.getLastErrorTime().toLocalDate().toString(),
                    Collectors.counting()
                ));
            distribution.put("recentTimeDistribution", timeDistribution);
            
        } catch (Exception e) {
            log.error("获取错题分布失败", e);
        }
        
        return distribution;
    }

    @Override
    public Map<String, Object> evaluateTrainingEffect(Long studentId, Long trainingId, List<String> answers) {
        log.info("评估学生 {} 的训练效果，训练ID: {}", studentId, trainingId);
        
        Map<String, Object> evaluation = new HashMap<>();
        
        try {
            // 这里应该根据训练ID获取训练题目，然后评估答案
            // 目前使用模拟评估
            
            int correctCount = 0;
            List<Map<String, Object>> detailedResults = new ArrayList<>();
            
            for (int i = 0; i < answers.size(); i++) {
                String userAnswer = answers.get(i);
                // 简单模拟：假设正确答案是A
                boolean isCorrect = "A".equals(userAnswer);
                if (isCorrect) correctCount++;
                
                Map<String, Object> result = new HashMap<>();
                result.put("questionIndex", i + 1);
                result.put("userAnswer", userAnswer);
                result.put("correctAnswer", "A");
                result.put("isCorrect", isCorrect);
                result.put("errorType", isCorrect ? null : determineErrorType(userAnswer, "A"));
                detailedResults.add(result);
            }
            
            double accuracy = answers.size() > 0 ? (double) correctCount / answers.size() * 100 : 0;
            
            evaluation.put("accuracy", accuracy);
            evaluation.put("correctCount", correctCount);
            evaluation.put("totalQuestions", answers.size());
            evaluation.put("detailedResults", detailedResults);
            evaluation.put("improvementSuggestions", generateImprovementSuggestions(accuracy, detailedResults));
            evaluation.put("nextSteps", generateNextSteps(accuracy));
            
        } catch (Exception e) {
            log.error("评估训练效果失败", e);
        }
        
        return evaluation;
    }

    // 私有辅助方法

    private List<ErrorQuestionAnalysisDTO> generateMockErrorQuestions(Long studentId) {
        List<ErrorQuestionAnalysisDTO> errorQuestions = new ArrayList<>();
        
        // 模拟数据1
        ErrorQuestionAnalysisDTO error1 = new ErrorQuestionAnalysisDTO();
        error1.setQuestionId(1L);
        error1.setQuestionContent("Linux系统中，下列哪个命令用于查看当前目录下的文件？");
        error1.setQuestionType("choice");
        error1.setKnowledgePoint("Linux基础命令");
        error1.setTopic("操作系统");
        error1.setDifficulty("easy");
        error1.setStudentAnswer("B");
        error1.setCorrectAnswer("A");
        error1.setErrorType("概念混淆");
        error1.setErrorReason("将ls命令与cd命令功能混淆");
        error1.setErrorCount(3);
        error1.setErrorRate(75.0);
        error1.setLastErrorTime(LocalDateTime.now().minusDays(2));
        error1.setRelatedConcepts(Arrays.asList("文件系统", "命令行操作", "目录浏览"));
        error1.setImprovementSuggestion("加强Linux基础命令的练习");
        errorQuestions.add(error1);
        
        // 模拟数据2
        ErrorQuestionAnalysisDTO error2 = new ErrorQuestionAnalysisDTO();
        error2.setQuestionId(2L);
        error2.setQuestionContent("数据结构中，栈的特点是什么？");
        error2.setQuestionType("choice");
        error2.setKnowledgePoint("数据结构基础");
        error2.setTopic("数据结构与算法");
        error2.setDifficulty("medium");
        error2.setStudentAnswer("C");
        error2.setCorrectAnswer("A");
        error2.setErrorType("理解错误");
        error2.setErrorReason("对LIFO(后进先出)概念理解不准确");
        error2.setErrorCount(2);
        error2.setErrorRate(66.7);
        error2.setLastErrorTime(LocalDateTime.now().minusDays(5));
        error2.setRelatedConcepts(Arrays.asList("LIFO", "数据结构", "栈操作"));
        error2.setImprovementSuggestion("重点复习栈的基本概念和操作");
        errorQuestions.add(error2);
        
        // 模拟数据3
        ErrorQuestionAnalysisDTO error3 = new ErrorQuestionAnalysisDTO();
        error3.setQuestionId(3L);
        error3.setQuestionContent("网络协议TCP和UDP的主要区别是什么？");
        error3.setQuestionType("short");
        error3.setKnowledgePoint("网络协议");
        error3.setTopic("计算机网络");
        error3.setDifficulty("hard");
        error3.setStudentAnswer("TCP比UDP快");
        error3.setCorrectAnswer("TCP是面向连接的可靠协议，UDP是无连接的不可靠协议");
        error3.setErrorType("知识点缺失");
        error3.setErrorReason("对协议特性理解不全面");
        error3.setErrorCount(4);
        error3.setErrorRate(80.0);
        error3.setLastErrorTime(LocalDateTime.now().minusDays(1));
        error3.setRelatedConcepts(Arrays.asList("传输协议", "可靠性", "连接机制"));
        error3.setImprovementSuggestion("系统学习网络协议的基本特性");
        errorQuestions.add(error3);
        
        return errorQuestions;
    }

    private String buildSimilarQuestionPrompt(ErrorQuestionAnalysisDTO originalError, Integer questionCount) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("请根据以下错题信息，生成").append(questionCount).append("道相似的练习题目：\n\n");
        prompt.append("原题内容：").append(originalError.getQuestionContent()).append("\n");
        prompt.append("知识点：").append(originalError.getKnowledgePoint()).append("\n");
        prompt.append("题目类型：").append(originalError.getQuestionType()).append("\n");
        prompt.append("错误类型：").append(originalError.getErrorType()).append("\n");
        prompt.append("错误原因：").append(originalError.getErrorReason()).append("\n\n");
        prompt.append("要求：\n");
        prompt.append("1. 题目应该针对学生的薄弱环节\n");
        prompt.append("2. 保持相同的知识点和难度\n");
        prompt.append("3. 避免完全相同的题目\n");
        prompt.append("4. 返回JSON格式的题目数组\n\n");
        prompt.append("返回格式：\n");
        prompt.append("[{\"title\":\"题目内容\",\"type\":\"").append(originalError.getQuestionType());
        prompt.append("\",\"options\":[\"A. 选项1\",\"B. 选项2\",\"C. 选项3\",\"D. 选项4\"],\"answer\":\"A\",\"explanation\":\"解析\"}]");
        
        return prompt.toString();
    }

    private String buildKnowledgePointPrompt(String knowledgePoint, List<ErrorQuestionAnalysisDTO> relatedErrors, Integer questionCount) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("请根据知识点\"").append(knowledgePoint).append("\"生成").append(questionCount).append("道练习题目：\n\n");
        
        if (!relatedErrors.isEmpty()) {
            prompt.append("学生在该知识点的常见错误：\n");
            for (ErrorQuestionAnalysisDTO error : relatedErrors) {
                prompt.append("- ").append(error.getErrorType()).append("：").append(error.getErrorReason()).append("\n");
            }
            prompt.append("\n");
        }
        
        prompt.append("要求：\n");
        prompt.append("1. 针对上述错误类型设计题目\n");
        prompt.append("2. 覆盖该知识点的核心概念\n");
        prompt.append("3. 题目难度渐进，由易到难\n");
        prompt.append("4. 返回JSON格式的题目数组\n\n");
        prompt.append("返回格式：\n");
        prompt.append("[{\"title\":\"题目内容\",\"type\":\"choice\",\"options\":[\"A. 选项1\",\"B. 选项2\",\"C. 选项3\",\"D. 选项4\"],\"answer\":\"A\",\"explanation\":\"解析\"}]");
        
        return prompt.toString();
    }

    private String buildEnhancedComprehensivePrompt(List<ErrorQuestionAnalysisDTO> targetErrors, 
                                                     List<String> knowledgePoints, 
                                                     List<String> errorTypes, 
                                                     Integer questionCount) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("【重要】必须生成恰好 ").append(questionCount).append(" 道题目，不能多也不能少！\n\n");
        
        prompt.append("【学生的错题详情】\n");
        for (int i = 0; i < Math.min(targetErrors.size(), 5); i++) {
            ErrorQuestionAnalysisDTO error = targetErrors.get(i);
            prompt.append((i + 1)).append(". 原题：").append(error.getQuestionContent()).append("\n");
            
            // 如果有选项，也展示出来
            if (error.getOptions() != null && !error.getOptions().isEmpty()) {
                prompt.append("   选项：\n");
                for (Map<String, String> option : error.getOptions()) {
                    prompt.append("   ").append(option.get("key")).append(". ").append(option.get("content")).append("\n");
                }
            }
            
            prompt.append("   知识点：").append(error.getKnowledgePoint()).append("\n");
            prompt.append("   题型：").append(getQuestionTypeText(error.getQuestionType())).append("\n");
            prompt.append("   学生答案：").append(error.getStudentAnswer()).append("（错误）\n");
            prompt.append("   正确答案：").append(error.getCorrectAnswer()).append("\n");
            prompt.append("   错误原因：").append(error.getErrorReason()).append("\n\n");
        }
        
        prompt.append("【生成要求】\n");
        prompt.append("1. **题目数量**：必须生成恰好 ").append(questionCount).append(" 道题目\n");
        prompt.append("2. **高度相似**：每道题都要与上面某道原题在知识点、题型、考查方式上完全一致\n");
        prompt.append("3. **只改表面**：只改变具体命令名、函数名、变量名、数据等，核心考点必须相同\n");
        prompt.append("4. **题型匹配**：原题是选择题就生成选择题，是填空题就生成填空题\n");
        prompt.append("5. **选项设计**：选择题的错误选项要设计成学生容易混淆的内容\n\n");
        
        prompt.append("【示例】\n");
        prompt.append("原题：\"Linux系统中，下列哪个命令用于查看当前目录下的文件？\"\n");
        prompt.append("相似题：\"Linux系统中，下列哪个命令用于显示当前工作目录的路径？\"\n\n");
        
        prompt.append("【返回格式】\n");
        prompt.append("直接返回包含 ").append(questionCount).append(" 个题目的JSON数组：\n");
        prompt.append("[{\"title\":\"题目内容\",\"type\":\"choice\",\"options\":[\"A. 选项1\",\"B. 选项2\",\"C. 选项3\",\"D. 选项4\"],\"answer\":\"A\",\"explanation\":\"解析\"}]");
        
        return prompt.toString();
    }
    
    private String getQuestionTypeText(String type) {
        switch (type) {
            case "choice": return "选择题";
            case "fill": return "填空题";
            case "short": return "简答题";
            case "coding": return "编程题";
            default: return type;
        }
    }
    
    private String generateEnhancedComprehensiveReport(List<ErrorQuestionAnalysisDTO> targetErrors, List<String> knowledgePoints) {
        StringBuilder report = new StringBuilder();
        report.append("**个性化错题训练报告**\n\n");
        report.append("基于您选中的 ").append(targetErrors.size()).append(" 道错题生成\n");
        report.append("涉及知识点：").append(String.join("、", knowledgePoints)).append("\n\n");
        
        report.append("**错题分析**\n");
        Map<String, Long> errorTypeCount = targetErrors.stream()
            .collect(Collectors.groupingBy(ErrorQuestionAnalysisDTO::getErrorType, Collectors.counting()));
        
        for (Map.Entry<String, Long> entry : errorTypeCount.entrySet()) {
            report.append("- ").append(entry.getKey()).append("：").append(entry.getValue()).append("次\n");
        }
        
        report.append("\n**训练目标**\n");
        report.append("1. 针对性强化选中错题涉及的知识点\n");
        report.append("2. 通过相似题目练习，巩固薄弱环节\n");
        report.append("3. 纠正错误理解，建立正确的知识体系\n");
        
        return report.toString();
    }

    private List<Map<String, Object>> parseQuestionsFromAI(String aiResponse) {
        log.info("AI原始响应内容: {}", aiResponse);
        
        try {
            // 清理AI响应，移除可能的markdown代码块标记
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
            
            log.info("清理后的响应内容: {}", cleanedResponse);
            
            // 尝试解析AI返回的JSON
            List<Map<String, Object>> questions = objectMapper.readValue(cleanedResponse, new TypeReference<List<Map<String, Object>>>() {});
            log.info("成功解析 {} 道题目", questions.size());
            return questions;
        } catch (JsonProcessingException e) {
            log.error("AI返回格式解析失败: {}", e.getMessage());
            log.error("原始响应: {}", aiResponse);
            // 返回备用题目
            return generateFallbackQuestions();
        }
    }

    private List<Map<String, Object>> generateFallbackQuestions() {
        List<Map<String, Object>> questions = new ArrayList<>();
        
        Map<String, Object> question1 = new HashMap<>();
        question1.put("title", "Linux系统中，使用哪个命令可以显示当前工作目录？");
        question1.put("type", "choice");
        question1.put("options", Arrays.asList("A. pwd", "B. cd", "C. ls", "D. mkdir"));
        question1.put("answer", "A");
        question1.put("explanation", "pwd命令用于显示当前工作目录的完整路径");
        questions.add(question1);
        
        Map<String, Object> question2 = new HashMap<>();
        question2.put("title", "在数据结构中，哪种结构遵循\"后进先出\"的原则？");
        question2.put("type", "choice");
        question2.put("options", Arrays.asList("A. 栈", "B. 队列", "C. 链表", "D. 数组"));
        question2.put("answer", "A");
        question2.put("explanation", "栈是一种后进先出(LIFO)的数据结构");
        questions.add(question2);
        
        return questions;
    }

    private ErrorQuestionTrainingDTO generateFallbackTraining(Long studentId, Long originalQuestionId, Integer questionCount, String trainingType) {
        ErrorQuestionTrainingDTO training = new ErrorQuestionTrainingDTO();
        training.setStudentId(studentId);
        training.setTrainingType(trainingType);
        training.setOriginalQuestionId(originalQuestionId);
        training.setQuestionCount(questionCount);
        training.setQuestions(generateFallbackQuestions());
        training.setAnalysisReport("由于AI服务暂时不可用，系统生成了备用练习题目。");
        training.setTargetErrorTypes(Arrays.asList("概念理解", "基础知识"));
        training.setExpectedImprovement(60.0);
        training.setCreateTime(LocalDateTime.now());
        
        return training;
    }

    private String generateAnalysisReport(ErrorQuestionAnalysisDTO originalError, String trainingType) {
        StringBuilder report = new StringBuilder();
        report.append("**错题分析报告**\n\n");
        report.append("原题知识点：").append(originalError.getKnowledgePoint()).append("\n");
        report.append("错误类型：").append(originalError.getErrorType()).append("\n");
        report.append("错误原因：").append(originalError.getErrorReason()).append("\n");
        report.append("历史错误次数：").append(originalError.getErrorCount()).append("\n");
        report.append("错误率：").append(originalError.getErrorRate()).append("%\n\n");
        report.append("**训练目标**\n");
        report.append("通过相似题目的练习，帮助您：\n");
        report.append("1. 巩固").append(originalError.getKnowledgePoint()).append("的核心概念\n");
        report.append("2. 纠正").append(originalError.getErrorType()).append("类型的错误\n");
        report.append("3. 提高该知识点的掌握程度\n");
        
        return report.toString();
    }

    private String generateKnowledgePointReport(String knowledgePoint, List<ErrorQuestionAnalysisDTO> relatedErrors) {
        StringBuilder report = new StringBuilder();
        report.append("**知识点专项训练报告**\n\n");
        report.append("目标知识点：").append(knowledgePoint).append("\n");
        report.append("相关错题数量：").append(relatedErrors.size()).append("\n\n");
        
        if (!relatedErrors.isEmpty()) {
            report.append("**主要问题分析**\n");
            Map<String, Long> errorTypeCount = relatedErrors.stream()
                .collect(Collectors.groupingBy(ErrorQuestionAnalysisDTO::getErrorType, Collectors.counting()));
            
            for (Map.Entry<String, Long> entry : errorTypeCount.entrySet()) {
                report.append("- ").append(entry.getKey()).append("：").append(entry.getValue()).append("次\n");
            }
        }
        
        report.append("\n**训练目标**\n");
        report.append("1. 系统掌握").append(knowledgePoint).append("的核心概念\n");
        report.append("2. 解决相关的理解和应用问题\n");
        report.append("3. 提高该知识点的整体掌握水平\n");
        
        return report.toString();
    }

    private String generateComprehensiveReport(List<ErrorQuestionAnalysisDTO> allErrors, List<String> priorityKnowledgePoints) {
        StringBuilder report = new StringBuilder();
        report.append("**综合错题训练报告**\n\n");
        report.append("错题总数：").append(allErrors.size()).append("\n");
        report.append("重点攻克知识点：").append(String.join("、", priorityKnowledgePoints)).append("\n\n");
        
        report.append("**薄弱环节分析**\n");
        for (String kp : priorityKnowledgePoints) {
            long count = allErrors.stream()
                .filter(error -> kp.equals(error.getKnowledgePoint()))
                .count();
            double avgErrorRate = allErrors.stream()
                .filter(error -> kp.equals(error.getKnowledgePoint()))
                .mapToDouble(ErrorQuestionAnalysisDTO::getErrorRate)
                .average()
                .orElse(0.0);
            report.append("- ").append(kp).append("：").append(count).append("道错题，平均错误率").append(String.format("%.1f", avgErrorRate)).append("%\n");
        }
        
        report.append("\n**训练目标**\n");
        report.append("1. 全面提升薄弱知识点的掌握程度\n");
        report.append("2. 减少常见错误类型的发生\n");
        report.append("3. 建立知识点之间的关联和理解\n");
        
        return report.toString();
    }

    private String determineErrorType(String userAnswer, String correctAnswer) {
        // 简单的错误类型判断逻辑
        if (userAnswer == null || userAnswer.trim().isEmpty()) {
            return "未作答";
        } else if (!userAnswer.equals(correctAnswer)) {
            return "答案错误";
        }
        return "正确";
    }

    private List<String> generateImprovementSuggestions(double accuracy, List<Map<String, Object>> detailedResults) {
        List<String> suggestions = new ArrayList<>();
        
        if (accuracy >= 80) {
            suggestions.add("表现优秀！继续保持这种学习状态");
            suggestions.add("可以尝试更高难度的题目来进一步提升");
        } else if (accuracy >= 60) {
            suggestions.add("基础掌握较好，需要加强细节理解");
            suggestions.add("建议多做相关练习，巩固薄弱知识点");
        } else {
            suggestions.add("需要系统复习相关知识点");
            suggestions.add("建议从基础概念开始重新学习");
            suggestions.add("可以寻求老师或同学的帮助");
        }
        
        return suggestions;
    }

    private List<String> generateNextSteps(double accuracy) {
        List<String> nextSteps = new ArrayList<>();
        
        if (accuracy >= 80) {
            nextSteps.add("进入下一个知识点的学习");
            nextSteps.add("尝试综合性的应用题目");
        } else if (accuracy >= 60) {
            nextSteps.add("重复练习相似题目");
            nextSteps.add("复习相关理论知识");
        } else {
            nextSteps.add("回到基础知识的学习");
            nextSteps.add("寻求额外的学习资源");
            nextSteps.add("与老师讨论学习方法");
        }
        
        return nextSteps;
    }
    
    @Override
    public boolean recordErrorQuestion(Long studentId, Map<String, Object> errorQuestion) {
        try {
            log.info("记录单个错题，学生ID: {}, 题目: {}", studentId, errorQuestion.get("questionContent"));
            
            ErrorQuestionRecord record = new ErrorQuestionRecord();
            record.setStudentId(studentId);
            record.setQuestionId(errorQuestion.get("questionId").toString());
            record.setQuestionType((String) errorQuestion.get("questionType"));
            record.setQuestionContent((String) errorQuestion.get("questionContent"));
            
            // 处理选项（如果有）
            if (errorQuestion.containsKey("options") && errorQuestion.get("options") != null) {
                try {
                    record.setQuestionOptions(objectMapper.writeValueAsString(errorQuestion.get("options")));
                } catch (JsonProcessingException e) {
                    log.warn("选项序列化失败", e);
                    record.setQuestionOptions(null);
                }
            }
            
            record.setUserAnswer((String) errorQuestion.get("userAnswer"));
            record.setCorrectAnswer((String) errorQuestion.get("correctAnswer"));
            record.setKnowledgePoint((String) errorQuestion.getOrDefault("knowledgePoint", "未分类"));
            record.setErrorType((String) errorQuestion.getOrDefault("errorType", "答案错误"));
            record.setErrorReason((String) errorQuestion.getOrDefault("errorReason", ""));
            record.setSource((String) errorQuestion.getOrDefault("source", "practice"));
            record.setErrorCount(1);
            record.setErrorRate(100.0);
            record.setCreateTime(LocalDateTime.now());
            record.setUpdateTime(LocalDateTime.now());
            
            int result = errorQuestionRecordMapper.insert(record);
            return result > 0;
            
        } catch (Exception e) {
            log.error("记录错题失败", e);
            return false;
        }
    }
    
    @Override
    public int batchRecordErrorQuestions(Long studentId, List<Map<String, Object>> errorQuestions, String source) {
        try {
            log.info("批量记录错题，学生ID: {}, 数量: {}, 来源: {}", studentId, errorQuestions.size(), source);
            
            List<ErrorQuestionRecord> records = new ArrayList<>();
            
            for (Map<String, Object> errorQuestion : errorQuestions) {
                ErrorQuestionRecord record = new ErrorQuestionRecord();
                record.setStudentId(studentId);
                record.setQuestionId(errorQuestion.get("questionId").toString());
                record.setQuestionType((String) errorQuestion.get("questionType"));
                record.setQuestionContent((String) errorQuestion.get("questionContent"));
                
                // 处理选项（如果有）
                if (errorQuestion.containsKey("options") && errorQuestion.get("options") != null) {
                    try {
                        record.setQuestionOptions(objectMapper.writeValueAsString(errorQuestion.get("options")));
                    } catch (JsonProcessingException e) {
                        log.warn("选项序列化失败", e);
                        record.setQuestionOptions(null);
                    }
                }
                
                record.setUserAnswer((String) errorQuestion.get("userAnswer"));
                record.setCorrectAnswer((String) errorQuestion.get("correctAnswer"));
                record.setKnowledgePoint((String) errorQuestion.getOrDefault("knowledgePoint", "未分类"));
                record.setErrorType((String) errorQuestion.getOrDefault("errorType", "答案错误"));
                record.setErrorReason((String) errorQuestion.getOrDefault("errorReason", ""));
                record.setSource(source);
                record.setErrorCount(1);
                record.setErrorRate(((Number) errorQuestion.getOrDefault("errorRate", 100)).doubleValue());
                record.setCreateTime(LocalDateTime.now());
                record.setUpdateTime(LocalDateTime.now());
                
                records.add(record);
            }
            
            if (!records.isEmpty()) {
                int result = errorQuestionRecordMapper.batchInsert(records);
                log.info("成功批量记录 {} 道错题", result);
                return result;
            }
            
            return 0;
            
        } catch (Exception e) {
            log.error("批量记录错题失败", e);
            return 0;
        }
    }
} 