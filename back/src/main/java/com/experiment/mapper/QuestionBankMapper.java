package com.experiment.mapper;

import com.experiment.pojo.QuestionBank;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 题库Mapper
 */
@Mapper
public interface QuestionBankMapper {
    
    /**
     * 插入题目到题库
     */
    @Insert("INSERT INTO question_bank (subject, knowledge_point, type, difficulty, content, options, " +
            "answer, analysis, score, source, use_count, avg_score, create_time, update_time) " +
            "VALUES (#{subject}, #{knowledgePoint}, #{type}, #{difficulty}, #{content}, #{options}, " +
            "#{answer}, #{analysis}, #{score}, #{source}, #{useCount}, #{avgScore}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(QuestionBank questionBank);
    
    /**
     * 批量插入题目
     */
    @Insert("<script>" +
            "INSERT INTO question_bank (subject, knowledge_point, type, difficulty, content, options, " +
            "answer, analysis, score, source, use_count, avg_score, create_time, update_time) VALUES " +
            "<foreach collection='list' item='item' separator=','>" +
            "(#{item.subject}, #{item.knowledgePoint}, #{item.type}, #{item.difficulty}, #{item.content}, " +
            "#{item.options}, #{item.answer}, #{item.analysis}, #{item.score}, #{item.source}, " +
            "#{item.useCount}, #{item.avgScore}, #{item.createTime}, #{item.updateTime})" +
            "</foreach>" +
            "</script>")
    int batchInsert(List<QuestionBank> questions);
    
    /**
     * 根据条件查询题目
     */
    @Select("<script>" +
            "SELECT * FROM question_bank WHERE 1=1 " +
            "<if test='subject != null'>AND subject = #{subject}</if> " +
            "<if test='knowledgePoint != null'>AND knowledge_point = #{knowledgePoint}</if> " +
            "<if test='type != null'>AND type = #{type}</if> " +
            "<if test='difficulty != null'>AND difficulty = #{difficulty}</if> " +
            "ORDER BY use_count ASC, create_time DESC " +
            "LIMIT #{limit}" +
            "</script>")
    List<QuestionBank> selectByConditions(@Param("subject") String subject,
                                          @Param("knowledgePoint") String knowledgePoint,
                                          @Param("type") String type,
                                          @Param("difficulty") String difficulty,
                                          @Param("limit") Integer limit);
    
    /**
     * 根据ID查询
     */
    @Select("SELECT * FROM question_bank WHERE id = #{id}")
    QuestionBank selectById(Long id);
    
    /**
     * 更新使用次数
     */
    @Update("UPDATE question_bank SET use_count = use_count + 1, update_time = NOW() WHERE id = #{id}")
    int incrementUseCount(Long id);
    
    /**
     * 更新平均得分率
     */
    @Update("UPDATE question_bank SET avg_score = #{avgScore}, update_time = NOW() WHERE id = #{id}")
    int updateAvgScore(@Param("id") Long id, @Param("avgScore") Double avgScore);
    
    /**
     * 统计题库数量
     */
    @Select("<script>" +
            "SELECT COUNT(*) FROM question_bank WHERE 1=1 " +
            "<if test='subject != null'>AND subject = #{subject}</if> " +
            "<if test='knowledgePoint != null'>AND knowledge_point = #{knowledgePoint}</if> " +
            "<if test='type != null'>AND type = #{type}</if> " +
            "<if test='difficulty != null'>AND difficulty = #{difficulty}</if>" +
            "</script>")
    int countByConditions(@Param("subject") String subject,
                         @Param("knowledgePoint") String knowledgePoint,
                         @Param("type") String type,
                         @Param("difficulty") String difficulty);
    
    /**
     * 删除题目
     */
    @Delete("DELETE FROM question_bank WHERE id = #{id}")
    int deleteById(Long id);
}
