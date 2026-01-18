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

import com.experiment.mapper.StudentNoteMapper;
import com.experiment.pojo.StudentNote;
import com.experiment.result.Result;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/student-notes")
@CrossOrigin
public class StudentNoteController {

    @Autowired
    private StudentNoteMapper studentNoteMapper;

    /**
     * 获取学生的所有笔记
     */
    @GetMapping("/student/{studentId}")
    public Result<List<StudentNote>> getStudentNotes(@PathVariable Long studentId) {
        try {
            List<StudentNote> notes = studentNoteMapper.findByStudentId(studentId);
            return Result.success("获取学生笔记成功", notes);
        } catch (Exception e) {
            log.error("获取学生笔记失败", e);
            return Result.error("获取学生笔记失败: " + e.getMessage());
        }
    }

    /**
     * 获取特定资源的学生笔记
     */
    @GetMapping("/student/{studentId}/resource/{resourceId}")
    public Result<List<StudentNote>> getResourceNotes(@PathVariable Long studentId, 
                                                     @PathVariable Long resourceId) {
        try {
            List<StudentNote> notes = studentNoteMapper.findByStudentIdAndResourceId(studentId, resourceId);
            return Result.success("获取资源笔记成功", notes);
        } catch (Exception e) {
            log.error("获取资源笔记失败", e);
            return Result.error("获取资源笔记失败: " + e.getMessage());
        }
    }

    /**
     * 获取资源的公开笔记
     */
    @GetMapping("/public/resource/{resourceId}")
    public Result<List<StudentNote>> getPublicNotes(@PathVariable Long resourceId) {
        try {
            List<StudentNote> notes = studentNoteMapper.findPublicNotesByResourceId(resourceId);
            return Result.success("获取公开笔记成功", notes);
        } catch (Exception e) {
            log.error("获取公开笔记失败", e);
            return Result.error("获取公开笔记失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID获取笔记详情
     */
    @GetMapping("/{id}")
    public Result<StudentNote> getNoteById(@PathVariable Long id) {
        try {
            StudentNote note = studentNoteMapper.findById(id);
            if (note == null) {
                return Result.error("笔记不存在");
            }
            return Result.success("获取笔记详情成功", note);
        } catch (Exception e) {
            log.error("获取笔记详情失败", e);
            return Result.error("获取笔记详情失败: " + e.getMessage());
        }
    }

    /**
     * 创建新笔记
     */
    @PostMapping
    public Result<StudentNote> createNote(@RequestBody StudentNote note) {
        try {
            // 参数验证
            if (note.getStudentId() == null || note.getResourceId() == null) {
                return Result.error("学生ID和资源ID不能为空");
            }
            if (note.getTitle() == null || note.getTitle().trim().isEmpty()) {
                return Result.error("笔记标题不能为空");
            }
            if (note.getContent() == null || note.getContent().trim().isEmpty()) {
                return Result.error("笔记内容不能为空");
            }

            // 设置默认值
            if (note.getPageNumber() == null) {
                note.setPageNumber(1);
            }
            if (note.getColor() == null) {
                note.setColor("#FFD700");
            }
            if (note.getIsPublic() == null) {
                note.setIsPublic(false);
            }

            int result = studentNoteMapper.insert(note);
            if (result > 0) {
                log.info("学生{}成功创建笔记: {}", note.getStudentId(), note.getTitle());
                return Result.success("创建笔记成功", note);
            } else {
                return Result.error("创建笔记失败");
            }
        } catch (Exception e) {
            log.error("创建笔记失败", e);
            return Result.error("创建笔记失败: " + e.getMessage());
        }
    }

    /**
     * 更新笔记
     */
    @PutMapping("/{id}")
    public Result<String> updateNote(@PathVariable Long id, @RequestBody StudentNote note) {
        try {
            // 验证笔记是否存在
            StudentNote existingNote = studentNoteMapper.findById(id);
            if (existingNote == null) {
                return Result.error("笔记不存在");
            }

            // 参数验证
            if (note.getTitle() == null || note.getTitle().trim().isEmpty()) {
                return Result.error("笔记标题不能为空");
            }
            if (note.getContent() == null || note.getContent().trim().isEmpty()) {
                return Result.error("笔记内容不能为空");
            }

            note.setId(id);
            int result = studentNoteMapper.update(note);
            if (result > 0) {
                log.info("成功更新笔记ID: {}", id);
                return Result.success("更新笔记成功");
            } else {
                return Result.error("更新笔记失败");
            }
        } catch (Exception e) {
            log.error("更新笔记失败", e);
            return Result.error("更新笔记失败: " + e.getMessage());
        }
    }

    /**
     * 删除笔记
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteNote(@PathVariable Long id) {
        try {
            // 验证笔记是否存在
            StudentNote existingNote = studentNoteMapper.findById(id);
            if (existingNote == null) {
                return Result.error("笔记不存在");
            }

            int result = studentNoteMapper.deleteById(id);
            if (result > 0) {
                log.info("成功删除笔记ID: {}", id);
                return Result.success("删除笔记成功");
            } else {
                return Result.error("删除笔记失败");
            }
        } catch (Exception e) {
            log.error("删除笔记失败", e);
            return Result.error("删除笔记失败: " + e.getMessage());
        }
    }

    /**
     * 搜索笔记
     */
    @GetMapping("/search")
    public Result<List<StudentNote>> searchNotes(@RequestParam Long studentId,
                                                @RequestParam(required = false) String keyword,
                                                @RequestParam(required = false) Long resourceId) {
        try {
            List<StudentNote> notes = studentNoteMapper.searchNotes(studentId, keyword, resourceId);
            return Result.success("搜索笔记成功", notes);
        } catch (Exception e) {
            log.error("搜索笔记失败", e);
            return Result.error("搜索笔记失败: " + e.getMessage());
        }
    }

    /**
     * 获取学生笔记统计
     */
    @GetMapping("/stats/{studentId}")
    public Result<Integer> getNoteStats(@PathVariable Long studentId) {
        try {
            int count = studentNoteMapper.countByStudentId(studentId);
            return Result.success("获取笔记统计成功", count);
        } catch (Exception e) {
            log.error("获取笔记统计失败", e);
            return Result.error("获取笔记统计失败: " + e.getMessage());
        }
    }
} 