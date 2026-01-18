package com.experiment;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.experiment.service.PracticeService;

@SpringBootTest
public class PracticeServiceTest {
    
    @Autowired
    private PracticeService practiceService;
    
    @Test
    public void testGetPracticeTypes() {
        List<Map<String, Object>> types = practiceService.getPracticeTypes();
        assertNotNull(types);
        assertFalse(types.isEmpty());
        assertEquals(4, types.size());
        
        // 验证第一个类型
        Map<String, Object> firstType = types.get(0);
        assertNotNull(firstType.get("name"));
        assertNotNull(firstType.get("description"));
        assertNotNull(firstType.get("icon"));
    }
    
    @Test
    public void testGetRecommendedPractices() {
        List<Map<String, Object>> practices = practiceService.getRecommendedPractices();
        assertNotNull(practices);
        assertFalse(practices.isEmpty());
        
        // 验证第一个练习
        Map<String, Object> firstPractice = practices.get(0);
        assertNotNull(firstPractice.get("title"));
        assertNotNull(firstPractice.get("description"));
        assertNotNull(firstPractice.get("difficulty"));
    }
    
    @Test
    public void testGetPracticeHistory() {
        Long studentId = 4L; // 使用测试数据中的学生ID
        List<Map<String, Object>> history = practiceService.getPracticeHistory(studentId);
        assertNotNull(history);
        // 由于是测试数据，可能有历史记录
    }
} 