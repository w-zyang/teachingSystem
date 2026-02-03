package com.experiment.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class ApiAuthAlgorithm {

    /**
     * 获取签名
     *
     * @param appId     应用ID
     * @param apiSecret API密钥
     * @param ts        时间戳
     * @return 生成的签名
     */
    public String getSignature(String appId, String apiSecret, long ts) {
        try {
            String auth = md5(appId + ts);
            return hmacSHA1Encrypt(auth, apiSecret);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * HMAC SHA1 加密
     *
     * @param encryptText 要加密的文本
     * @param encryptKey  加密密钥
     * @return 加密后的字符串
     */
    private String hmacSHA1Encrypt(String encryptText, String encryptKey)
            throws NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec keySpec = new SecretKeySpec(
                encryptKey.getBytes(StandardCharsets.UTF_8), "HmacSHA1");

        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(keySpec);
        byte[] result = mac.doFinal(encryptText.getBytes(StandardCharsets.UTF_8));

        return Base64.getEncoder().encodeToString(result);
    }

    /**
     * 生成MD5哈希
     *
     * @param text 要哈希的文本
     * @return MD5哈希值
     */
    private String md5(String text) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(text.getBytes(StandardCharsets.UTF_8));

        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
