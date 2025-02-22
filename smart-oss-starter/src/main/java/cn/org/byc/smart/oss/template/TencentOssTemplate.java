/*
 * Copyright 2025 Ken(kan.zhang-cn@hotmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.org.byc.smart.oss.template;

import cn.org.byc.smart.oss.model.OssFile;
import cn.org.byc.smart.oss.model.SmartFile;
import cn.org.byc.smart.oss.props.OssProperties;
import cn.org.byc.smart.oss.rule.OssRule;
import cn.org.byc.smart.tool.constant.StringPool;
import cn.org.byc.smart.tool.jackson.JsonUtil;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.http.HttpMethodName;
import com.qcloud.cos.model.GeneratePresignedUrlRequest;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectResult;
import com.tencent.cloud.CosStsClient;
import com.tencent.cloud.Response;
import lombok.SneakyThrows;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

public class TencentOssTemplate {

    private COSClient ossClient;
    private OssProperties ossProperties;
    private OssRule ossRule;

    public TencentOssTemplate(COSClient ossClient, OssProperties ossProperties, OssRule ossRule) {
        this.ossClient = ossClient;
        this.ossProperties = ossProperties;
        this.ossRule = ossRule;
    }

    @SneakyThrows
    public void makeBucket(String bucketName) {
        if (!bucketExists(bucketName)) {
            ossClient.createBucket(getBucketName(bucketName));
        }
    }

    @SneakyThrows
    public void removeBucket(String bucketName) {
        ossClient.deleteBucket(getBucketName(bucketName));
    }

    @SneakyThrows
    public boolean bucketExists(String bucketName) {
        return ossClient.doesBucketExist(getBucketName(bucketName));
    }

    @SneakyThrows
    public void copyFile(String bucketName, String fileName, String destBucketName) {
        ossClient.copyObject(getBucketName(bucketName), fileName, getBucketName(destBucketName), fileName);
    }

    @SneakyThrows
    public void copyFile(String bucketName, String fileName, String destBucketName, String destFileName) {
        ossClient.copyObject(getBucketName(bucketName), fileName, getBucketName(destBucketName), destFileName);
    }

    @SneakyThrows
    public OssFile statFile(String fileName) {
        return statFile(ossProperties.getBucketName(), fileName);
    }

    @SneakyThrows
    public OssFile statFile(String bucketName, String fileName) {
        ObjectMetadata stat = ossClient.getObjectMetadata(getBucketName(bucketName), fileName);
        OssFile ossFile = new OssFile();
        ossFile.setFileName(fileName);
        ossFile.setLink(fileLink(ossFile.getFileName()));
        ossFile.setHash(stat.getContentMD5());
        ossFile.setLength(stat.getContentLength());
        ossFile.setPutTime(stat.getLastModified());
        ossFile.setContentType(stat.getContentType());
        return ossFile;
    }

    @SneakyThrows
    public String filePath(String fileName) {
        return getOssHost().concat(StringPool.SLASH).concat(fileName);
    }

    @SneakyThrows
    public String filePath(String bucketName, String fileName) {
        return getOssHost(bucketName).concat(StringPool.SLASH).concat(fileName);
    }

    @SneakyThrows
    public String fileLink(String fileName) {
        return getOssHost().concat(StringPool.SLASH).concat(fileName);
    }

    @SneakyThrows
    public String fileLink(String bucketName, String fileName) {
        return getOssHost(bucketName).concat(StringPool.SLASH).concat(fileName);
    }

    @SneakyThrows
    public SmartFile putFile(MultipartFile file) {
        return putFile(ossProperties.getBucketName(), file.getOriginalFilename(), file);
    }

    @SneakyThrows
    public SmartFile putFile(String fileName, MultipartFile file) {
        return putFile(ossProperties.getBucketName(), fileName, file);
    }

    @SneakyThrows
    public SmartFile putFile(String bucketName, String fileName, MultipartFile file) {
        return putFile(bucketName, fileName, file.getInputStream());
    }

    @SneakyThrows
    public SmartFile putFileWithPath(String fileName, String filePath, InputStream stream) {
        return put(ossProperties.getBucketName(), stream, fileName, filePath,false);
    }

    @SneakyThrows
    public SmartFile putFile(String fileName, InputStream stream) {
        return putFile(ossProperties.getBucketName(), fileName, stream);
    }

    @SneakyThrows
    public SmartFile putFile(String bucketName, String fileName, InputStream stream) {
        return put(bucketName, stream, fileName, null, false);
    }

    @SneakyThrows
    public SmartFile put(String bucketName, InputStream stream, String key, String filePath, boolean cover) {
        makeBucket(bucketName);
        String originalName = key;
        key = (null != filePath ? filePath : getFileName(key));
        // 覆盖上传
        if (cover) {
            ossClient.putObject(getBucketName(bucketName), key, stream, null);
        } else {
            PutObjectResult response = ossClient.putObject(getBucketName(bucketName), key, stream, null);
            int retry = 0;
            int retryCount = 5;
            while (StringUtils.isEmpty(response.getETag()) && retry < retryCount) {
                response = ossClient.putObject(getBucketName(bucketName), key, stream, null);
                retry++;
            }
        }
        SmartFile file = new SmartFile();
        file.setOriginalName(originalName);
        file.setName(key);
        file.setLink(fileLink(bucketName, key));
        return file;
    }

    @SneakyThrows
    public void removeFile(String fileName) {
        ossClient.deleteObject(getBucketName(), fileName);
    }

    @SneakyThrows
    public void removeFile(String bucketName, String fileName) {
        ossClient.deleteObject(getBucketName(bucketName), fileName);
    }

    @SneakyThrows
    public void removeFiles(List<String> fileNames) {
        fileNames.forEach(this::removeFile);
    }

    @SneakyThrows
    public void removeFiles(String bucketName, List<String> fileNames) {
        fileNames.forEach(fileName -> removeFile(getBucketName(bucketName), fileName));
    }

    /**
     * 根据规则生成存储桶名称规则
     *
     * @return String
     */
    private String getBucketName() {
        return getBucketName(ossProperties.getBucketName());
    }

    /**
     * 根据规则生成存储桶名称规则
     *
     * @param bucketName 存储桶名称
     * @return String
     */
    private String getBucketName(String bucketName) {
        return ossRule.bucketName(bucketName);
    }

    /**
     * 根据规则生成文件名称规则
     *
     * @param originalFilename 原始文件名
     * @return string
     */
    private String getFileName(String originalFilename) {
        return ossRule.fileName(originalFilename);
    }

    public String getUploadToken() {
        return getUploadToken(ossProperties.getBucketName());
    }

    /**
     * TODO 过期时间
     * <p>
     * 获取上传凭证，普通上传
     */
    public String getUploadToken(String bucketName) {
        // 默认过期时间10分钟
        return getUploadToken(bucketName, ossProperties.getArgs().get("expireTime", 600L));
    }

    /**
     * TODO 上传大小限制、基础路径
     * <p>
     * 获取上传凭证，普通上传
     */
    @SneakyThrows
    public String getUploadToken(String bucketName, long expireTime) {

        TreeMap<String, Object> config = new TreeMap<>();
        // 云 api 密钥 SecretId
        config.put("secretId", ossProperties.getAccessKey());
        // 云 api 密钥 SecretKey
        config.put("secretKey", ossProperties.getSecretKey());
        // 设置域名
        //config.put("host", "sts.internal.tencentcloudapi.com");
        long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
        // 临时密钥有效时长，单位是秒
        config.put("durationSeconds", expireEndTime);
        // 换成你的 bucket
        config.put("bucket", bucketName);
        // 换成 bucket 所在地区
        config.put("region", ossProperties.getArgs().get("region", "ap-guangzhou"));
        // 可以通过 allowPrefixes 指定前缀数组, 例子： a.jpg 或者 a/* 或者 * (使用通配符*存在重大安全风险, 请谨慎评估使用)
        config.put("allowPrefixes", new String[]{
                StringPool.ASTERISK
        });
        // 密钥的权限列表。简单上传和分片需要以下的权限，其他权限列表请看 https://cloud.tencent.com/document/product/436/31923
        String[] allowActions = new String[]{
                // 简单上传
                "name/cos:PutObject",
                "name/cos:PostObject",
                // 分片上传
                "name/cos:InitiateMultipartUpload",
                "name/cos:ListMultipartUploads",
                "name/cos:ListParts",
                "name/cos:UploadPart",
                "name/cos:CompleteMultipartUpload"
        };
        config.put("allowActions", allowActions);

        Response response = CosStsClient.getCredential(config);
        return JsonUtil.toJson(response);
    }

    /**
     * 生成预签名
     * @param fileKey
     * @param httpMethod
     * @return
     */
    public String generatePresignedUrl(String fileKey, String httpMethod) {
        return generatePresignedUrl(getBucketName(), fileKey, httpMethod);
    }

    /**
     * 生成预签名url
     * @param bucketName
     * @param fileKey
     * @param httpMethod
     * @return
     */
    private String generatePresignedUrl(String bucketName, String fileKey, String httpMethod) {
        //  url的有效时间默认10分钟
        final LocalDateTime expirationDateTime = LocalDateTime.now().plusSeconds(ossProperties.getArgs().get("expireTime", 600));
        final Date expiration = Date.from(expirationDateTime.atZone(ZoneId.systemDefault()).toInstant());
        final GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(getBucketName(bucketName), fileKey)
                .withMethod(HttpMethodName.valueOf(httpMethod))
                .withExpiration(expiration);
        // ACL权限：公共读
//        generatePresignedUrlRequest.putCustomRequestHeader("x-amz-acl", "public-read");
        final URL url = ossClient.generatePresignedUrl(generatePresignedUrlRequest);
        return url.toString();
    }

    public String getOssHost(String bucketName) {
        String prefix = ossProperties.getEndpoint().contains("https://") ? "https://" : "http://";
        return prefix + getBucketName(bucketName) + StringPool.DOT + ossProperties.getEndpoint().replaceFirst(prefix, StringPool.EMPTY);
    }

    public String getOssHost() {
        return getOssHost(ossProperties.getBucketName());
    }
}
