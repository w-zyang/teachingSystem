package com.experiment.pojo;

import java.time.LocalDateTime;

public class StudentNote {
    private Long id;
    private Long studentId;
    private Long resourceId;
    private String title;
    private String content;
    private Integer pageNumber;
    private String position; // JSON字符串形式存储位置信息
    private String tags;
    private String color;
    private Boolean isPublic;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 关联对象字段
    private String studentName;
    private String resourceTitle;

    public StudentNote() {}

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
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
        return "StudentNote{" +
                "id=" + id +
                ", studentId=" + studentId +
                ", resourceId=" + resourceId +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", pageNumber=" + pageNumber +
                ", position='" + position + '\'' +
                ", tags='" + tags + '\'' +
                ", color='" + color + '\'' +
                ", isPublic=" + isPublic +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", studentName='" + studentName + '\'' +
                ", resourceTitle='" + resourceTitle + '\'' +
                '}';
    }
} 