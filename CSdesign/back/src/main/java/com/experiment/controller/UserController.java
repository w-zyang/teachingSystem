package com.experiment.controller;

import com.experiment.pojo.User;
import com.experiment.pojo.UserChangePwdDTO;
import com.experiment.pojo.UserLoginDTO;
import com.experiment.pojo.UserRegisterDTO;
import com.experiment.pojo.UserQueryDTO;
import com.experiment.result.PageResult;
import com.experiment.result.Result;
import com.experiment.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody UserLoginDTO loginDTO) {
        log.info("用户登录：{}", loginDTO);
        return userService.login(loginDTO);
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<User> register(@RequestBody UserRegisterDTO registerDTO) {
        log.info("用户注册：{}", registerDTO);
        return userService.register(registerDTO);
    }

    /**
     * 修改密码
     */
    @PostMapping("/change-password")
    public Result<String> changePassword(@RequestBody UserChangePwdDTO changePwdDTO) {
        log.info("用户修改密码：{}", changePwdDTO);
        return userService.changePassword(changePwdDTO);
    }

    /**
     * 分页查询用户列表
     */
    @GetMapping("/list")
    public Result<PageResult<User>> getUserList(UserQueryDTO queryDTO) {
        log.info("查询用户列表：{}", queryDTO);
        PageResult<User> pageResult = userService.getUserList(queryDTO);
        return Result.success("查询成功", pageResult);
    }

    /**
     * 根据ID查询用户
     */
    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable Long id) {
        log.info("查询用户：{}", id);
        return userService.getUserById(id);
    }

    /**
     * 添加用户
     */
    @PostMapping("/add")
    public Result<String> addUser(@RequestBody User user) {
        log.info("添加用户：{}", user);
        return userService.addUser(user);
    }

    /**
     * 更新用户
     */
    @PutMapping("/update")
    public Result<String> updateUser(@RequestBody User user) {
        log.info("更新用户：{}", user);
        return userService.updateUser(user);
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteUser(@PathVariable Long id) {
        log.info("删除用户：{}", id);
        return userService.deleteUser(id);
    }

    /**
     * 更新用户状态
     */
    @PutMapping("/{id}/status")
    public Result<String> updateUserStatus(@PathVariable Long id, @RequestParam String status) {
        log.info("更新用户状态：{} - {}", id, status);
        return userService.updateUserStatus(id, status);
    }

    /**
     * 获取用户统计信息
     */
    @GetMapping("/stats")
    public Result<Object> getUserStats() {
        log.info("获取用户统计信息");
        return userService.getUserStats();
    }

    /**
     * 批量删除用户
     */
    @DeleteMapping("/batch")
    public Result<String> batchDeleteUsers(@RequestBody Long[] ids) {
        log.info("批量删除用户：{}", ids);
        return userService.batchDeleteUsers(ids);
    }

    /**
     * 头像上传
     */
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) {
        log.info("用户头像上传：{}", file);
        return userService.upload(file);
    }

    /**
     * 用户信息查询
     */
    @GetMapping("/get-by-username")
    public Result getUserByUsername(String username) {
        log.info("用户信息回写：{}", username);
        return userService.getUserByUsername(username);
    }
} 