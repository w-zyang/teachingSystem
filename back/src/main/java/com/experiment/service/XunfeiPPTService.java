package com.experiment.service;

import java.util.Map;

/**
 * 讯飞PPT生成服务接口
 */
public interface XunfeiPPTService {
    
    /**
     * 生成PPT
     * @param content PPT内容
     * @param title PPT标题
     * @return PPT文件下载URL
     */
    String generatePPT(String content, String title);
    
    /**
     * 检查PPT生成状态
     * @param taskId 任务ID
     * @return 任务状态和下载链接
     */
    Map<String, Object> checkPPTStatus(String taskId);
}
