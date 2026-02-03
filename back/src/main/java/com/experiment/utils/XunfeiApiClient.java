package com.experiment.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
public class XunfeiApiClient {

    private static final String MEDIA_TYPE_JSON = "application/json; charset=utf-8";
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectionPool(new ConnectionPool(100, 5, TimeUnit.MINUTES))
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .build();

    private final String baseUrl;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public XunfeiApiClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    /**
     * 创建PPT
     */
    public String createPPT(String appId, String timestamp, String signature, String query) throws IOException {
        validateParameters(appId, timestamp, signature, query);

        // 构建请求体
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("query", query);
        builder.addFormDataPart("language", "cn");
        builder.addFormDataPart("isFigure", "false");
        builder.addFormDataPart("isCardNote", "false");
        builder.addFormDataPart("search", "false");
        builder.addFormDataPart("author", "智文");
        
        RequestBody body = builder.build();

        Request request = new Request.Builder()
                .url(baseUrl)
                .addHeader("appId", appId)
                .addHeader("timestamp", timestamp)
                .addHeader("signature", signature)
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .post(body)
                .build();

        log.info("发送讯飞PPT创建请求: appId={}, timestamp={}, signature={}", appId, timestamp, signature);
        return executeRequest(request);
    }

    /**
     * 查询PPT进度
     */
    public String checkProgress(String appId, String timestamp, String signature, String sid) throws IOException {
        validateParameters(appId, timestamp, signature, sid);

        String progressUrl = baseUrl.replace("/create", "/progress");
        HttpUrl url = HttpUrl.parse(progressUrl).newBuilder()
                .addQueryParameter("sid", sid)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("appId", appId)
                .addHeader("timestamp", timestamp)
                .addHeader("signature", signature)
                .get()
                .build();

        log.info("查询PPT进度: sid={}", sid);
        return executeRequest(request);
    }

    /**
     * 执行请求
     */
    private String executeRequest(Request request) throws IOException {
        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            
            if (!response.isSuccessful()) {
                log.error("请求失败: code={}, body={}", response.code(), responseBody);
                throw new IOException("请求失败: " + response.code() + " - " + responseBody);
            }
            
            log.info("请求成功: {}", responseBody);
            return responseBody;
        }
    }

    /**
     * 验证参数
     */
    private void validateParameters(String... params) {
        for (String param : params) {
            if (param == null || param.isEmpty()) {
                throw new IllegalArgumentException("参数不能为空");
            }
        }
    }
}
