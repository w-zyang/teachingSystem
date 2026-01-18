package com.experiment.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.experiment.pojo.StudentAnnotation;

@Mapper
public interface StudentAnnotationMapper {
    
    /**
     * 根据学生ID和资源ID获取标注列表
     */
    List<StudentAnnotation> findByStudentIdAndResourceId(@Param("studentId") Long studentId, 
                                                        @Param("resourceId") Long resourceId);
    
    /**
     * 根据学生ID获取所有标注
     */
    List<StudentAnnotation> findByStudentId(@Param("studentId") Long studentId);
    
    /**
     * 根据资源ID和页码获取标注
     */
    List<StudentAnnotation> findByResourceIdAndPage(@Param("resourceId") Long resourceId,
                                                   @Param("pageNumber") Integer pageNumber);
    
    /**
     * 根据标注类型获取标注
     */
    List<StudentAnnotation> findByType(@Param("studentId") Long studentId,
                                      @Param("type") String type);
    
    /**
     * 根据ID获取标注详情
     */
    StudentAnnotation findById(@Param("id") Long id);
    
    /**
     * 插入新标注
     */
    int insert(StudentAnnotation annotation);
    
    /**
     * 更新标注
     */
    int update(StudentAnnotation annotation);
    
    /**
     * 删除标注
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 根据资源ID删除所有标注（级联删除）
     */
    int deleteByResourceId(@Param("resourceId") Long resourceId);
    
    /**
     * 根据重要程度获取标注
     */
    List<StudentAnnotation> findByImportance(@Param("studentId") Long studentId,
                                           @Param("importance") Integer importance);
    
    /**
     * 统计学生标注数量
     */
    int countByStudentId(@Param("studentId") Long studentId);
    
    /**
     * 搜索标注
     */
    List<StudentAnnotation> searchAnnotations(@Param("studentId") Long studentId,
                                            @Param("keyword") String keyword,
                                            @Param("type") String type,
                                            @Param("resourceId") Long resourceId);
} 