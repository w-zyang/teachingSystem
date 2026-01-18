package com.experiment.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.experiment.mapper.StudentAnnotationMapper;
import com.experiment.pojo.StudentAnnotation;
import com.experiment.result.Result;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/student-annotations")
@CrossOrigin
public class StudentAnnotationController {

    @Autowired
    private StudentAnnotationMapper studentAnnotationMapper;

    /**
     * 获取学生的所有标注
     */
    @GetMapping("/student/{studentId}")
    public Result<List<StudentAnnotation>> getStudentAnnotations(@PathVariable Long studentId) {
        try {
            List<StudentAnnotation> annotations = studentAnnotationMapper.findByStudentId(studentId);
            return Result.success("获取学生标注成功", annotations);
        } catch (Exception e) {
            log.error("获取学生标注失败", e);
            return Result.error("获取学生标注失败: " + e.getMessage());
        }
    }

    /**
     * 获取特定资源的学生标注
     */
    @GetMapping("/student/{studentId}/resource/{resourceId}")
    public Result<List<StudentAnnotation>> getResourceAnnotations(@PathVariable Long studentId, 
                                                                 @PathVariable Long resourceId) {
        try {
            List<StudentAnnotation> annotations = studentAnnotationMapper.findByStudentIdAndResourceId(studentId, resourceId);
            return Result.success("获取资源标注成功", annotations);
        } catch (Exception e) {
            log.error("获取资源标注失败", e);
            return Result.error("获取资源标注失败: " + e.getMessage());
        }
    }

    /**
     * 获取特定页面的标注
     */
    @GetMapping("/resource/{resourceId}/page/{pageNumber}")
    public Result<List<StudentAnnotation>> getPageAnnotations(@PathVariable Long resourceId,
                                                             @PathVariable Integer pageNumber) {
        try {
            List<StudentAnnotation> annotations = studentAnnotationMapper.findByResourceIdAndPage(resourceId, pageNumber);
            return Result.success("获取页面标注成功", annotations);
        } catch (Exception e) {
            log.error("获取页面标注失败", e);
            return Result.error("获取页面标注失败: " + e.getMessage());
        }
    }

    /**
     * 根据类型获取标注
     */
    @GetMapping("/student/{studentId}/type/{type}")
    public Result<List<StudentAnnotation>> getAnnotationsByType(@PathVariable Long studentId,
                                                               @PathVariable String type) {
        try {
            List<StudentAnnotation> annotations = studentAnnotationMapper.findByType(studentId, type);
            return Result.success("获取类型标注成功", annotations);
        } catch (Exception e) {
            log.error("获取类型标注失败", e);
            return Result.error("获取类型标注失败: " + e.getMessage());
        }
    }

