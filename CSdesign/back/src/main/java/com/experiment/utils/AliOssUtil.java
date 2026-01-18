package com.experiment.utils;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import java.io.ByteArrayInputStream;

@Data
@AllArgsConstructor
@Slf4j
public class AliOssUtil {

    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;

    /**
     * 文件上传（默认公共读）
     *
     * @param bytes
     * @param objectName
     * @return
     */
    public String upload(byte[] bytes, String objectName) {
        return upload(bytes, objectName, true);
    }

    /**
     * 文件上传
     *
     * @param bytes 文件字节数组
     * @param objectName OSS对象名称
     * @param publicRead 是否设置为公共读（true=公共读，false=私有）
     * @return 文件访问URL
     */
    public String upload(byte[] bytes, String objectName, boolean publicRead) {

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            // 创建PutObjectRequest请求
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName, 
                    objectName, 
                    new ByteArrayInputStream(bytes)
            );
            
            // 如果设置为公共读，则设置ACL
            if (publicRead) {
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setObjectAcl(CannedAccessControlList.PublicRead);
                putObjectRequest.setMetadata(metadata);
                log.info("设置文件ACL为公共读: {}", objectName);
            }
            
            // 执行上传
            ossClient.putObject(putObjectRequest);
            log.info("文件上传成功: {}", objectName);
            
        } catch (OSSException oe) {
            log.error("OSS异常 - Error Message: {}, Error Code: {}, Request ID: {}, Host ID: {}", 
                    oe.getErrorMessage(), oe.getErrorCode(), oe.getRequestId(), oe.getHostId());
            throw new RuntimeException("OSS上传失败: " + oe.getErrorMessage(), oe);
        } catch (ClientException ce) {
            log.error("OSS客户端异常 - Error Message: {}", ce.getMessage());
            throw new RuntimeException("OSS客户端异常: " + ce.getMessage(), ce);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }

        //文件访问路径规则 https://BucketName.Endpoint/ObjectName
        StringBuilder stringBuilder = new StringBuilder("https://");
        stringBuilder
                .append(bucketName)
                .append(".")
                .append(endpoint)
                .append("/")
                .append(objectName);

        log.info("文件访问URL: {}", stringBuilder.toString());

        return stringBuilder.toString();
    }
}