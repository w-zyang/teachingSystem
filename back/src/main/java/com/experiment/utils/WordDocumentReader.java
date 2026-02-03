package com.experiment.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

@Slf4j
public class WordDocumentReader {

    public static String readWordDocument(String fileUrl) {
        try {
            log.info("Reading Word document: {}", fileUrl);
            
            InputStream inputStream;
            
            if (fileUrl.startsWith("http://") || fileUrl.startsWith("https://")) {
                URL url = new URL(fileUrl);
                inputStream = url.openStream();
            } else if (fileUrl.startsWith("blob:")) {
                log.warn("Cannot read Blob URL");
                return "";
            } else {
                inputStream = new FileInputStream(fileUrl);
            }
            
            String content;
            if (fileUrl.toLowerCase().endsWith(".docx")) {
                content = readDocx(inputStream);
            } else if (fileUrl.toLowerCase().endsWith(".doc")) {
                content = readDoc(inputStream);
            } else if (fileUrl.toLowerCase().endsWith(".txt")) {
                content = readText(inputStream);
            } else {
                log.warn("Unsupported file format: {}", fileUrl);
                return "";
            }
            
            inputStream.close();
            log.info("Document read successfully, length: {}", content.length());
            return content;
            
        } catch (Exception e) {
            log.error("Failed to read Word document: {}", fileUrl, e);
            return "";
        }
    }

    private static String readDocx(InputStream inputStream) throws Exception {
        XWPFDocument document = new XWPFDocument(inputStream);
        XWPFWordExtractor extractor = new XWPFWordExtractor(document);
        String text = extractor.getText();
        extractor.close();
        document.close();
        return text;
    }

    private static String readDoc(InputStream inputStream) throws Exception {
        HWPFDocument document = new HWPFDocument(inputStream);
        WordExtractor extractor = new WordExtractor(document);
        String text = extractor.getText();
        extractor.close();
        document.close();
        return text;
    }

    private static String readText(InputStream inputStream) throws Exception {
        StringBuilder content = new StringBuilder();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            content.append(new String(buffer, 0, bytesRead, "UTF-8"));
        }
        return content.toString();
    }
}

