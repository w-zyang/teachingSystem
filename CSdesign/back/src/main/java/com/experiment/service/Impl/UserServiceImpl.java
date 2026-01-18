package com.experiment.service.Impl;

import java.io.IOException;
import java.time.LocalDateTime;
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
     *
     * @param loginDTO
     * @return
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
     *
     * @param registerDTO
     * @return
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
     *
     * @param changePwdDTO
     * @return
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
     * @param file
     * @return
     */
    public Result<String> upload(MultipartFile file) {
        try {
            //原始文件名
            String originalFilename = file.getOriginalFilename();
            //截取原始文件名的后缀——".png"
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            //构建新文件名称
            String objectName = UUID.randomUUID() + extension;
            //文件的请求路径
            String filePath = aliOssUtil.upload(file.getBytes(), objectName);
            return Result.success("头像上传成功！", filePath);
        } catch (IOException e) {
            log.error("头像上传失败：{}", e);
        }
        return Result.error("头像上传失败！");
    }

    /**
     * 用户信息查询
     * @param queryDTO
     * @return
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
     * 获取用户信息
     * @param id
     * @return
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

        return Result.success("获取用户成功", user);
    }

    /**
     * 添加用户
     * @param user
     * @return
     */
    @Override
    public Result<String> addUser(User user) {
        // 参数校验
        if (user.getUsername() == null || user.getPassword() == null || user.getRole() == null) {
            return Result.error("必填字段不能为空");
        }

        // 检查用户名是否已存在
        User existingUser = userMapper.findByUsernameAndRole(user.getUsername(), user.getRole());
        if (existingUser != null) {
            return Result.error("用户名已存在");
        }

        // 加密密码
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
        user.setStatus("active");
        user.setRegisterDate(LocalDateTime.now());
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        userMapper.insert(user);
        return Result.success("用户添加成功");
    }

    /**
     * 更新用户
     * @param user
     * @return
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

        // 如果密码不为空，则加密密码
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
        }

        user.setUpdateTime(LocalDateTime.now());
        userMapper.update(user);
        return Result.success("用户更新成功");
    }

    /**
     * 删除用户
     * @param id
     * @return
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

        userMapper.deleteById(id);
        return Result.success("用户删除成功");
    }

    /**
     * 更新用户状态
     * @param id
     * @param status
     * @return
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

        userMapper.updateStatus(id, status);
        return Result.success("用户状态更新成功");
    }

    /**
     * 获取用户统计
     * @return
     */
    @Override
    public Result<Object> getUserStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalTeachers", userMapper.countByRole("teacher"));
        stats.put("totalStudents", userMapper.countByRole("student"));
        stats.put("totalAdmins", userMapper.countByRole("admin"));
        stats.put("activeUsers", userMapper.countByStatus("active"));
        return Result.success("获取统计信息成功", stats);
    }

    /**
     * 批量删除用户
     * @param ids
     * @return
     */
    @Override
    public Result<String> batchDeleteUsers(Long[] ids) {
        if (ids == null || ids.length == 0) {
            return Result.error("请选择要删除的用户");
        }

        for (Long id : ids) {
            userMapper.deleteById(id);
        }

        return Result.success("批量删除成功");
    }

    /**
     * 获取用户信息
     * @param username
     * @return
     */
    @Override
    public Result getUserByUsername(String username) {
        // 保持原有方法兼容性
        return Result.success("获取用户信息成功");
    }
}
