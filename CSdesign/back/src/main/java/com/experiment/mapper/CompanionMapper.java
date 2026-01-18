package com.experiment.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.experiment.pojo.LearningCompanion;
import com.experiment.pojo.StudyGroup;

@Mapper
public interface CompanionMapper {
    
    /**
     * 查找学习伙伴匹配
     */
    @Select("SELECT lc.*, u1.real_name as student1_name, u2.real_name as student2_name, " +
            "u1.major as student1_major, u2.major as student2_major " +
            "FROM learning_companion_matching lc " +
            "JOIN user u1 ON lc.student1_id = u1.id " +
            "JOIN user u2 ON lc.student2_id = u2.id " +
            "WHERE (lc.student1_id = #{studentId} OR lc.student2_id = #{studentId}) " +
            "AND lc.status = #{status} " +
            "ORDER BY lc.matching_score DESC")
    List<Map<String, Object>> findCompanionMatches(@Param("studentId") Long studentId, @Param("status") String status);
    
    /**
     * 创建学习伙伴匹配
     */
    @Insert("INSERT INTO learning_companion_matching (student1_id, student2_id, matching_score, matching_criteria, " +
            "common_subjects, complementary_strengths, status) " +
            "VALUES (#{student1Id}, #{student2Id}, #{matchingScore}, #{matchingCriteria}, #{commonSubjects}, #{complementaryStrengths}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int createCompanionMatch(LearningCompanion companion);
    
    /**
     * 智能匹配学习伙伴
     */
    @Select("SELECT u2.id as partner_id, u2.real_name, u2.major, u2.grade, " +
            "COALESCE(AVG(se.score), 0) as avg_score, " +
            "COUNT(DISTINCT sc.course_id) as common_courses, " +
            "CASE " +
            "WHEN ABS(COALESCE(AVG(se1.score), 0) - COALESCE(AVG(se2.score), 0)) < 10 THEN 0.3 " +
            "WHEN ABS(COALESCE(AVG(se1.score), 0) - COALESCE(AVG(se2.score), 0)) < 20 THEN 0.2 " +
            "ELSE 0.1 END + " +
            "CASE WHEN COUNT(DISTINCT sc.course_id) > 0 THEN 0.4 ELSE 0 END + " +
            "CASE WHEN u2.major = u1.major THEN 0.3 ELSE 0.1 END as matching_score " +
            "FROM user u1 " +
            "JOIN user u2 ON u2.id != u1.id AND u2.role = 'student' " +
            "LEFT JOIN student_exam se1 ON se1.student_id = u1.id " +
            "LEFT JOIN student_exam se2 ON se2.student_id = u2.id " +
            "LEFT JOIN student_course sc1 ON sc1.student_id = u1.id " +
            "LEFT JOIN student_course sc2 ON sc2.student_id = u2.id AND sc2.course_id = sc1.course_id " +
            "LEFT JOIN student_course sc ON sc.student_id = u2.id " +
            "LEFT JOIN student_exam se ON se.student_id = u2.id " +
            "WHERE u1.id = #{studentId} " +
            "AND NOT EXISTS (SELECT 1 FROM learning_companion_matching lcm " +
            "WHERE (lcm.student1_id = u1.id AND lcm.student2_id = u2.id) " +
            "OR (lcm.student1_id = u2.id AND lcm.student2_id = u1.id)) " +
            "GROUP BY u2.id, u2.real_name, u2.major, u2.grade " +
            "HAVING matching_score > 0.5 " +
            "ORDER BY matching_score DESC " +
            "LIMIT #{limit}")
    List<Map<String, Object>> findPotentialCompanions(@Param("studentId") Long studentId, @Param("limit") Integer limit);
    
    /**
     * 更新伙伴关系状态
     */
    @Update("UPDATE learning_companion_matching SET status = #{status}, activation_time = #{activationTime}, " +
            "update_time = NOW() WHERE id = #{matchId}")
    int updateCompanionStatus(@Param("matchId") Long matchId, @Param("status") String status, @Param("activationTime") String activationTime);
    
    /**
     * 获取学习小组列表
     */
    @Select("SELECT sg.*, u.real_name as leader_name, " +
            "COUNT(sgm.id) as member_count " +
            "FROM study_group sg " +
            "JOIN user u ON sg.leader_id = u.id " +
            "LEFT JOIN study_group_member sgm ON sg.id = sgm.group_id AND sgm.status = 'active' " +
            "WHERE sg.status = 'active' " +
            "AND (sg.privacy_level = 'public' OR sg.leader_id = #{studentId} " +
            "OR EXISTS (SELECT 1 FROM study_group_member sgm2 WHERE sgm2.group_id = sg.id AND sgm2.student_id = #{studentId})) " +
            "${searchCondition} " +
            "GROUP BY sg.id " +
            "ORDER BY sg.activity_level DESC, sg.create_time DESC " +
            "LIMIT #{limit}")
    List<Map<String, Object>> getStudyGroups(@Param("studentId") Long studentId, @Param("searchCondition") String searchCondition, @Param("limit") Integer limit);
    
    /**
     * 创建学习小组
     */
    @Insert("INSERT INTO study_group (group_name, group_description, subject, leader_id, max_members, " +
            "group_type, privacy_level, learning_goals, group_rules) " +
            "VALUES (#{groupName}, #{groupDescription}, #{subject}, #{leaderId}, #{maxMembers}, " +
            "#{groupType}, #{privacyLevel}, #{learningGoals}, #{groupRules})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int createStudyGroup(StudyGroup studyGroup);
    
    /**
     * 加入学习小组
     */
    @Insert("INSERT INTO study_group_member (group_id, student_id, role, join_time) " +
            "VALUES (#{groupId}, #{studentId}, #{role}, NOW())")
    int joinStudyGroup(@Param("groupId") Long groupId, @Param("studentId") Long studentId, @Param("role") String role);
    
    /**
     * 获取小组成员
     */
    @Select("SELECT sgm.*, u.real_name, u.major, u.grade " +
            "FROM study_group_member sgm " +
            "JOIN user u ON sgm.student_id = u.id " +
            "WHERE sgm.group_id = #{groupId} AND sgm.status = 'active' " +
            "ORDER BY sgm.role, sgm.join_time")
    List<Map<String, Object>> getGroupMembers(@Param("groupId") Long groupId);
    
    /**
     * 创建协作学习会话
     */
    @Insert("INSERT INTO collaborative_learning_session (session_name, session_type, creator_id, subject, topic, " +
            "max_participants, session_description, start_time, estimated_duration, learning_goals) " +
            "VALUES (#{sessionName}, #{sessionType}, #{creatorId}, #{subject}, #{topic}, #{maxParticipants}, " +
            "#{sessionDescription}, #{startTime}, #{estimatedDuration}, #{learningGoals})")
    @Options(useGeneratedKeys = true, keyProperty = "sessionId")
    int createCollaborativeSession(@Param("sessionName") String sessionName, @Param("sessionType") String sessionType,
                                  @Param("creatorId") Long creatorId, @Param("subject") String subject, @Param("topic") String topic,
                                  @Param("maxParticipants") Integer maxParticipants, @Param("sessionDescription") String sessionDescription,
                                  @Param("startTime") String startTime, @Param("estimatedDuration") Integer estimatedDuration,
                                  @Param("learningGoals") String learningGoals);
    
    /**
     * 获取协作会话列表
     */
    @Select("SELECT cls.*, u.real_name as creator_name, " +
            "COUNT(csp.id) as participant_count " +
            "FROM collaborative_learning_session cls " +
            "JOIN user u ON cls.creator_id = u.id " +
            "LEFT JOIN collaborative_session_participant csp ON cls.id = csp.session_id AND csp.status = 'active' " +
            "WHERE cls.status IN ('scheduled', 'active') " +
            "AND cls.start_time > NOW() " +
            "GROUP BY cls.id " +
            "ORDER BY cls.start_time " +
            "LIMIT #{limit}")
    List<Map<String, Object>> getUpcomingSessions(@Param("limit") Integer limit);
    
    /**
     * 记录协作互动
     */
    @Insert("INSERT INTO collaborative_interaction_record (session_id, student_id, interaction_type, content, target_student_id, metadata) " +
            "VALUES (#{sessionId}, #{studentId}, #{interactionType}, #{content}, #{targetStudentId}, #{metadata})")
    int recordInteraction(@Param("sessionId") Long sessionId, @Param("studentId") Long studentId,
                         @Param("interactionType") String interactionType, @Param("content") String content,
                         @Param("targetStudentId") Long targetStudentId, @Param("metadata") String metadata);
    
    /**
     * 更新协作评分
     */
    @Update("UPDATE learning_companion_matching SET collaboration_count = collaboration_count + 1, " +
            "satisfaction_rating = #{satisfactionRating}, update_time = NOW() " +
            "WHERE id = #{matchId}")
    int updateCollaborationRating(@Param("matchId") Long matchId, @Param("satisfactionRating") Double satisfactionRating);
} 