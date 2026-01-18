package com.experiment.controller;

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

import com.experiment.pojo.Course;
import com.experiment.pojo.CourseDesignDTO;
import com.experiment.pojo.CourseQueryDTO;
import com.experiment.result.PageResult;
import com.experiment.result.Result;
import com.experiment.service.CourseService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/course")
@CrossOrigin
@Slf4j
public class CourseController {

    @Autowired
    private CourseService courseService;

    /**
     * 分页查询课程列表
     */
    @GetMapping("/list")
    public PageResult<Course> getCourseList(CourseQueryDTO queryDTO) {
        log.info("查询课程列表：{}", queryDTO);
        return courseService.getCourseList(queryDTO);
    }

    /**
     * 根据ID查询课程
     */
    @GetMapping("/{id}")
    public Result<Course> getCourseById(@PathVariable Long id) {
        log.info("查询课程：{}", id);
        return courseService.getCourseById(id);
    }

    /**
     * 添加课程
     */
    @PostMapping("/add")
    public Result<String> addCourse(@RequestBody Course course) {
        log.info("添加课程：{}", course);
        return courseService.addCourse(course);
    }

    /**
     * 更新课程
     */
    @PutMapping("/update")
    public Result<String> updateCourse(@RequestBody Course course) {
        log.info("更新课程：{}", course);
        return courseService.updateCourse(course);
    }

    /**
     * 删除课程
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteCourse(@PathVariable Long id) {
        log.info("删除课程：{}", id);
        return courseService.deleteCourse(id);
    }

    /**
     * 更新课程状态
     */
    @PutMapping("/{id}/status")
    public Result<String> updateCourseStatus(@PathVariable Long id, @RequestParam String status) {
        log.info("更新课程状态：{} - {}", id, status);
        return courseService.updateCourseStatus(id, status);
    }

    /**
     * 根据教师ID查询课程列表
     */
    @GetMapping("/teacher/{teacherId}")
    public Result<Object> getCoursesByTeacherId(@PathVariable Long teacherId) {
        log.info("查询教师课程：{}", teacherId);
        return courseService.getCoursesByTeacherId(teacherId);
    }

    /**
     * 根据学生ID查询课程列表
     */
    @GetMapping("/student/{studentId}")
    public Result<Object> getCoursesByStudentId(@PathVariable Long studentId) {
        log.info("查询学生课程：{}", studentId);
        return courseService.getCoursesByStudentId(studentId);
    }

    /**
     * AI生成课程大纲
     */
    @PostMapping("/generate-outline")
    public Result<Object> generateCourseOutline(@RequestBody CourseDesignDTO designDTO) {
        log.info("AI生成课程大纲：{}", designDTO);
        return courseService.generateCourseOutline(designDTO);
    }

    /**
     * AI生成教学内容
     */
    @PostMapping("/generate-content/{chapterId}")
    public Result<Object> generateCourseContent(@PathVariable Long chapterId) {
        log.info("AI生成教学内容：{}", chapterId);
        return courseService.generateCourseContent(chapterId);
    }

    /**
     * 获取课程统计信息
     */
    @GetMapping("/stats")
    public Result<Object> getCourseStats() {
        log.info("获取课程统计信息");
        return courseService.getCourseStats();
    }
} 