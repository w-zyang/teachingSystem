package com.experiment.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.experiment.result.Result;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/test")
@CrossOrigin
@Slf4j
public class TestController {

    /**
     * 测试接口
     */
    @GetMapping("/hello")
    public Result<String> hello() {
        log.info("测试接口被调用");
        return Result.success("Hello, 智能教学管理系统后端启动成功！");
    }

    /**
     * 获取系统信息
     */
    @GetMapping("/info")
    public Result<Object> getSystemInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", "智能教学管理系统");
        info.put("version", "1.0.0");
        info.put("description", "面向大学生的智能教学管理平台");
        info.put("status", "running");
        return Result.success("获取系统信息成功", info);
    }

    /**
     * 模拟用户登录测试
     */
    @PostMapping("/mock-login")
    public Result<Object> mockLogin(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");
        String role = loginData.get("role");

        log.info("模拟登录：用户名={}, 角色={}", username, role);

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", 1L);
        userInfo.put("username", username);
        userInfo.put("role", role);
        userInfo.put("realName", "测试用户");
        userInfo.put("token", "mock-token-" + System.currentTimeMillis());

        return Result.success("模拟登录成功", userInfo);
    }
} 