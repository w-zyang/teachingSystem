package com.experiment.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.experiment.result.Result;

import lombok.extern.slf4j.Slf4j;

/**
 * VR虚拟教室控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/vr")
@CrossOrigin
public class VRController {

    /**
     * 获取VR课程列表
     */
    @GetMapping("/courses")
    public Result<Object> getVRCourses(@RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "10") int size) {
        log.info("获取VR课程列表 - 页码: {}, 每页大小: {}", page, size);
        
        try {
            List<Map<String, Object>> courses = new ArrayList<>();
            
            // 模拟VR课程数据
            courses.add(createVRCourse(1, "数字信号处理基础", "DSP", 
                "学习数字信号处理的基本概念和算法", "intermediate", 180));
            courses.add(createVRCourse(2, "线性代数", "Mathematics", 
                "线性代数的基础理论与应用", "beginner", 240));
            courses.add(createVRCourse(3, "计算机图形学", "Graphics", 
                "3D图形学原理与VR应用", "advanced", 300));
            courses.add(createVRCourse(4, "机器学习入门", "AI", 
                "机器学习基础算法与实践", "intermediate", 360));
            courses.add(createVRCourse(5, "虚拟现实技术", "VR", 
                "VR技术原理与开发实践", "advanced", 420));
            
            Map<String, Object> result = new HashMap<>();
            result.put("courses", courses);
            result.put("total", courses.size());
            result.put("page", page);
            result.put("size", size);
            
            return Result.success("获取VR课程列表成功", result);
        } catch (Exception e) {
            log.error("获取VR课程列表失败", e);
            return Result.error("获取VR课程列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取VR课程详情
     */
    @GetMapping("/courses/{courseId}")
    public Result<Object> getVRCourseDetail(@PathVariable Long courseId) {
        log.info("获取VR课程详情 - 课程ID: {}", courseId);
        
        try {
            Map<String, Object> course = createDetailedVRCourse(courseId);
            return Result.success("获取VR课程详情成功", course);
        } catch (Exception e) {
            log.error("获取VR课程详情失败", e);
            return Result.error("获取VR课程详情失败: " + e.getMessage());
        }
    }

    /**
     * 创建VR学习会话
     */
    @PostMapping("/sessions")
    public Result<Object> createVRSession(@RequestBody Map<String, Object> data) {
        log.info("创建VR学习会话 - 数据: {}", data);
        
        try {
            Long courseId = Long.valueOf(data.get("courseId").toString());
            String sessionName = (String) data.get("sessionName");
            Integer maxUsers = (Integer) data.getOrDefault("maxUsers", 20);
            
            Map<String, Object> session = new HashMap<>();
            session.put("sessionId", System.currentTimeMillis());
            session.put("courseId", courseId);
            session.put("sessionName", sessionName);
            session.put("maxUsers", maxUsers);
            session.put("currentUsers", 0);
            session.put("status", "waiting");
            session.put("createdAt", new Date());
            session.put("vrEnvironment", "classroom_3d");
            session.put("hasTeacher", true);
            session.put("isRecording", false);
            
            return Result.success("创建VR学习会话成功", session);
        } catch (Exception e) {
            log.error("创建VR学习会话失败", e);
            return Result.error("创建VR学习会话失败: " + e.getMessage());
        }
    }

    /**
     * 加入VR教室
     */
    @PostMapping("/sessions/{sessionId}/join")
    public Result<Object> joinVRClassroom(@PathVariable Long sessionId, 
                                          @RequestBody Map<String, Object> data) {
        log.info("加入VR教室 - 会话ID: {}, 用户数据: {}", sessionId, data);
        
        try {
            String username = (String) data.get("username");
            String avatar = (String) data.getOrDefault("avatar", "data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNDAiIGhlaWdodD0iNDAiIHZpZXdCb3g9IjAgMCA0MCA0MCIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPGNpcmNsZSBjeD0iMjAiIGN5PSIyMCIgcj0iMjAiIGZpbGw9IiMzNDk4ZGIiLz4KPHN2ZyB3aWR0aD0iMjQiIGhlaWdodD0iMjQiIHZpZXdCb3g9IjAgMCAyNCAyNCIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB4PSI4IiB5PSI4Ij4KPHBhdGggZD0iTTEyIDEyQzE0LjIwOTEgMTIgMTYgMTAuMjA5MSAxNiA4QzE2IDUuNzkwODYgMTQuMjA5MSA0IDEyIDRDOS43OTA4NiA0IDggNS43OTA4NiA4IDhDOCAxMC4yMDkxIDkuNzkwODYgMTIgMTIgMTJaIiBmaWxsPSJ3aGl0ZSIvPgo8cGF0aCBkPSJNMTIgMTRDOC42ODYyOSAxNCA2IDE2LjY4NjMgNiAyMFYyMkgxOFYyMEMxOCAxNi42ODYzIDE1LjMxMzcgMTQgMTIgMTRaIiBmaWxsPSJ3aGl0ZSIvPgo8L3N2Zz4KPC9zdmc+");
            String seatPreference = (String) data.getOrDefault("seatPreference", "auto");
            
            Map<String, Object> joinResult = new HashMap<>();
            joinResult.put("sessionId", sessionId);
            joinResult.put("userId", data.get("userId"));
            joinResult.put("username", username);
            joinResult.put("avatar", avatar);
            joinResult.put("seatNumber", generateRandomSeat());
            joinResult.put("joinTime", new Date());
            joinResult.put("status", "connected");
            joinResult.put("vrReady", true);
            
            // 模拟其他在线用户
            List<Map<String, Object>> onlineUsers = generateOnlineUsers();
            joinResult.put("onlineUsers", onlineUsers);
            
            return Result.success("成功加入VR教室", joinResult);
        } catch (Exception e) {
            log.error("加入VR教室失败", e);
            return Result.error("加入VR教室失败: " + e.getMessage());
        }
    }

    /**
     * 离开VR教室
     */
    @PostMapping("/sessions/{sessionId}/leave")
    public Result<Object> leaveVRClassroom(@PathVariable Long sessionId) {
        log.info("离开VR教室 - 会话ID: {}", sessionId);
        
        try {
            Map<String, Object> result = new HashMap<>();
            result.put("sessionId", sessionId);
            result.put("leaveTime", new Date());
            result.put("sessionDuration", "25分钟");
            result.put("learningPoints", 85);
            result.put("message", "感谢您的参与，学习数据已保存");
            
            return Result.success("成功离开VR教室", result);
        } catch (Exception e) {
            log.error("离开VR教室失败", e);
            return Result.error("离开VR教室失败: " + e.getMessage());
        }
    }

    /**
     * 发送VR聊天消息
     */
    @PostMapping("/sessions/{sessionId}/messages")
    public Result<Object> sendVRMessage(@PathVariable Long sessionId, 
                                        @RequestBody Map<String, Object> message) {
        log.info("发送VR聊天消息 - 会话ID: {}, 消息: {}", sessionId, message);
        
        try {
            Map<String, Object> result = new HashMap<>();
            result.put("messageId", System.currentTimeMillis());
            result.put("sessionId", sessionId);
            result.put("text", message.get("text"));
            result.put("username", message.get("username"));
            result.put("timestamp", new Date());
            result.put("delivered", true);
            
            return Result.success("消息发送成功", result);
        } catch (Exception e) {
            log.error("发送VR聊天消息失败", e);
            return Result.error("发送消息失败: " + e.getMessage());
        }
    }

    /**
     * 获取VR聊天历史
     */
    @GetMapping("/sessions/{sessionId}/messages")
    public Result<Object> getVRMessages(@PathVariable Long sessionId,
                                        @RequestParam(defaultValue = "20") int limit) {
        log.info("获取VR聊天历史 - 会话ID: {}, 限制: {}", sessionId, limit);
        
        try {
            List<Map<String, Object>> messages = new ArrayList<>();
            
            // 模拟聊天消息
            messages.add(createMessage(1, "系统", "欢迎进入VR虚拟教室！", 10));
            messages.add(createMessage(2, "李老师", "今天我们学习数字信号处理", 9));
            messages.add(createMessage(3, "张同学", "老师，这个算法的复杂度是多少？", 8));
            messages.add(createMessage(4, "李老师", "时间复杂度是O(n log n)", 7));
            messages.add(createMessage(5, "王同学", "可以演示一下具体实现吗？", 5));
            
            Map<String, Object> result = new HashMap<>();
            result.put("messages", messages);
            result.put("total", messages.size());
            result.put("sessionId", sessionId);
            
            return Result.success("获取聊天历史成功", result);
        } catch (Exception e) {
            log.error("获取VR聊天历史失败", e);
            return Result.error("获取聊天历史失败: " + e.getMessage());
        }
    }

    /**
     * 更新VR学习进度
     */
    @PutMapping("/sessions/{sessionId}/progress")
    public Result<Object> updateVRProgress(@PathVariable Long sessionId, 
                                           @RequestBody Map<String, Object> progress) {
        log.info("更新VR学习进度 - 会话ID: {}, 进度: {}", sessionId, progress);
        
        try {
            Map<String, Object> result = new HashMap<>();
            result.put("sessionId", sessionId);
            result.put("progress", progress.get("progress"));
            result.put("currentChapter", progress.get("currentChapter"));
            result.put("completedTasks", progress.get("completedTasks"));
            result.put("totalTasks", progress.get("totalTasks"));
            result.put("lastUpdate", new Date());
            result.put("learningPoints", calculateLearningPoints(progress));
            
            return Result.success("学习进度更新成功", result);
        } catch (Exception e) {
            log.error("更新VR学习进度失败", e);
            return Result.error("更新学习进度失败: " + e.getMessage());
        }
    }

    /**
     * 获取VR设备信息
     */
    @GetMapping("/device-info")
    public Result<Object> getVRDeviceInfo() {
        log.info("获取VR设备信息");
        
        try {
            Map<String, Object> deviceInfo = new HashMap<>();
            deviceInfo.put("webXRSupported", true);
            deviceInfo.put("vrDisplays", Arrays.asList("Oculus Rift", "HTC Vive", "Oculus Quest"));
            deviceInfo.put("handTrackingSupported", true);
            deviceInfo.put("spatialTrackingSupported", true);
            deviceInfo.put("recommendedResolution", "2160x1200");
            deviceInfo.put("recommendedRefreshRate", "90Hz");
            deviceInfo.put("minSystemRequirements", Map.of(
                "cpu", "Intel i5-4590 / AMD FX 8350",
                "gpu", "NVIDIA GTX 1060 / AMD RX 580",
                "ram", "8GB",
                "usb", "USB 3.0"
            ));
            
            return Result.success("获取VR设备信息成功", deviceInfo);
        } catch (Exception e) {
            log.error("获取VR设备信息失败", e);
            return Result.error("获取VR设备信息失败: " + e.getMessage());
        }
    }

    /**
     * 检查WebXR支持
     */
    @GetMapping("/webxr-support")
    public Result<Object> checkWebXRSupport() {
        log.info("检查WebXR支持");
        
        try {
            Map<String, Object> support = new HashMap<>();
            support.put("webXRSupported", true);
            support.put("immersiveVRSupported", true);
            support.put("immersiveARSupported", false);
            support.put("inlineSupported", true);
            support.put("browserName", "Chrome");
            support.put("browserVersion", "91+");
            support.put("features", Arrays.asList(
                "local-floor", "bounded-floor", "hand-tracking", "anchors"
            ));
            
            return Result.success("WebXR支持检查完成", support);
        } catch (Exception e) {
            log.error("检查WebXR支持失败", e);
            return Result.error("检查WebXR支持失败: " + e.getMessage());
        }
    }

    /**
     * 获取VR教室状态
     */
    @GetMapping("/classrooms/{classroomId}/status")
    public Result<Object> getVRClassroomStatus(@PathVariable Long classroomId) {
        log.info("获取VR教室状态 - 教室ID: {}", classroomId);
        
        try {
            Map<String, Object> status = new HashMap<>();
            status.put("classroomId", classroomId);
            status.put("status", "active");
            status.put("currentUsers", 15);
            status.put("maxUsers", 25);
            status.put("isLessonActive", true);
            status.put("currentLesson", "数字信号处理 - 第3章");
            status.put("lessonProgress", 65);
            status.put("teacherOnline", true);
            status.put("recordingEnabled", true);
            status.put("environmentType", "university_classroom");
            
            return Result.success("获取VR教室状态成功", status);
        } catch (Exception e) {
            log.error("获取VR教室状态失败", e);
            return Result.error("获取VR教室状态失败: " + e.getMessage());
        }
    }

    /**
     * 举手发言
     */
    @PostMapping("/sessions/{sessionId}/raise-hand")
    public Result<Object> raiseHandInVR(@PathVariable Long sessionId) {
        log.info("VR教室举手发言 - 会话ID: {}", sessionId);
        
        try {
            Map<String, Object> result = new HashMap<>();
            result.put("sessionId", sessionId);
            result.put("handRaised", true);
            result.put("queuePosition", 2);
            result.put("estimatedWaitTime", "30秒");
            result.put("message", "您已举手，请等待老师回应");
            result.put("timestamp", new Date());
            
            return Result.success("举手成功", result);
        } catch (Exception e) {
            log.error("VR教室举手失败", e);
            return Result.error("举手失败: " + e.getMessage());
        }
    }

    /**
     * 提交VR问题
     */
    @PostMapping("/sessions/{sessionId}/questions")
    public Result<Object> submitVRQuestion(@PathVariable Long sessionId, 
                                           @RequestBody Map<String, Object> question) {
        log.info("提交VR问题 - 会话ID: {}, 问题: {}", sessionId, question);
        
        try {
            Map<String, Object> result = new HashMap<>();
            result.put("questionId", System.currentTimeMillis());
            result.put("sessionId", sessionId);
            result.put("question", question.get("question"));
            result.put("category", question.getOrDefault("category", "general"));
            result.put("priority", question.getOrDefault("priority", "normal"));
            result.put("status", "submitted");
            result.put("submitTime", new Date());
            result.put("estimatedResponseTime", "2-3分钟");
            
            return Result.success("问题提交成功", result);
        } catch (Exception e) {
            log.error("提交VR问题失败", e);
            return Result.error("提交问题失败: " + e.getMessage());
        }
    }

    /**
     * 保存VR笔记
     */
    @PostMapping("/sessions/{sessionId}/notes")
    public Result<Object> saveVRNote(@PathVariable Long sessionId, 
                                     @RequestBody Map<String, Object> note) {
        log.info("保存VR笔记 - 会话ID: {}, 笔记: {}", sessionId, note);
        
        try {
            Map<String, Object> result = new HashMap<>();
            result.put("noteId", System.currentTimeMillis());
            result.put("sessionId", sessionId);
            result.put("content", note.get("content"));
            result.put("timestamp", note.get("timestamp"));
            result.put("tags", note.getOrDefault("tags", Arrays.asList("VR课程")));
            result.put("saveTime", new Date());
            result.put("syncStatus", "synced");
            
            return Result.success("笔记保存成功", result);
        } catch (Exception e) {
            log.error("保存VR笔记失败", e);
            return Result.error("保存笔记失败: " + e.getMessage());
        }
    }

    /**
     * 获取VR学习数据
     */
    @GetMapping("/users/{userId}/learning-data")
    public Result<Object> getVRLearningData(@PathVariable Long userId,
                                            @RequestParam(defaultValue = "7") int days) {
        log.info("获取VR学习数据 - 用户ID: {}, 天数: {}", userId, days);
        
        try {
            Map<String, Object> learningData = new HashMap<>();
            learningData.put("userId", userId);
            learningData.put("totalVRTime", "25小时30分钟");
            learningData.put("completedCourses", 3);
            learningData.put("totalCourses", 8);
            learningData.put("averageScore", 87.5);
            learningData.put("learningStreak", 12);
            learningData.put("favoriteCourse", "数字信号处理");
            learningData.put("vrEfficiency", 92);
            
            // 每日学习数据
            List<Map<String, Object>> dailyData = new ArrayList<>();
            for (int i = days - 1; i >= 0; i--) {
                Map<String, Object> day = new HashMap<>();
                day.put("date", new Date(System.currentTimeMillis() - i * 24 * 60 * 60 * 1000L));
                day.put("vrTime", 30 + (int)(Math.random() * 60));
                day.put("coursesCompleted", (int)(Math.random() * 3));
                day.put("score", 80 + (int)(Math.random() * 20));
                dailyData.add(day);
            }
            learningData.put("dailyData", dailyData);
            
            return Result.success("获取VR学习数据成功", learningData);
        } catch (Exception e) {
            log.error("获取VR学习数据失败", e);
            return Result.error("获取VR学习数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取硬件拆解列表
     */
    @GetMapping("/hardware")
    public Result<Object> getHardwareList() {
        log.info("获取硬件拆解列表");
        
        try {
            List<Map<String, Object>> hardwareList = new ArrayList<>();
            
            // 服务器机箱
            Map<String, Object> server = new HashMap<>();
            server.put("id", 1);
            server.put("name", "服务器机箱");
            server.put("type", "server");
            server.put("description", "高性能服务器主机");
            server.put("icon", "fas fa-server");
            
            List<Map<String, Object>> serverSpecs = new ArrayList<>();
            serverSpecs.add(Map.of("name", "型号", "value", "Dell PowerEdge R720"));
            serverSpecs.add(Map.of("name", "处理器", "value", "Intel Xeon E5-2680 v2"));
            serverSpecs.add(Map.of("name", "内存", "value", "64GB DDR3"));
            serverSpecs.add(Map.of("name", "存储", "value", "2TB SAS HDD"));
            serverSpecs.add(Map.of("name", "电源", "value", "750W 冗余电源"));
            server.put("specs", serverSpecs);
            
            List<Map<String, Object>> serverPerformance = new ArrayList<>();
            serverPerformance.add(Map.of("name", "CPU使用率", "value", 65, "color", "#3498db"));
            serverPerformance.add(Map.of("name", "内存使用率", "value", 45, "color", "#e74c3c"));
            serverPerformance.add(Map.of("name", "存储使用率", "value", 78, "color", "#f39c12"));
            serverPerformance.add(Map.of("name", "网络负载", "value", 32, "color", "#2ecc71"));
            server.put("performance", serverPerformance);
            
            hardwareList.add(server);
            
            // CPU处理器
            Map<String, Object> cpu = new HashMap<>();
            cpu.put("id", 2);
            cpu.put("name", "CPU处理器");
            cpu.put("type", "cpu");
            cpu.put("description", "深度拆解CPU内部结构");
            cpu.put("icon", "fas fa-microchip");
            
            List<Map<String, Object>> cpuSpecs = new ArrayList<>();
            cpuSpecs.add(Map.of("name", "制程工艺", "value", "22nm"));
            cpuSpecs.add(Map.of("name", "晶体管数", "value", "26.4亿"));
            cpuSpecs.add(Map.of("name", "核心面积", "value", "315mm²"));
            cpuSpecs.add(Map.of("name", "工作温度", "value", "0-85°C"));
            cpu.put("specs", cpuSpecs);
            
            List<Map<String, Object>> cpuPerformance = new ArrayList<>();
            cpuPerformance.add(Map.of("name", "计算性能", "value", 85, "color", "#3498db"));
            cpuPerformance.add(Map.of("name", "缓存效率", "value", 92, "color", "#e74c3c"));
            cpuPerformance.add(Map.of("name", "能效比", "value", 78, "color", "#f39c12"));
            cpuPerformance.add(Map.of("name", "热管理", "value", 88, "color", "#2ecc71"));
            cpu.put("performance", cpuPerformance);
            
            hardwareList.add(cpu);
            
            // 显卡GPU
            Map<String, Object> gpu = new HashMap<>();
            gpu.put("id", 3);
            gpu.put("name", "显卡GPU");
            gpu.put("type", "gpu");
            gpu.put("description", "图形处理器内部结构");
            gpu.put("icon", "fas fa-tv");
            
            List<Map<String, Object>> gpuSpecs = new ArrayList<>();
            gpuSpecs.add(Map.of("name", "架构", "value", "Kepler"));
            gpuSpecs.add(Map.of("name", "制程", "value", "28nm"));
            gpuSpecs.add(Map.of("name", "显存带宽", "value", "480GB/s"));
            gpuSpecs.add(Map.of("name", "功耗", "value", "300W"));
            gpu.put("specs", gpuSpecs);
            
            List<Map<String, Object>> gpuPerformance = new ArrayList<>();
            gpuPerformance.add(Map.of("name", "渲染性能", "value", 90, "color", "#3498db"));
            gpuPerformance.add(Map.of("name", "计算性能", "value", 95, "color", "#e74c3c"));
            gpuPerformance.add(Map.of("name", "内存带宽", "value", 88, "color", "#f39c12"));
            gpuPerformance.add(Map.of("name", "能效", "value", 75, "color", "#2ecc71"));
            gpu.put("performance", gpuPerformance);
            
            hardwareList.add(gpu);
            
            return Result.success("获取硬件列表成功", hardwareList);
        } catch (Exception e) {
            log.error("获取硬件列表失败", e);
            return Result.error("获取硬件列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取硬件详细信息
     */
    @GetMapping("/hardware/{hardwareId}")
    public Result<Object> getHardwareDetail(@PathVariable Long hardwareId) {
        log.info("获取硬件详细信息 - 硬件ID: {}", hardwareId);
        
        try {
            Map<String, Object> hardwareDetail = new HashMap<>();
            
            if (hardwareId == 1) {
                // 服务器机箱详细信息
                hardwareDetail.put("id", 1);
                hardwareDetail.put("name", "服务器机箱");
                hardwareDetail.put("type", "server");
                hardwareDetail.put("description", "高性能服务器主机完整拆解");
                
                List<Map<String, Object>> components = new ArrayList<>();
                
                // 主板
                Map<String, Object> motherboard = new HashMap<>();
                motherboard.put("id", 1);
                motherboard.put("name", "主板");
                motherboard.put("type", "motherboard");
                motherboard.put("function", "连接所有硬件组件的主要电路板");
                motherboard.put("icon", "fas fa-microchip");
                motherboard.put("status", "active");
                motherboard.put("description", "主板是计算机的核心组件，负责连接CPU、内存、存储设备和扩展卡。");
                components.add(motherboard);
                
                // CPU
                Map<String, Object> cpuComponent = new HashMap<>();
                cpuComponent.put("id", 2);
                cpuComponent.put("name", "CPU处理器");
                cpuComponent.put("type", "cpu");
                cpuComponent.put("function", "执行计算任务的核心处理单元");
                cpuComponent.put("icon", "fas fa-microchip");
                cpuComponent.put("status", "active");
                cpuComponent.put("description", "Intel Xeon E5-2680 v2是一款高性能服务器处理器，适用于数据中心和高性能计算。");
                components.add(cpuComponent);
                
                // 内存
                Map<String, Object> ram = new HashMap<>();
                ram.put("id", 3);
                ram.put("name", "内存条");
                ram.put("type", "ram");
                ram.put("function", "临时存储数据和程序的高速存储器");
                ram.put("icon", "fas fa-memory");
                ram.put("status", "active");
                ram.put("description", "DDR3内存条提供高速数据访问，确保系统流畅运行。");
                components.add(ram);
                
                // 显卡
                Map<String, Object> gpuComponent = new HashMap<>();
                gpuComponent.put("id", 4);
                gpuComponent.put("name", "显卡");
                gpuComponent.put("type", "gpu");
                gpuComponent.put("function", "处理图形渲染和并行计算任务");
                gpuComponent.put("icon", "fas fa-tv");
                gpuComponent.put("status", "active");
                gpuComponent.put("description", "NVIDIA Tesla K80专为高性能计算和深度学习设计。");
                components.add(gpuComponent);
                
                // 硬盘
                Map<String, Object> storage = new HashMap<>();
                storage.put("id", 5);
                storage.put("name", "硬盘");
                storage.put("type", "storage");
                storage.put("function", "永久存储数据和程序的存储设备");
                storage.put("icon", "fas fa-hdd");
                storage.put("status", "active");
                storage.put("description", "SAS硬盘提供高性能和可靠性，适用于企业级应用。");
                components.add(storage);
                
                // 电源
                Map<String, Object> power = new HashMap<>();
                power.put("id", 6);
                power.put("name", "电源");
                power.put("type", "power");
                power.put("function", "为整个系统提供稳定的电力供应");
                power.put("icon", "fas fa-plug");
                power.put("status", "active");
                power.put("description", "冗余电源确保系统在单个电源故障时仍能正常运行。");
                components.add(power);
                
                hardwareDetail.put("components", components);
            }
            
            return Result.success("获取硬件详细信息成功", hardwareDetail);
        } catch (Exception e) {
            log.error("获取硬件详细信息失败", e);
            return Result.error("获取硬件详细信息失败: " + e.getMessage());
        }
    }

    /**
     * 更新硬件拆解状态
     */
    @PostMapping("/hardware/{hardwareId}/explode")
    public Result<Object> explodeHardware(@PathVariable Long hardwareId, 
                                          @RequestBody Map<String, Object> explodeData) {
        log.info("更新硬件拆解状态 - 硬件ID: {}, 数据: {}", hardwareId, explodeData);
        
        try {
            Map<String, Object> result = new HashMap<>();
            result.put("hardwareId", hardwareId);
            result.put("exploded", explodeData.get("exploded"));
            result.put("components", explodeData.get("components"));
            result.put("timestamp", new Date());
            result.put("message", "硬件拆解状态更新成功");
            
            return Result.success("硬件拆解状态更新成功", result);
        } catch (Exception e) {
            log.error("更新硬件拆解状态失败", e);
            return Result.error("更新硬件拆解状态失败: " + e.getMessage());
        }
    }

    // 辅助方法

    private Map<String, Object> createVRCourse(int id, String title, String category, 
                                               String description, String difficulty, int duration) {
        Map<String, Object> course = new HashMap<>();
        course.put("id", id);
        course.put("title", title);
        course.put("category", category);
        course.put("description", description);
        course.put("difficulty", difficulty);
        course.put("duration", duration + "分钟");
        course.put("rating", 4.5 + Math.random() * 0.5);
        course.put("enrollments", 1000 + (int)(Math.random() * 5000));
        course.put("vrEnvironment", "classroom_3d");
        course.put("hasInteractiveElements", true);
        course.put("supports3D", true);
        // 移除placeholder图片路径
        return course;
    }

    private Map<String, Object> createDetailedVRCourse(Long courseId) {
        Map<String, Object> course = createVRCourse(courseId.intValue(), 
            "数字信号处理基础", "DSP", "学习数字信号处理的基本概念和算法", "intermediate", 180);
        
        // 添加详细信息
        course.put("instructor", "李教授");
        // 移除placeholder图片路径
        course.put("syllabus", Arrays.asList(
            "第1章：信号的基本概念",
            "第2章：数字信号的特点", 
            "第3章：信号处理的基本流程",
            "第4章：常见的信号处理算法",
            "第5章：实际应用案例"
        ));
        course.put("vrFeatures", Arrays.asList(
            "3D信号可视化",
            "交互式实验",
            "虚拟实验室",
            "实时协作"
        ));
        course.put("prerequisites", Arrays.asList("高等数学", "线性代数"));
        course.put("learningOutcomes", Arrays.asList(
            "掌握数字信号处理基本概念",
            "能够设计简单的数字滤波器",
            "理解频域分析方法"
        ));
        
        return course;
    }

    private List<Map<String, Object>> generateOnlineUsers() {
        List<Map<String, Object>> users = new ArrayList<>();
        String[] names = {"张同学", "王同学", "李同学", "赵同学", "陈同学"};
        
        for (int i = 0; i < 5; i++) {
            Map<String, Object> user = new HashMap<>();
            user.put("userId", i + 100);
            user.put("username", names[i]);
            user.put("avatar", "data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNDAiIGhlaWdodD0iNDAiIHZpZXdCb3g9IjAgMCA0MCA0MCIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPGNpcmNsZSBjeD0iMjAiIGN5PSIyMCIgcj0iMjAiIGZpbGw9IiMzNDk4ZGIiLz4KPHN2ZyB3aWR0aD0iMjQiIGhlaWdodD0iMjQiIHZpZXdCb3g9IjAgMCAyNCAyNCIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB4PSI4IiB5PSI4Ij4KPHBhdGggZD0iTTEyIDEyQzE0LjIwOTEgMTIgMTYgMTAuMjA5MSAxNiA4QzE2IDUuNzkwODYgMTQuMjA5MSA0IDEyIDRDOS43OTA4NiA0IDggNS43OTA4NiA4IDhDOCAxMC4yMDkxIDkuNzkwODYgMTIgMTIgMTJaIiBmaWxsPSJ3aGl0ZSIvPgo8cGF0aCBkPSJNMTIgMTRDOC42ODYyOSAxNCA2IDE2LjY4NjMgNiAyMFYyMkgxOFYyMEMxOCAxNi42ODYzIDE1LjMxMzcgMTQgMTIgMTRaIiBmaWxsPSJ3aGl0ZSIvPgo8L3N2Zz4KPC9zdmc+");
            user.put("seatNumber", i + 1);
            user.put("status", "online");
            user.put("joinTime", new Date(System.currentTimeMillis() - i * 60000));
            users.add(user);
        }
        
        return users;
    }

    private int generateRandomSeat() {
        return 1 + (int)(Math.random() * 20);
    }

    private Map<String, Object> createMessage(int id, String username, String text, int minutesAgo) {
        Map<String, Object> message = new HashMap<>();
        message.put("id", id);
        message.put("username", username);
        message.put("text", text);
        message.put("timestamp", new Date(System.currentTimeMillis() - minutesAgo * 60000));
        message.put("avatar", "data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNDAiIGhlaWdodD0iNDAiIHZpZXdCb3g9IjAgMCA0MCA0MCIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPGNpcmNsZSBjeD0iMjAiIGN5PSIyMCIgcj0iMjAiIGZpbGw9IiMzNDk4ZGIiLz4KPHN2ZyB3aWR0aD0iMjQiIGhlaWdodD0iMjQiIHZpZXdCb3g9IjAgMCAyNCAyNCIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB4PSI4IiB5PSI4Ij4KPHBhdGggZD0iTTEyIDEyQzE0LjIwOTEgMTIgMTYgMTAuMjA5MSAxNiA4QzE2IDUuNzkwODYgMTQuMjA5MSA0IDEyIDRDOS43OTA4NiA0IDggNS43OTA4NiA4IDhDOCAxMC4yMDkxIDkuNzkwODYgMTIgMTIgMTJaIiBmaWxsPSJ3aGl0ZSIvPgo8cGF0aCBkPSJNMTIgMTRDOC42ODYyOSAxNCA2IDE2LjY4NjMgNiAyMFYyMkgxOFYyMEMxOCAxNi42ODYzIDE1LjMxMzcgMTQgMTIgMTRaIiBmaWxsPSJ3aGl0ZSIvPgo8L3N2Zz4KPC9zdmc+");
        message.put("isOwn", false);
        return message;
    }

    private int calculateLearningPoints(Map<String, Object> progress) {
        int basePoints = 10;
        Object progressValue = progress.get("progress");
        if (progressValue instanceof Number) {
            double progressPercent = ((Number) progressValue).doubleValue();
            return (int)(basePoints * (progressPercent / 100.0));
        }
        return basePoints;
    }
} 