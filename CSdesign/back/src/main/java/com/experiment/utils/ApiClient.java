package com.experiment.utils;

import com.alibaba.fastjson.JSONObject;
import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ApiClient {

    private static final String MEDIA_TYPE_JSON = "application/json; charset=utf-8";
    private final static OkHttpClient client = new OkHttpClient().newBuilder()
            .connectionPool(new ConnectionPool(100, 5, TimeUnit.MINUTES))
            .readTimeout(60 * 10, TimeUnit.SECONDS)
            .build();
    private static final String ERROR_MESSAGE = "Unexpected code: ";
    private final String baseUrl;

    public ApiClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getTemplateList(String appId, String timestamp, String signature) throws IOException {
        validateParameters(appId, timestamp, signature);

        //注意：请求体不能为空，至少有一项
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("style", "卡通");
        jsonObject.put("pageSize", "10");
        RequestBody body = RequestBody.create(jsonObject.toString(), MediaType.get(MEDIA_TYPE_JSON));

        Request request = buildPostRequest(baseUrl + "/template/list", appId, timestamp, signature, body);
        return executeRequest(request);
    }

    public String createOutline(String appId, String timestamp, String signature, String query) throws IOException {
        validateParameters(appId, timestamp, signature, query);

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("query", query);
        RequestBody body = builder.build();

        Request request = buildPostRequest(baseUrl + "/createOutline", appId, timestamp, signature, body);
        return executeRequest(request);
    }

    public String createOutlineByDoc(String appId, String timestamp, String signature, File file) throws IOException {
        validateParameters(appId, timestamp, signature);

        MultipartBody.Builder body = new MultipartBody.Builder().setType(MultipartBody.FORM);
        try {
            body.addFormDataPart("file", file.getName(), RequestBody.create(null, file));
            body.addFormDataPart("fileName", file.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        RequestBody requestBody = body.build();

        Request request = new Request.Builder()
                .url(baseUrl + "/createOutlineByDoc")
                .addHeader("appId", appId)
                .addHeader("timestamp", timestamp)
                .addHeader("signature", signature)
                //.addHeader("Content-Type", "multipart/form-data")
                .post(requestBody)
                .build();
        return executeRequest(request);
    }

    public String create(String appId, String timestamp, String signature,File file) throws IOException {
        validateParameters(appId, timestamp, signature);

        MultipartBody.Builder builder = new MultipartBody.Builder();

        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("file", file.getName(),
                RequestBody.create(MediaType.parse("multipart/form-data"), file));
        builder.addFormDataPart("fileName", file.getName());
        RequestBody body = builder.build();

        Request request = buildPostRequest(baseUrl + "/create", appId, timestamp, signature, body);
        return executeRequest(request);
    }


    public String createPptByOutline(String appId, String ts, String signature,String outline) throws IOException {
        validateParameters(appId, ts, signature);
        JSONObject jsonObject = new JSONObject();

        JSONObject outlineJson = JSONObject.parseObject(outline);
        jsonObject.put("outline", outlineJson);
        jsonObject.put("query", "test");
        RequestBody body = RequestBody.create(jsonObject.toString(), MediaType.get(MEDIA_TYPE_JSON));

        Request request = buildPostRequest(baseUrl + "/createPptByOutline", appId, ts, signature, body);
        return executeRequest(request);
    }

    public String checkProgress(String appId, String timestamp, String signature, String sid) throws IOException {
        validateParameters(appId, timestamp, signature, sid);

        HttpUrl url = HttpUrl.parse(baseUrl+"/progress").newBuilder()
                .addQueryParameter("sid", sid)
                .build();

        Request request = buildGetRequest(url.toString(), appId, timestamp, signature);
        return executeRequest(request);
    }


    private Request buildPostRequest(String url, String appId, String timestamp, String signature, RequestBody body) {
        return new Request.Builder()
                .url(url)
                .addHeader("appId", appId)
                .addHeader("timestamp", timestamp)
                .addHeader("signature", signature)
                .post(body)
                .build();
    }

    private Request buildGetRequest(String url, String appId, String timestamp, String signature) {
        return new Request.Builder()
                .url(url)
                .addHeader("appId", appId)
                .addHeader("timestamp", timestamp)
                .addHeader("signature", signature)
                .get()
                .build();
    }

    private String executeRequest(Request request) throws IOException {
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.out.println(response.body().string());
                throw new IOException(ERROR_MESSAGE + response);
            }
            return response.body().string();
        }
    }

    private void validateParameters(String... params) {
        for (String param : params) {
            if (param == null || param.isEmpty()) {
                throw new IllegalArgumentException("Parameter cannot be null or empty");
            }
        }
    }

} 