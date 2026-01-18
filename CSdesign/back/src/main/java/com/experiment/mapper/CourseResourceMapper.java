package com.experiment.mapper;

import com.experiment.pojo.CourseResource;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CourseResourceMapper {
    
    /**
     * 根据课程ID获取资源列表
     */
    List<CourseResource> findByCourseId(@Param("courseId") Long courseId);
    
    /**
     * 根据教师ID获取资源列表
     */
    List<CourseResource> findByTeacherId(@Param("teacherId") Long teacherId);
    
    /**
     * 根据ID获取资源详情
     */
    CourseResource findById(@Param("id") Long id);
    
    /**
     * 插入新资源
     */
    int insert(CourseResource resource);
    
    /**
     * 更新资源信息
     */
    int update(CourseResource resource);
    
    /**
     * 删除资源
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 更新下载次数
     */
    int updateDownloadCount(@Param("id") Long id);
    
    /**
     * 获取所有资源（管理员用）
     */
    List<CourseResource> findAll();
    
    /**
     * 根据文件类型获取资源
     */
    List<CourseResource> findByFileType(@Param("fileType") String fileType);
    
    /**
     * 搜索资源
     */
    List<CourseResource> searchResources(@Param("keyword") String keyword,
                                       @Param("courseId") Long courseId,
                                       @Param("fileType") String fileType);
} 