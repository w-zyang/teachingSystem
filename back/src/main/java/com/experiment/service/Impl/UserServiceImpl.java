package com.experiment.service.Impl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import com.experiment.mapper.UserMapper;
import com.experiment.pojo.User;
import com.experiment.pojo.UserChangePwdDTO;
import com.experiment.pojo.UserLoginDTO;
import com.experiment.pojo.UserQueryDTO;
import com.experiment.pojo.UserRegisterDTO;
import com.experiment.result.PageResult;
import com.experiment.result.Result;
import com.experiment.service.UserService;
import com.experiment.utils.AliOssUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AliOssUtil aliOssUtil;

    /**
     * 登录
     */
    @Override
    public Result<Map<String, Object>> login(UserLoginDTO loginDTO) {
        // 参数校验
        if (loginDTO.getUsername() == null || loginDTO.getPassword() == null || loginDTO.getRole() == null) {
            return Result.error("用户名、密码和角色不能为空");
        }

        // 查询用户
        User user = userMapper.findByUsernameAndRole(loginDTO.getUsername(), loginDTO.getRole());
        if (user == null) {
            return Result.error("用户不存在或角色不匹配");
        }

        // 验证密码
        String encryptedPassword = DigestUtils.md5DigestAsHex(loginDTO.getPassword().getBytes());
        if (!user.getPassword().equals(encryptedPassword)) {
            return Result.error("密码错误");
        }

        // 检查用户状态
        if (!"active".equals(user.getStatus())) {
            return Result.error("用户账户已被停用");
        }

        // 更新最后登录时间
        userMapper.updateLastLogin(user.getId());
        user.setLastLogin(LocalDateTime.now());

        // 构造token和返回结构
        Map<String, Object> data = new HashMap<>();
        data.put("token", UUID.randomUUID().toString());
        data.put("user", user);
        return Result.success("登录成功", data);
    }

    /**
     * 注册
     */
    @Override
    public Result<User> register(UserRegisterDTO registerDTO) {
        // 参数校验
        if (registerDTO.getUsername() == null || registerDTO.getPassword() == null || 
            registerDTO.getEmail() == null || registerDTO.getRole() == null) {
            return Result.error("必填字段不能为空");
        }

        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            return Result.error("两次输入的密码不一致");
        }

        if (!registerDTO.getAgree()) {
            return Result.error("请先同意用户协议和隐私政策");
        }

        // 检查用户名是否已存在
        User existingUser = userMapper.findByUsernameAndRole(registerDTO.getUsername(), registerDTO.getRole());
        if (existingUser != null) {
            return Result.error("用户名已存在");
        }

        // 检查邮箱是否已存在
        User existingEmail = userMapper.findByEmail(registerDTO.getEmail());
        if (existingEmail != null) {
            return Result.error("邮箱已被注册");
        }

        // 创建新用户
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(DigestUtils.md5DigestAsHex(registerDTO.getPassword().getBytes()));
        user.setEmail(registerDTO.getEmail());
        user.setRole(registerDTO.getRole());
        user.setRealName(registerDTO.getRealName());
        user.setPhone(registerDTO.getPhone());
        user.setStatus("active");
        user.setRegisterDate(LocalDateTime.now());
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        // 根据角色设置特定字段
        if ("teacher".equals(registerDTO.getRole())) {
            user.setSubject(registerDTO.getSubject());
        } else if ("student".equals(registerDTO.getRole())) {
            user.setGrade(registerDTO.getGrade());
            user.setCollege(registerDTO.getCollege());
            user.setClassName(registerDTO.getClassName());
            user.setUserId(registerDTO.getStudentId());
        }

        // 插入用户
        userMapper.insert(user);
        return Result.success("注册成功", user);
    }

    /**
     * 修改密码
     */
    @Override
    public Result<String> changePassword(UserChangePwdDTO changePwdDTO) {
        // 参数校验
        if (changePwdDTO.getUsername() == null || changePwdDTO.getOldPassword() == null || 
            changePwdDTO.getNewPassword() == null) {
            return Result.error("参数不能为空");
        }

        // 查询用户
        User user = userMapper.findByUsernameAndRole(changePwdDTO.getUsername(), changePwdDTO.getRole());
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 验证旧密码
        String oldEncryptedPassword = DigestUtils.md5DigestAsHex(changePwdDTO.getOldPassword().getBytes());
        if (!user.getPassword().equals(oldEncryptedPassword)) {
            return Result.error("原密码错误");
        }

        // 更新密码
        String newEncryptedPassword = DigestUtils.md5DigestAsHex(changePwdDTO.getNewPassword().getBytes());
        user.setPassword(newEncryptedPassword);
        user.setUpdateTime(LocalDateTime.now());
        userMapper.update(user);

        return Result.success("密码修改成功");
    }

    /**
     * 头像上传
     */
    public Result<String> upload(MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String objectName = UUID.randomUUID() + extension;
            String filePath = aliOssUtil.upload(file.getBytes(), objectName);
            return Result.success("头像上传成功！", filePath);
        } catch (IOException e) {
            log.error("头像上传失败：{}", e);
        }
        return Result.error("头像上传失败！");
    }

    /**
     * 分页查询用户列表
     */
    @Override
    public PageResult<User> getUserList(UserQueryDTO queryDTO) {
        // 设置默认分页参数
        if (queryDTO.getPage() == null || queryDTO.getPage() < 1) {
            queryDTO.setPage(1);
        }
        if (queryDTO.getSize() == null || queryDTO.getSize() < 1) {
            queryDTO.setSize(10);
        }

        // 计算偏移量
        int offset = (queryDTO.getPage() - 1) * queryDTO.getSize();
        queryDTO.setPage(offset);

        // 查询数据
        List<User> users = userMapper.findByCondition(queryDTO);
        int total = userMapper.countByCondition(queryDTO);

        return new PageResult<>(total, users);
    }

    /**
     * 根据ID查询用户
     */
    @Override
    public Result<User> getUserById(Long id) {
        if (id == null) {
            return Result.error("用户ID不能为空");
        }

        User user = userMapper.findById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 不返回密码
        user.setPassword(null);
        return Result.success("获取用户成功", user);
    }

    /**
     * 添加用户
     */
    @Override
    public Result<String> addUser(User user) {
        // 参数校验
        if (user.getUsername() == null || user.getPassword() == null || user.getRole() == null) {
            return Result.error("必填字段不能为空");
        }

        try {
            // 检查用户名是否已存在
            User existingUser = userMapper.findByUsernameAndRole(user.getUsername(), user.getRole());
            if (existingUser != null) {
                return Result.error("用户名已存在");
            }

            // 检查邮箱是否已存在（如果提供了邮箱）
            if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                User existingEmail = userMapper.findByEmail(user.getEmail());
                if (existingEmail != null) {
                    return Result.error("邮箱已被注册");
                }
            }

            // 加密密码
            user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
            user.setStatus("active");
            user.setRegisterDate(LocalDateTime.now());
            user.setCreateTime(LocalDateTime.now());
            user.setUpdateTime(LocalDateTime.now());

            int result = userMapper.insert(user);
            
            if (result > 0) {
                return Result.success("用户添加成功");
            } else {
                return Result.error("用户添加失败");
            }
        } catch (Exception e) {
            log.error("添加用户失败：{}", e.getMessage());
            return Result.error("添加用户失败：" + e.getMessage());
        }
    }

    /**
     * 更新用户
     */
    @Override
    public Result<String> updateUser(User user) {
        if (user.getId() == null) {
            return Result.error("用户ID不能为空");
        }

        User existingUser = userMapper.findById(user.getId());
        if (existingUser == null) {
            return Result.error("用户不存在");
        }

        try {
            // 如果密码不为空，则加密密码
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
            } else {
                // 不修改密码时保持原密码
                user.setPassword(existingUser.getPassword());
            }

            user.setUpdateTime(LocalDateTime.now());
            int result = userMapper.update(user);
            
            if (result > 0) {
                return Result.success("用户更新成功");
            } else {
                return Result.error("用户更新失败，未找到要更新的记录");
            }
        } catch (Exception e) {
            log.error("更新用户失败：{}", e.getMessage());
            return Result.error("更新用户失败：" + e.getMessage());
        }
    }

    /**
     * 删除用户
     */
    @Override
    public Result<String> deleteUser(Long id) {
        if (id == null) {
            return Result.error("用户ID不能为空");
        }

        User user = userMapper.findById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }

        try {
            userMapper.deleteById(id);
            return Result.success("用户删除成功");
        } catch (Exception e) {
            log.error("删除用户失败：{}", e.getMessage());
            
            // 检查是否是外键约束错误
            if (e.getMessage() != null && e.getMessage().contains("foreign key constraint")) {
                if (e.getMessage().contains("course")) {
                    return Result.error("无法删除该用户，该教师还有关联的课程。请先删除或转移相关课程后再试。");
                } else if (e.getMessage().contains("student_exam")) {
                    return Result.error("无法删除该用户，该学生还有考试记录。请先删除相关考试记录后再试。");
                } else {
                    return Result.error("无法删除该用户，该用户还有关联的数据。请先删除相关数据后再试。");
                }
            }
            
            return Result.error("删除用户失败：" + e.getMessage());
        }
    }

    /**
     * 更新用户状态
     */
    @Override
    public Result<String> updateUserStatus(Long id, String status) {
        if (id == null || status == null) {
            return Result.error("参数不能为空");
        }

        User user = userMapper.findById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }

        try {
            int result = userMapper.updateStatus(id, status);
            
            if (result > 0) {
                return Result.success("用户状态更新成功");
            } else {
                return Result.error("用户状态更新失败，未找到要更新的记录");
            }
        } catch (Exception e) {
            log.error("更新用户状态失败：{}", e.getMessage());
            return Result.error("更新用户状态失败：" + e.getMessage());
        }
    }

    /**
     * 获取用户统计
     */
    @Override
    public Result<Object> getUserStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            Integer totalTeachers = userMapper.countByRole("teacher");
            Integer totalStudents = userMapper.countByRole("student");
            Integer totalAdmins = userMapper.countByRole("admin");
            Integer activeUsers = userMapper.countByStatus("active");
            
            stats.put("totalTeachers", totalTeachers != null ? totalTeachers : 0);
            stats.put("totalStudents", totalStudents != null ? totalStudents : 0);
            stats.put("totalAdmins", totalAdmins != null ? totalAdmins : 0);
            stats.put("activeUsers", activeUsers != null ? activeUsers : 0);
            stats.put("totalUsers", (totalTeachers != null ? totalTeachers : 0) + 
                                   (totalStudents != null ? totalStudents : 0) + 
                                   (totalAdmins != null ? totalAdmins : 0));
            
            return Result.success("获取统计信息成功", stats);
        } catch (Exception e) {
            log.error("获取用户统计失败：{}", e.getMessage());
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalTeachers", 0);
            stats.put("totalStudents", 0);
            stats.put("totalAdmins", 0);
            stats.put("activeUsers", 0);
            stats.put("totalUsers", 0);
            return Result.success("获取统计信息成功", stats);
        }
    }

    /**
     * 批量删除用户
     */
    @Override
    public Result<String> batchDeleteUsers(Long[] ids) {
        if (ids == null || ids.length == 0) {
            return Result.error("请选择要删除的用户");
        }

        int successCount = 0;
        int failCount = 0;
        StringBuilder errorMsg = new StringBuilder();

        for (Long id : ids) {
            try {
                User user = userMapper.findById(id);
                if (user == null) {
                    failCount++;
                    errorMsg.append("用户ID ").append(id).append(" 不存在；");
                    continue;
                }
                
                userMapper.deleteById(id);
                successCount++;
            } catch (Exception e) {
                failCount++;
                log.error("删除用户 {} 失败：{}", id, e.getMessage());
                
                // 检查是否是外键约束错误
                if (e.getMessage() != null && e.getMessage().contains("foreign key constraint")) {
                    errorMsg.append("用户ID ").append(id).append(" 有关联数据无法删除；");
                } else {
                    errorMsg.append("用户ID ").append(id).append(" 删除失败；");
                }
            }
        }

        if (failCount == 0) {
            return Result.success("批量删除成功，共删除 " + successCount + " 个用户");
        } else if (successCount == 0) {
            return Result.error("批量删除失败：" + errorMsg.toString());
        } else {
            return Result.success("部分删除成功：成功 " + successCount + " 个，失败 " + failCount + " 个。" + errorMsg.toString());
        }
    }

    /**
     * 根据用户名查询用户
     */
    @Override
    public Result getUserByUsername(String username) {
        try {
            // 查询所有角色的用户
            List<User> users = new ArrayList<>();
            String[] roles = {"teacher", "student", "admin"};
            
            for (String role : roles) {
                User user = userMapper.findByUsernameAndRole(username, role);
                if (user != null) {
                    user.setPassword(null);
                    return Result.success("查询成功", user);
                }
            }
            
            return Result.error("用户不存在");
        } catch (Exception e) {
            log.error("查询用户失败：{}", e.getMessage());
            return Result.error("查询用户失败");
        }
    }
}