    /**
     * 根据重要程度获取标注
     */
    @GetMapping("/student/{studentId}/importance/{importance}")
    public Result<List<StudentAnnotation>> getAnnotationsByImportance(@PathVariable Long studentId,
                                                                     @PathVariable Integer importance) {
        try {
            List<StudentAnnotation> annotations = studentAnnotationMapper.findByImportance(studentId, importance);
            return Result.success("获取重要标注成功", annotations);
        } catch (Exception e) {
            log.error("获取重要标注失败", e);
            return Result.error("获取重要标注失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID获取标注详情
     */
    @GetMapping("/{id}")
    public Result<StudentAnnotation> getAnnotationById(@PathVariable Long id) {
        try {
            StudentAnnotation annotation = studentAnnotationMapper.findById(id);
            if (annotation == null) {
                return Result.error("标注不存在");
            }
            return Result.success("获取标注详情成功", annotation);
        } catch (Exception e) {
            log.error("获取标注详情失败", e);
            return Result.error("获取标注详情失败: " + e.getMessage());
        }
    }

    /**
     * 创建新标注
     */
    @PostMapping
    public Result<StudentAnnotation> createAnnotation(@RequestBody StudentAnnotation annotation) {
        try {
            // 参数验证
            if (annotation.getStudentId() == null || annotation.getResourceId() == null) {
                return Result.error("学生ID和资源ID不能为空");
            }
            if (annotation.getType() == null || annotation.getType().trim().isEmpty()) {
                return Result.error("标注类型不能为空");
            }

            // 设置默认值
            if (annotation.getPageNumber() == null) {
                annotation.setPageNumber(1);
            }
            if (annotation.getColor() == null) {
                annotation.setColor("#FFFF00");
            }
            if (annotation.getImportance() == null) {
                annotation.setImportance(3);
            }

            // 验证标注类型
            String type = annotation.getType();
            if (!type.equals("highlight") && !type.equals("underline") && 
                !type.equals("comment") && !type.equals("sticky-note")) {
                return Result.error("标注类型无效，只支持: highlight, underline, comment, sticky-note");
            }

            // 验证重要程度
            if (annotation.getImportance() < 1 || annotation.getImportance() > 5) {
                return Result.error("重要程度必须在1-5之间");
            }

            int result = studentAnnotationMapper.insert(annotation);
            if (result > 0) {
                log.info("学生{}成功创建标注，类型: {}", annotation.getStudentId(), annotation.getType());
                return Result.success("创建标注成功", annotation);
            } else {
                return Result.error("创建标注失败");
            }
        } catch (Exception e) {
            log.error("创建标注失败", e);
            return Result.error("创建标注失败: " + e.getMessage());
        }
    }

    /**
     * 更新标注
     */
    @PutMapping("/{id}")
    public Result<String> updateAnnotation(@PathVariable Long id, @RequestBody StudentAnnotation annotation) {
        try {
            // 验证标注是否存在
            StudentAnnotation existingAnnotation = studentAnnotationMapper.findById(id);
            if (existingAnnotation == null) {
                return Result.error("标注不存在");
            }

            // 参数验证
            if (annotation.getType() != null) {
                String type = annotation.getType();
                if (!type.equals("highlight") && !type.equals("underline") && 
                    !type.equals("comment") && !type.equals("sticky-note")) {
                    return Result.error("标注类型无效，只支持: highlight, underline, comment, sticky-note");
                }
            }

            if (annotation.getImportance() != null) {
                if (annotation.getImportance() < 1 || annotation.getImportance() > 5) {
                    return Result.error("重要程度必须在1-5之间");
                }
            }

            annotation.setId(id);
            int result = studentAnnotationMapper.update(annotation);
            if (result > 0) {
                log.info("成功更新标注ID: {}", id);
                return Result.success("更新标注成功");
            } else {
                return Result.error("更新标注失败");
            }
        } catch (Exception e) {
            log.error("更新标注失败", e);
            return Result.error("更新标注失败: " + e.getMessage());
        }
    }

    /**
     * 删除标注
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteAnnotation(@PathVariable Long id) {
        try {
            // 验证标注是否存在
            StudentAnnotation existingAnnotation = studentAnnotationMapper.findById(id);
            if (existingAnnotation == null) {
                return Result.error("标注不存在");
            }

            int result = studentAnnotationMapper.deleteById(id);
            if (result > 0) {
                log.info("成功删除标注ID: {}", id);
                return Result.success("删除标注成功");
            } else {
                return Result.error("删除标注失败");
            }
        } catch (Exception e) {
            log.error("删除标注失败", e);
            return Result.error("删除标注失败: " + e.getMessage());
        }
    }

    /**
     * 搜索标注
     */
    @GetMapping("/search")
    public Result<List<StudentAnnotation>> searchAnnotations(@RequestParam Long studentId,
                                                           @RequestParam(required = false) String keyword,
                                                           @RequestParam(required = false) String type,
                                                           @RequestParam(required = false) Long resourceId) {
        try {
            List<StudentAnnotation> annotations = studentAnnotationMapper.searchAnnotations(studentId, keyword, type, resourceId);
            return Result.success("搜索标注成功", annotations);
        } catch (Exception e) {
            log.error("搜索标注失败", e);
            return Result.error("搜索标注失败: " + e.getMessage());
        }
    }

    /**
     * 获取学生标注统计
     */
    @GetMapping("/stats/{studentId}")
    public Result<Integer> getAnnotationStats(@PathVariable Long studentId) {
        try {
            int count = studentAnnotationMapper.countByStudentId(studentId);
            return Result.success("获取标注统计成功", count);
        } catch (Exception e) {
            log.error("获取标注统计失败", e);
            return Result.error("获取标注统计失败: " + e.getMessage());
        }
    }
} 