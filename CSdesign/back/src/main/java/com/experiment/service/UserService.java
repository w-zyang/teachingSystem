package com.experiment.service;

import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.experiment.pojo.User;
import com.experiment.pojo.UserChangePwdDTO;
import com.experiment.pojo.UserLoginDTO;
import com.experiment.pojo.UserQueryDTO;
import com.experiment.pojo.UserRegisterDTO;
import com.experiment.result.PageResult;
import com.experiment.result.Result;

public interface UserService {

    /**
     * 登录
     * @param userLogin
     * @return
     */
    Result<Map<String, Object>> login(UserLoginDTO userLogin);

    /**
     * 注册
     * @param userRegister
     * @return
     */
    Result<User> register(UserRegisterDTO userRegister);

    /**
     * 修改密码
     * @param userChangePwd
     * @return
     */
    Result<String> changePassword(UserChangePwdDTO userChangePwd);

    /**
     * 头像上传
     * @param file
     * @return
     */
    Result<String> upload(MultipartFile file);

    /**
     * 用户信息查询
     * @return
     */
    Result getUserByUsername(String username);

    /**
     * 分页查询用户列表
     * @param queryDTO
     * @return
     */
    PageResult<User> getUserList(UserQueryDTO queryDTO);

    /**
     * 根据ID查询用户
     * @param id
     * @return
     */
    Result<User> getUserById(Long id);

    /**
     * 添加用户
     * @param user
     * @return
     */
    Result<String> addUser(User user);

    /**
     * 更新用户
     * @param user
     * @return
     */
    Result<String> updateUser(User user);

    /**
     * 删除用户
     * @param id
     * @return
     */
    Result<String> deleteUser(Long id);

    /**
     * 更新用户状态
     * @param id
     * @param status
     * @return
     */
    Result<String> updateUserStatus(Long id, String status);

    /**
     * 获取用户统计信息
     * @return
     */
    Result<Object> getUserStats();

    /**
     * 批量删除用户
     * @param ids
     * @return
     */
    Result<String> batchDeleteUsers(Long[] ids);
}
