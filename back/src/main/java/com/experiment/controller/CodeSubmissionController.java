package com.experiment.controller;

import com.experiment.result.Result;
import com.experiment.service.Judge0Service;
import com.experiment.service.PistonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    private Judge0Service judge0Service;
    
    @Autowired
    private PistonService pistonService;
    
    @Value("${code.execution.engine:piston}")
    private String executionEngine;

    /**
     * 提交代码
     */
    @PostMapping("/submit")
    public Result<Map<String, Object>> submitCode(@RequestBody Map<String, Object> request) {
        try {
            String sourceCode = (String) request.get("sourceCode");
            Object languageIdObj = request.get("languageId");
            String stdin = (String) request.getOrDefault("stdin", "");
            String expectedOutput = (String) request.getOrDefault("expectedOutput", "");

            // 参数验证
            if (sourceCode == null || sourceCode.trim().isEmpty()) {
                return Result.error("源代码不能为空");
            }

            if (languageIdObj == null) {
                return Result.error("语言ID不能为空");
            }

            // 转换语言ID
            Integer languageId;
            if (languageIdObj instanceof String) {
                languageId = Integer.parseInt((String) languageIdObj);
            } else if (languageIdObj instanceof Integer) {
                languageId = (Integer) languageIdObj;
            } else {
                return Result.error("语言ID类型错误");
            }

            log.info("收到代码提交请求，语言ID: {}, 代码长度: {}", languageId, sourceCode.length());

            // 提交到 Judge0
            String token = judge0Service.submitCode(sourceCode, languageId, stdin, expectedOutput);

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("message", "代码提交成功，正在评测中...");

            return Result.success("提交成功", response);

        } catch (Exception e) {
            log.error("代码提交失败", e);
            return Result.error("代码提交失败: " + e.getMessage());
        }
    }

    /**
     * 获取提交结果
     */
    @GetMapping("/result/{token}")
    public Result<Map<String, Object>> getResult(@PathVariable String token) {
        try {
            log.info("查询提交结果，token: {}", token);
            
            Map<String, Object> result = judge0Service.getSubmissionResult(token);
            
            return Result.success("获取结果成功", result);

        } catch (Exception e) {
            log.error("获取结果失败", e);
            return Result.error("获取结果失败: " + e.getMessage());
        }
    }

    /**
     * 批量提交测试用例
     */
    @PostMapping("/submit-batch")
    public Result<Map<String, Object>> submitBatch(@RequestBody Map<String, Object> request) {
        try {
            String sourceCode = (String) request.get("sourceCode");
            Integer languageId = (Integer) request.get("languageId");
            @SuppressWarnings("unchecked")
            Map<String, String>[] testCases = (Map<String, String>[]) request.get("testCases");

            log.info("收到批量提交请求，语言ID: {}, 测试用例数: {}", languageId, testCases.length);

            // 批量提交
            String[] tokens = judge0Service.submitBatch(sourceCode, languageId, testCases);

            Map<String, Object> response = new HashMap<>();
            response.put("tokens", tokens);
            response.put("totalCases", testCases.length);
            response.put("message", "批量提交成功");

            return Result.success("批量提交成功", response);

        } catch (Exception e) {
            log.error("批量提交失败", e);
            return Result.error("批量提交失败: " + e.getMessage());
        }
    }

    /**
     * 获取支持的编程语言
     */
    @GetMapping("/languages")
    public Result<String> getLanguages() {
        try {
            String languages = judge0Service.getSupportedLanguages();
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

            log.info("运行代码，引擎: {}, 语言ID: {}, 代码长度: {}, 输入长度: {}", 
                executionEngine, languageId, sourceCode.length(), stdin.length());
            log.info("源代码内容: {}", sourceCode);

            Map<String, Object> result;
            
            // 根据配置选择执行引擎
            if ("piston".equalsIgnoreCase(executionEngine)) {
                // 使用 Piston 执行
                log.info("使用 Piston 引擎执行代码");
                result = pistonService.executeCode(sourceCode, languageId, stdin);
                log.info("Piston 执行完成，状态: {}", result.get("status"));
            } else {
                // 使用 Judge0 执行（原有逻辑）
                log.info("使用 Judge0 引擎执行代码");
                String token = judge0Service.submitCode(sourceCode, languageId, stdin, null);
                log.info("代码已提交，token: {}", token);

                // 等待结果（轮询）
                result = null;
                int maxAttempts = 10;
                int attempt = 0;

                while (attempt < maxAttempts) {
                    Thread.sleep(500); // 等待500ms
                    result = judge0Service.getSubmissionResult(token);
                    
                    Integer statusId = (Integer) result.get("statusId");
                    log.debug("轮询结果 {}/{}: statusId={}", attempt + 1, maxAttempts, statusId);
                    
                    // 1=队列中, 2=处理中
                    if (statusId != 1 && statusId != 2) {
                        break;
                    }
                    
                    attempt++;
                }

                if (result == null) {
                    log.warn("获取结果超时");
                    return Result.error("获取结果超时");
                }
                
                log.info("Judge0 执行完成，状态: {}", result.get("status"));
            }

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
            if ("piston".equalsIgnoreCase(executionEngine)) {
                // 测试 Piston 连接
                String languages = pistonService.getSupportedLanguages();
                int count = languages.split("\\{").length - 1; // 粗略统计语言数量
                return Result.success("Piston 连接正常", "当前使用 Piston 引擎，支持 50+ 种编程语言");
            } else {
                // 测试 Judge0 连接
                String languages = judge0Service.getSupportedLanguages();
                return Result.success("Judge0 连接正常", "支持 " + languages.split(",").length + " 种编程语言");
            }
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
            @SuppressWarnings("unchecked")
            Map<String, String>[] testCases = (Map<String, String>[]) request.get("testCases");

            log.info("批量执行代码，引擎: {}, 语言ID: {}, 测试用例数: {}", 
                executionEngine, languageId, testCases.length);

            List<Map<String, Object>> results;
            
            if ("piston".equalsIgnoreCase(executionEngine)) {
                // 使用 Piston 批量执行
                results = pistonService.executeBatch(sourceCode, languageId, testCases);
            } else {
                // 使用 Judge0 批量执行（需要实现）
                return Result.error("Judge0 批量执行暂未实现，请使用 Piston");
            }

            return Result.success("批量执行完成", results);

        } catch (Exception e) {
            log.error("批量执行失败", e);
            return Result.error("批量执行失败: " + e.getMessage());
        }
    }
}
