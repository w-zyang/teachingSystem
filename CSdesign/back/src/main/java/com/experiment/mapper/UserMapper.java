package com.experiment.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.experiment.pojo.User;
import com.experiment.pojo.UserQueryDTO;

@Mapper
public interface UserMapper {

    // 根据用户名和角色查询用户
    User findByUsernameAndRole(@Param("username") String username, @Param("role") String role);
    
    // 根据邮箱查询用户
    User findByEmail(@Param("email") String email);
    
    // 根据用户ID查询用户
    User findById(@Param("id") Long id);
    
    // 插入新用户
    int insert(User user);
    
    // 更新用户信息
    int update(User user);
    
    // 删除用户
    int deleteById(@Param("id") Long id);
    
    // 分页查询用户列表
    List<User> findByCondition(UserQueryDTO queryDTO);
    
    // 统计用户总数
    int countByCondition(UserQueryDTO queryDTO);
    
    // 根据角色统计用户数量
    int countByRole(@Param("role") String role);
    
    // 根据状态统计用户数量
    int countByStatus(@Param("status") String status);
    
    // 更新用户状态
    int updateStatus(@Param("id") Long id, @Param("status") String status);
    
    // 更新最后登录时间
    int updateLastLogin(@Param("id") Long id);
    
    // 根据学号或工号查询用户
    User findByUserId(@Param("userId") String userId);
} 