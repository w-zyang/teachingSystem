package com.experiment.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xslf.usermodel.*;
import org.apache.poi.hslf.usermodel.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.List;

/**
 * PPT文档读取工具类
 * 支持 .ppt 和 .pptx 格式
 */
@Slf4j
public class PPTDocumentReader {

    /**
     * 从URL读取PPT文档内容（纯文本，按页分隔）
     */
    public static String readPPTDocument(String fileUrl) {
        try {
            log.info("开始读取PPT文档: {}", fileUrl);
            InputStream inputStream = openStream(fileUrl);
            if (inputStream == null) return "";

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
     * 按页读取PPT，返回结构化的幻灯片数据列表（文本）。
     * 每个 Map 包含：pageNum / title / content
     */
    public static List<Map<String, String>> readSlidesStructured(String fileUrl) {
        try {
            log.info("开始按页读取PPT幻灯片: {}", fileUrl);
            InputStream inputStream = openStream(fileUrl);
            if (inputStream == null) return Collections.emptyList();

            List<Map<String, String>> slides;
            if (fileUrl.toLowerCase().endsWith(".pptx")) {
                slides = readPptxStructured(inputStream);
            } else if (fileUrl.toLowerCase().endsWith(".ppt")) {
                slides = readPptStructured(inputStream);
            } else {
                log.warn("不支持的文件格式: {}", fileUrl);
                return Collections.emptyList();
            }

            inputStream.close();
            log.info("结构化读取完成，共 {} 页", slides.size());
            return slides;

        } catch (Exception e) {
            log.error("结构化读取PPT失败: {}", fileUrl, e);
            return Collections.emptyList();
        }
    }

    /**
     * 将每页PPT幻灯片渲染为PNG图片，保存到 outputDir 目录下。
     *
     * @param fileUrl   PPT 文件 URL（OSS URL 或本地路径）
     * @param outputDir 图片输出目录（绝对路径）
     * @param scale     缩放倍数（建议 1.5，即在 960×720 基础上放大 1.5 倍）
     * @return 按页码顺序排列的图片文件名列表（如 ["slide_1.png", ...]）
     */
    public static List<String> renderSlidesToImages(String fileUrl, String outputDir, float scale) {
        List<String> imageFiles = new ArrayList<>();
        try {
            // 确保无头模式（服务器环境）
            System.setProperty("java.awt.headless", "true");

            log.info("开始渲染PPT为图片: {}", fileUrl);
            InputStream inputStream = openStream(fileUrl);
            if (inputStream == null) return imageFiles;

            // 确保输出目录存在
            File dir = new File(outputDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            if (fileUrl.toLowerCase().endsWith(".pptx")) {
                imageFiles = renderPptxToImages(inputStream, dir, scale);
            } else if (fileUrl.toLowerCase().endsWith(".ppt")) {
                imageFiles = renderPptToImages(inputStream, dir, scale);
            } else {
                log.warn("不支持渲染的文件格式: {}", fileUrl);
            }

            inputStream.close();
            log.info("PPT渲染完成，共生成 {} 张图片", imageFiles.size());

        } catch (Exception e) {
            log.error("渲染PPT为图片失败: {}", fileUrl, e);
        }
        return imageFiles;
    }

    // ===================== 私有辅助方法 =====================

    private static InputStream openStream(String fileUrl) {
        try {
            if (fileUrl.startsWith("http://") || fileUrl.startsWith("https://")) {
                return new URL(fileUrl).openStream();
            } else if (fileUrl.startsWith("blob:")) {
                log.warn("无法读取Blob URL");
                return null;
            } else {
                return new FileInputStream(fileUrl);
            }
        } catch (Exception e) {
            log.error("打开文件流失败: {}", fileUrl, e);
            return null;
        }
    }

    private static String readPptx(InputStream inputStream) throws Exception {
        XMLSlideShow ppt = new XMLSlideShow(inputStream);
        StringBuilder content = new StringBuilder();
        List<XSLFSlide> slides = ppt.getSlides();
        log.info("PPT共有 {} 页", slides.size());
        for (int i = 0; i < slides.size(); i++) {
            content.append("\n========== 第 ").append(i + 1).append(" 页 ==========\n");
            for (XSLFShape shape : slides.get(i).getShapes()) {
                if (shape instanceof XSLFTextShape) {
                    String text = ((XSLFTextShape) shape).getText();
                    if (text != null && !text.trim().isEmpty()) {
                        content.append(text).append("\n");
                    }
                }
            }
        }
        ppt.close();
        return content.toString();
    }

    private static String readPpt(InputStream inputStream) throws Exception {
        HSLFSlideShow ppt = new HSLFSlideShow(inputStream);
        StringBuilder content = new StringBuilder();
        List<HSLFSlide> slides = ppt.getSlides();
        log.info("PPT共有 {} 页", slides.size());
        for (int i = 0; i < slides.size(); i++) {
            content.append("\n========== 第 ").append(i + 1).append(" 页 ==========\n");
            for (HSLFShape shape : slides.get(i).getShapes()) {
                if (shape instanceof HSLFTextShape) {
                    String text = ((HSLFTextShape) shape).getText();
                    if (text != null && !text.trim().isEmpty()) {
                        content.append(text).append("\n");
                    }
                }
            }
        }
        ppt.close();
        return content.toString();
    }

    private static List<Map<String, String>> readPptxStructured(InputStream inputStream) throws Exception {
        XMLSlideShow ppt = new XMLSlideShow(inputStream);
        List<Map<String, String>> result = new ArrayList<>();
        List<XSLFSlide> slides = ppt.getSlides();
        for (int i = 0; i < slides.size(); i++) {
            List<String> texts = new ArrayList<>();
            for (XSLFShape shape : slides.get(i).getShapes()) {
                if (shape instanceof XSLFTextShape) {
                    String text = ((XSLFTextShape) shape).getText();
                    if (text != null && !text.trim().isEmpty()) texts.add(text.trim());
                }
            }
            Map<String, String> slideMap = new LinkedHashMap<>();
            slideMap.put("pageNum", String.valueOf(i + 1));
            slideMap.put("title", texts.isEmpty() ? "" : texts.get(0));
            slideMap.put("content", texts.size() > 1 ? String.join("\n", texts.subList(1, texts.size())) : "");
            result.add(slideMap);
        }
        ppt.close();
        return result;
    }

    private static List<Map<String, String>> readPptStructured(InputStream inputStream) throws Exception {
        HSLFSlideShow ppt = new HSLFSlideShow(inputStream);
        List<Map<String, String>> result = new ArrayList<>();
        List<HSLFSlide> slides = ppt.getSlides();
        for (int i = 0; i < slides.size(); i++) {
            List<String> texts = new ArrayList<>();
            for (HSLFShape shape : slides.get(i).getShapes()) {
                if (shape instanceof HSLFTextShape) {
                    String text = ((HSLFTextShape) shape).getText();
                    if (text != null && !text.trim().isEmpty()) texts.add(text.trim());
                }
            }
            Map<String, String> slideMap = new LinkedHashMap<>();
            slideMap.put("pageNum", String.valueOf(i + 1));
            slideMap.put("title", texts.isEmpty() ? "" : texts.get(0));
            slideMap.put("content", texts.size() > 1 ? String.join("\n", texts.subList(1, texts.size())) : "");
            result.add(slideMap);
        }
        ppt.close();
        return result;
    }

    /** 渲染 .pptx 每页为 PNG 图片 */
    private static List<String> renderPptxToImages(InputStream inputStream, File outputDir, float scale) throws Exception {
        List<String> fileNames = new ArrayList<>();
        XMLSlideShow ppt = new XMLSlideShow(inputStream);
        Dimension pgSize = ppt.getPageSize();
        List<XSLFSlide> slides = ppt.getSlides();

        int imgWidth  = (int) (pgSize.getWidth()  * scale);
        int imgHeight = (int) (pgSize.getHeight() * scale);

        for (int i = 0; i < slides.size(); i++) {
            BufferedImage img = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = img.createGraphics();

            // 抗锯齿等渲染提示
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,     RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING,        RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,    RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // 白色背景
            g2d.setPaint(Color.WHITE);
            g2d.fill(new Rectangle2D.Float(0, 0, imgWidth, imgHeight));

            // 缩放后绘制
            g2d.scale(scale, scale);
            slides.get(i).draw(g2d);
            g2d.dispose();

            String fileName = "slide_" + (i + 1) + ".png";
            File outputFile = new File(outputDir, fileName);
            ImageIO.write(img, "PNG", outputFile);
            fileNames.add(fileName);
            log.info("渲染第 {} 页完成: {}", i + 1, outputFile.getAbsolutePath());
        }

        ppt.close();
        return fileNames;
    }

    /** 渲染 .ppt 每页为 PNG 图片 */
    private static List<String> renderPptToImages(InputStream inputStream, File outputDir, float scale) throws Exception {
        List<String> fileNames = new ArrayList<>();
        HSLFSlideShow ppt = new HSLFSlideShow(inputStream);
        Dimension pgSize = ppt.getPageSize();
        List<HSLFSlide> slides = ppt.getSlides();

        int imgWidth  = (int) (pgSize.getWidth()  * scale);
        int imgHeight = (int) (pgSize.getHeight() * scale);

        for (int i = 0; i < slides.size(); i++) {
            BufferedImage img = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = img.createGraphics();

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,     RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING,        RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,    RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            g2d.setPaint(Color.WHITE);
            g2d.fill(new Rectangle2D.Float(0, 0, imgWidth, imgHeight));
            g2d.scale(scale, scale);
            slides.get(i).draw(g2d);
            g2d.dispose();

            String fileName = "slide_" + (i + 1) + ".png";
            File outputFile = new File(outputDir, fileName);
            ImageIO.write(img, "PNG", outputFile);
            fileNames.add(fileName);
            log.info("渲染第 {} 页完成: {}", i + 1, outputFile.getAbsolutePath());
        }

        ppt.close();
        return fileNames;
    }
}
