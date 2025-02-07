package com.sky.utils;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;

@Data
@Slf4j
public class AliOssUtil {

    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;

    private OSS ossClient;

    public AliOssUtil(String endpoint, String accessKeyId, String accessKeySecret, String bucketName) {
        this.endpoint = endpoint;
        this.accessKeyId = accessKeyId;
        this.accessKeySecret = accessKeySecret;
        this.bucketName = bucketName;
        ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }

    /**
     * 文件上传
     *
     * @param bytes      文件字节数组
     * @param objectName 文件名
     * @return String
     */
    public String upload(byte[] bytes, String objectName) {
        try {
            // 创建PutObject请求。
            ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(bytes));

            // 文件访问路径规则 https://BucketName.Endpoint/ObjectName
            StringBuilder stringBuilder = new StringBuilder("https://");
            stringBuilder.append(bucketName).append(".").append(endpoint).append("/").append(objectName);

            log.info("文件上传到: {}", stringBuilder);
            return stringBuilder.toString();
        } catch (OSSException oe) {
            log.error("Caught an OSSException, which means your request made it to OSS, but was rejected with an error response for some reason.", oe);
            throw new RuntimeException(oe);
        } catch (ClientException ce) {
            log.error("Caught an ClientException, which means the client encountered a serious internal problem while trying to communicate with OSS, such as not being able to access the network.", ce);
            throw new RuntimeException(ce);
        }
    }

    /**
     * 文件删除
     *
     * @param objectName 文件名
     */
    public void delete(String objectName) {
        try {
            // 删除文件。
            ossClient.deleteObject(bucketName, objectName);
            log.info("文件删除成功: {}", objectName);
        } catch (OSSException oe) {
            log.error("Caught an OSSException, which means your request made it to OSS, but was rejected with an error response for some reason.", oe);
        } catch (ClientException ce) {
            log.error("Caught an ClientException, which means the client encountered a serious internal problem while trying to communicate with OSS, such as not being able to access the network.", ce);
        }
    }
}
