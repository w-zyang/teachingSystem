package com.experiment.controller;

import com.experiment.result.Result;
import com.experiment.service.PistonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 代码提交控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/code")
@CrossOrigin
public class CodeSubmissionController {
    
    @Autowired
    private PistonService pistonService;

    /**
     * 获取支持的编程语言
     */
    @GetMapping("/languages")
    public Result<String> getLanguages() {
        try {
            String languages = pistonService.getSupportedLanguages();
            return Result.success("获取语言列表成功", languages);
        } catch (Exception e) {
            log.error("获取语言列表失败", e);
            return Result.error("获取语言列表失败: " + e.getMessage());
        }
    }

    /**
     * 运行代码（单个测试用例）
     */
    @PostMapping("/run")
    public Result<Map<String, Object>> runCode(@RequestBody Map<String, Object> request) {
        try {
            // 获取参数
            String sourceCode = (String) request.get("sourceCode");
            Object languageIdObj = request.get("languageId");
            String stdin = (String) request.getOrDefault("stdin", "");

            // 参数验证
            if (sourceCode == null || sourceCode.trim().isEmpty()) {
                log.warn("源代码为空");
                return Result.error("源代码不能为空");
            }

            if (languageIdObj == null) {
                log.warn("语言ID为空");
                return Result.error("语言ID不能为空");
            }

            // 转换语言ID
            Integer languageId;
            if (languageIdObj instanceof String) {
                try {
                    languageId = Integer.parseInt((String) languageIdObj);
                } catch (NumberFormatException e) {
                    log.warn("语言ID格式错误: {}", languageIdObj);
                    return Result.error("语言ID格式错误");
                }
            } else if (languageIdObj instanceof Integer) {
                languageId = (Integer) languageIdObj;
            } else {
                log.warn("语言ID类型错误: {}", languageIdObj.getClass());
                return Result.error("语言ID类型错误");
            }

            log.info("运行代码，语言ID: {}, 代码长度: {}, 输入长度: {}", 
                languageId, sourceCode.length(), stdin.length());
            log.info("源代码内容: {}", sourceCode);

            // 使用 Piston 执行
            log.info("使用 Piston 引擎执行代码");
            Map<String, Object> result = pistonService.executeCode(sourceCode, languageId, stdin);
            log.info("Piston 执行完成，状态: {}", result.get("status"));

            return Result.success("运行完成", result);

        } catch (IllegalArgumentException e) {
            log.error("参数错误: {}", e.getMessage());
            return Result.error("参数错误: " + e.getMessage());
        } catch (Exception e) {
            log.error("运行代码失败", e);
            return Result.error("运行失败: " + e.getMessage());
        }
    }

    /**
     * 测试连接
     */
    @GetMapping("/test-connection")
    public Result<String> testConnection() {
        try {
            // 测试 Piston 连接
            String languages = pistonService.getSupportedLanguages();
            return Result.success("Piston 连接正常", "当前使用 Piston 引擎，支持 50+ 种编程语言");
        } catch (Exception e) {
            log.error("连接测试失败", e);
            return Result.error("连接失败: " + e.getMessage());
        }
    }
    
    /**
     * 批量执行测试用例（使用 Piston）
     */
    @PostMapping("/run-batch")
    public Result<List<Map<String, Object>>> runBatch(@RequestBody Map<String, Object> request) {
        try {
            String sourceCode = (String) request.get("sourceCode");
            Integer languageId = (Integer) request.get("languageId");
            
            // 修复：正确处理 ArrayList 类型的 testCases
            @SuppressWarnings("unchecked")
            List<Map<String, String>> testCasesList = (List<Map<String, String>>) request.get("testCases");
            
            if (testCasesList == null || testCasesList.isEmpty()) {
                return Result.error("测试用例不能为空");
            }
            
            // 转换为数组
            @SuppressWarnings("unchecked")
            Map<String, String>[] testCases = testCasesList.toArray(new Map[0]);

            log.info("批量执行代码，语言ID: {}, 测试用例数: {}", languageId, testCases.length);

            // 使用 Piston 批量执行
            List<Map<String, Object>> results = pistonService.executeBatch(sourceCode, languageId, testCases);

            return Result.success("批量执行完成", results);

        } catch (Exception e) {
            log.error("批量执行失败", e);
            return Result.error("批量执行失败: " + e.getMessage());
        }
    }
}
