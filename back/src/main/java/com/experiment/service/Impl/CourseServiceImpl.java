package com.experiment.service.Impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import com.experiment.mapper.ChapterMapper;
import com.experiment.mapper.CourseMapper;
import com.experiment.mapper.LessonMapper;
import com.experiment.pojo.Chapter;
import com.experiment.pojo.Course;
import com.experiment.pojo.CourseDesignDTO;
import com.experiment.pojo.CourseQueryDTO;
import com.experiment.pojo.Lesson;
import com.experiment.result.PageResult;
import com.experiment.result.Result;
import com.experiment.service.CourseService;

@Slf4j
@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseMapper courseMapper;
    
    @Autowired
    private ChapterMapper chapterMapper;
    
    @Autowired
    private LessonMapper lessonMapper;

    @Override
    public PageResult<Course> getCourseList(CourseQueryDTO queryDTO) {
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
        List<Course> courses = courseMapper.findByCondition(queryDTO);
        int total = courseMapper.countByCondition(queryDTO);

        return new PageResult<>(total, courses);
    }

    @Override
    public Result<Course> getCourseById(Long id) {
        if (id == null) {
            return Result.error("课程ID不能为空");
        }

        Course course = courseMapper.findById(id);
        if (course == null) {
            return Result.error("课程不存在");
        }

        // 查询课程章节
        List<Chapter> chapters = chapterMapper.findByCourseId(id);
        for (Chapter chapter : chapters) {
            // 查询章节课时
            List<Lesson> lessons = lessonMapper.findByChapterId(chapter.getId());
            chapter.setLessons(lessons);
        }
        course.setChapters(chapters);

        return Result.success("获取课程成功", course);
    }

    @Override
    public Result<String> addCourse(Course course) {
        // 参数校验
        if (course.getName() == null || course.getTeacherId() == null) {
            return Result.error("课程名称和教师ID不能为空");
        }

        // 设置默认值（只设置数据库表中存在的字段）
        course.setStatus("draft");
        // 注意：students和rating字段在数据库表中不存在，已移除设置
        course.setCreateTime(LocalDateTime.now());
        course.setUpdateTime(LocalDateTime.now());

        courseMapper.insert(course);
        return Result.success("课程添加成功");
    }

    @Override
    public Result<String> updateCourse(Course course) {
        if (course.getId() == null) {
            return Result.error("课程ID不能为空");
        }

        Course existingCourse = courseMapper.findById(course.getId());
        if (existingCourse == null) {
            return Result.error("课程不存在");
        }

        course.setUpdateTime(LocalDateTime.now());
        courseMapper.update(course);
        return Result.success("课程更新成功");
    }

    @Override
    public Result<String> deleteCourse(Long id) {
        if (id == null) {
            return Result.error("课程ID不能为空");
        }

        Course course = courseMapper.findById(id);
        if (course == null) {
            return Result.error("课程不存在");
        }

        // 删除课程相关的章节和课时
        chapterMapper.deleteByCourseId(id);
        courseMapper.deleteById(id);
        
        return Result.success("课程删除成功");
    }

    @Override
    public Result<String> updateCourseStatus(Long id, String status) {
        if (id == null || status == null) {
            return Result.error("参数不能为空");
        }

        Course course = courseMapper.findById(id);
        if (course == null) {
            return Result.error("课程不存在");
        }

        courseMapper.updateStatus(id, status);
        return Result.success("课程状态更新成功");
    }

    @Override
    public Result<Object> getCoursesByTeacherId(Long teacherId) {
        if (teacherId == null) {
            return Result.error("教师ID不能为空");
        }

        List<Course> courses = courseMapper.findByTeacherId(teacherId);
        
        return Result.success("获取教师课程成功", courses);
    }
    
    private List<Course> createDefaultCourses(Long teacherId) {
        List<Course> defaultCourses = new ArrayList<>();
        
        Course course1 = new Course();
        course1.setId(1L);
        course1.setName("嵌入式Linux系统开发");
        course1.setDescription("深入学习嵌入式Linux系统开发，包括内核、驱动、应用开发等");
        course1.setTeacherId(teacherId);
        course1.setStatus("published");
        course1.setStudents(25);
        course1.setRating(4.8);
        course1.setCreateTime(LocalDateTime.now());
        course1.setUpdateTime(LocalDateTime.now());
        defaultCourses.add(course1);
        
        Course course2 = new Course();
        course2.setId(2L);
        course2.setName("Linux系统编程基础");
        course2.setDescription("Linux系统编程基础知识，包括进程管理、文件系统、网络编程等");
        course2.setTeacherId(teacherId);
        course2.setStatus("published");
        course2.setStudents(32);
        course2.setRating(4.6);
        course2.setCreateTime(LocalDateTime.now());
        course2.setUpdateTime(LocalDateTime.now());
        defaultCourses.add(course2);
        
        Course course3 = new Course();
        course3.setId(3L);
        course3.setName("Linux内核与驱动开发");
        course3.setDescription("Linux内核源码分析和设备驱动程序开发");
        course3.setTeacherId(teacherId);
        course3.setStatus("published");
        course3.setStudents(18);
        course3.setRating(4.9);
        course3.setCreateTime(LocalDateTime.now());
        course3.setUpdateTime(LocalDateTime.now());
        defaultCourses.add(course3);
        
        Course course4 = new Course();
        course4.setId(4L);
        course4.setName("计算机网络原理");
        course4.setDescription("计算机网络基础理论和TCP/IP协议栈深入学习");
        course4.setTeacherId(teacherId);
        course4.setStatus("published");
        course4.setStudents(45);
        course4.setRating(4.7);
        course4.setCreateTime(LocalDateTime.now());
        course4.setUpdateTime(LocalDateTime.now());
        defaultCourses.add(course4);
        
        return defaultCourses;
    }

    @Override
    public Result<Object> getCoursesByStudentId(Long studentId) {
        if (studentId == null) {
            return Result.error("学生ID不能为空");
        }

        try {
            // 从数据库查询学生选修的课程
            List<Course> courses = courseMapper.findByStudentId(studentId);
            
            if (courses == null || courses.isEmpty()) {
                return Result.success("获取学生课程成功", new ArrayList<>());
            }
            
            return Result.success("获取学生课程成功", courses);
            
        } catch (Exception e) {
            System.err.println("查询学生课程失败：studentId=" + studentId + ", error=" + e.getMessage());
            return Result.error("查询学生课程失败：" + e.getMessage());
        }
    }

    @Override
    public Result<Object> generateCourseOutline(CourseDesignDTO designDTO) {
        // 模拟AI生成课程大纲
        Map<String, Object> result = new HashMap<>();
        result.put("message", "AI生成课程大纲功能待实现");
        result.put("status", "pending");
        return Result.success("AI生成功能开发中", result);
    }

    @Override
    public Result<Object> generateCourseContent(Long chapterId) {
        // 模拟AI生成教学内容
        Map<String, Object> result = new HashMap<>();
        result.put("message", "AI生成教学内容功能待实现");
        result.put("status", "pending");
        return Result.success("AI生成功能开发中", result);
    }

    @Override
    public Result<Object> getCourseStats() {
        Map<String, Object> stats = new HashMap<>();
        // 这里应该从数据库统计，暂时使用模拟数据
        stats.put("totalCourses", 0);
        stats.put("publishedCourses", 0);
        stats.put("draftCourses", 0);
        stats.put("totalStudents", 0);
        return Result.success("获取课程统计成功", stats);
    }
} 