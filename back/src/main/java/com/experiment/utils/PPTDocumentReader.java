package com.experiment.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xslf.usermodel.*;
import org.apache.poi.hslf.usermodel.*;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

/**
 * PPT文档读取工具类
 * 支持 .ppt 和 .pptx 格式
 */
@Slf4j
public class PPTDocumentReader {

    /**
     * 从URL读取PPT文档内容
     * @param fileUrl 文件URL（可以是本地文件路径或网络URL）
     * @return 文档文本内容
     */
    public static String readPPTDocument(String fileUrl) {
        try {
            log.info("开始读取PPT文档: {}", fileUrl);
            
            InputStream inputStream;
            
            // 判断是本地文件还是网络URL
            if (fileUrl.startsWith("http://") || fileUrl.startsWith("https://")) {
                URL url = new URL(fileUrl);
                inputStream = url.openStream();
            } else if (fileUrl.startsWith("blob:")) {
                log.warn("无法读取Blob URL");
                return "";
            } else {
                inputStream = new FileInputStream(fileUrl);
            }
            
            // 根据文件扩展名判断格式
            String content;
            if (fileUrl.toLowerCase().endsWith(".pptx")) {
                content = readPptx(inputStream);
            } else if (fileUrl.toLowerCase().endsWith(".ppt")) {
                content = readPpt(inputStream);
            } else {
                log.warn("不支持的文件格式: {}", fileUrl);
                return "";
            }
            
            inputStream.close();
            log.info("PPT文档读取成功，内容长度: {}", content.length());
            return content;
            
        } catch (Exception e) {
            log.error("读取PPT文档失败: {}", fileUrl, e);
            return "";
        }
    }

    /**
     * 读取 .pptx 格式文档
     */
    private static String readPptx(InputStream inputStream) throws Exception {
        XMLSlideShow ppt = new XMLSlideShow(inputStream);
        StringBuilder content = new StringBuilder();
        
        List<XSLFSlide> slides = ppt.getSlides();
        log.info("PPT共有 {} 页", slides.size());
        
        for (int i = 0; i < slides.size(); i++) {
            XSLFSlide slide = slides.get(i);
            content.append("\n========== 第 ").append(i + 1).append(" 页 ==========\n");
            
            // 读取幻灯片中的所有形状
            for (XSLFShape shape : slide.getShapes()) {
                if (shape instanceof XSLFTextShape) {
                    XSLFTextShape textShape = (XSLFTextShape) shape;
                    String text = textShape.getText();
                    if (text != null && !text.trim().isEmpty()) {
                        content.append(text).append("\n");
                    }
                }
            }
        }
        
        ppt.close();
        return content.toString();
    }

    /**
     * 读取 .ppt 格式文档
     */
    private static String readPpt(InputStream inputStream) throws Exception {
        HSLFSlideShow ppt = new HSLFSlideShow(inputStream);
        StringBuilder content = new StringBuilder();
        
        List<HSLFSlide> slides = ppt.getSlides();
        log.info("PPT共有 {} 页", slides.size());
        
        for (int i = 0; i < slides.size(); i++) {
            HSLFSlide slide = slides.get(i);
            content.append("\n========== 第 ").append(i + 1).append(" 页 ==========\n");
            
            // 读取幻灯片中的所有形状
            for (HSLFShape shape : slide.getShapes()) {
                if (shape instanceof HSLFTextShape) {
                    HSLFTextShape textShape = (HSLFTextShape) shape;
                    String text = textShape.getText();
                    if (text != null && !text.trim().isEmpty()) {
                        content.append(text).append("\n");
                    }
                }
            }
        }
        
        ppt.close();
        return content.toString();
    }
}

