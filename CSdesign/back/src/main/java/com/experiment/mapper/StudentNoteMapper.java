package com.experiment.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.experiment.pojo.StudentNote;

@Mapper
public interface StudentNoteMapper {
    
    /**
     * 根据学生ID和资源ID获取笔记列表
     */
    List<StudentNote> findByStudentIdAndResourceId(@Param("studentId") Long studentId, 
                                                  @Param("resourceId") Long resourceId);
    
    /**
     * 根据学生ID获取所有笔记
     */
    List<StudentNote> findByStudentId(@Param("studentId") Long studentId);
    
    /**
     * 根据资源ID获取公开笔记
     */
    List<StudentNote> findPublicNotesByResourceId(@Param("resourceId") Long resourceId);
    
    /**
     * 根据ID获取笔记详情
     */
    StudentNote findById(@Param("id") Long id);
    
    /**
     * 插入新笔记
     */
    int insert(StudentNote note);
    
    /**
     * 更新笔记
     */
    int update(StudentNote note);
    
    /**
     * 删除笔记
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 根据标签搜索笔记
     */
    List<StudentNote> findByTags(@Param("studentId") Long studentId, 
                                @Param("tags") String tags);
    
    /**
     * 统计学生笔记数量
     */
    int countByStudentId(@Param("studentId") Long studentId);
    
    /**
     * 搜索笔记
     */
    List<StudentNote> searchNotes(@Param("studentId") Long studentId,
                                 @Param("keyword") String keyword,
                                 @Param("resourceId") Long resourceId);
} 