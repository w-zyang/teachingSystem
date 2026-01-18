package com.experiment.pojo;

import java.time.LocalDateTime;

public class StudentAnnotation {
    private Long id;
    private Long studentId;
    private Long resourceId;
    private String type; // highlight, underline, comment, bookmark
    private String selectedText;
    private Integer pageNumber;
    private String position; // JSON字符串形式存储位置信息
    private String color;
    private String comment;
    private Integer importance; // 1-5
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 关联对象字段
    private String studentName;
    private String resourceTitle;

    public StudentAnnotation() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSelectedText() {
        return selectedText;
    }

    public void setSelectedText(String selectedText) {
        this.selectedText = selectedText;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getImportance() {
        return importance;
    }

    public void setImportance(Integer importance) {
        this.importance = importance;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getResourceTitle() {
        return resourceTitle;
    }

    public void setResourceTitle(String resourceTitle) {
        this.resourceTitle = resourceTitle;
    }

    @Override
    public String toString() {
        return "StudentAnnotation{" +
                "id=" + id +
                ", studentId=" + studentId +
                ", resourceId=" + resourceId +
                ", type='" + type + '\'' +
                ", selectedText='" + selectedText + '\'' +
                ", pageNumber=" + pageNumber +
                ", position='" + position + '\'' +
                ", color='" + color + '\'' +
                ", comment='" + comment + '\'' +
                ", importance=" + importance +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", studentName='" + studentName + '\'' +
                ", resourceTitle='" + resourceTitle + '\'' +
                '}';
    }
} 