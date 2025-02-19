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
import cn.org.byc.smart.tool.utils.JsonUtil;
import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PolicyConditions;
import com.aliyun.oss.model.PutObjectResult;
import lombok.SneakyThrows;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AliOssTemplate {
    private final OSSClient ossClient;
    private final OssProperties ossProperties;
    private final OssRule ossRule;

    public AliOssTemplate(OSSClient ossClient, OssProperties ossProperties, OssRule ossRule) {
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
    public boolean bucketExists(String bucketName) {
        return ossClient.doesBucketExist(getBucketName(bucketName));
    }

    @SneakyThrows
    public void removeBucket(String bucketName) {
        ossClient.deleteBucket(getBucketName(bucketName));
    }

    @SneakyThrows
    public void copyFile(String bucketName, String fileName, String destBucketName) {
        ossClient.copyObject(getBucketName(bucketName), fileName, getBucketName(destBucketName), fileName);
    }

    @SneakyThrows
    public String filePath(String bucketName, String fileName) {
        return getOssHost(bucketName).concat(StringPool.SLASH).concat(fileName);
    }



    @SneakyThrows
    public OssFile statFile(String bucketName, String fileName) {
        ObjectMetadata stat = ossClient.getObjectMetadata(getBucketName(bucketName), fileName);
        OssFile ossFile = new OssFile();
        ossFile.setFileName(fileName);
        ossFile.setLink(fileLink(fileName));
        ossFile.setHash(stat.getContentMD5());
        ossFile.setLength(stat.getContentLength());
        ossFile.setPutTime(stat.getLastModified());
        ossFile.setContentType(stat.getContentType());
        return ossFile;
    }

    @SneakyThrows
    public String fileLink(String bucketName, String fileName) {
        return getOssHost(bucketName).concat(StringPool.SLASH).concat(fileName);
    }

    @SneakyThrows
    public String fileLink(String fileName) {
        return getOssHost() + StringPool.SLASH + fileName;
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
    public SmartFile putFile(String fileName, InputStream stream) {
        return putFile(ossProperties.getBucketName(), fileName, stream);
    }

    @SneakyThrows
    public SmartFile putFile(String bucketName, String fileName, InputStream stream) {
        return put(bucketName, stream, fileName, false);
    }

    @SneakyThrows
    public SmartFile put(String bucketName, InputStream stream, String key, boolean cover) {
        makeBucket(bucketName);
        String originalName = key;
        key = getFileName(key);
        // 覆盖上传
        if (cover) {
            ossClient.putObject(getBucketName(bucketName), key, stream);
        } else {
            PutObjectResult response = ossClient.putObject(getBucketName(bucketName), key, stream);
            int retry = 0;
            int retryCount = 5;
            while (StringUtils.isEmpty(response.getETag()) && retry < retryCount) {
                response = ossClient.putObject(getBucketName(bucketName), key, stream);
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

    public String getUploadToken() {
        return getUploadToken(ossProperties.getBucketName());
    }

    /**
     * TODO 过期时间
     * <p>
     * 获取上传凭证，普通上传
     */
    public String getUploadToken(String bucketName) {
        // 默认过期时间2小时
        return getUploadToken(bucketName, ossProperties.getArgs().get("expireTime", 3600L));
    }

    /**
     * TODO 上传大小限制、基础路径
     * <p>
     * 获取上传凭证，普通上传
     */
    public String getUploadToken(String bucketName, long expireTime) {
        String baseDir = "upload";

        long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
        Date expiration = new Date(expireEndTime);

        PolicyConditions policyConditions = new PolicyConditions();
        // 默认大小限制10M
        policyConditions.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, ossProperties.getArgs().get("contentLengthRange", 10485760));
        policyConditions.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, baseDir);

        String postPolicy = ossClient.generatePostPolicy(expiration, policyConditions);
        byte[] binaryData = postPolicy.getBytes(StandardCharsets.UTF_8);
        String encodedPolicy = BinaryUtil.toBase64String(binaryData);
        String postSignature = ossClient.calculatePostSignature(postPolicy);

        Map<String, String> respMap = new LinkedHashMap<>(16);
        respMap.put("accessid", ossProperties.getAccessKey());
        respMap.put("policy", encodedPolicy);
        respMap.put("signature", postSignature);
        respMap.put("dir", baseDir);
        respMap.put("host", getOssHost(bucketName));
        respMap.put("expire", String.valueOf(expireEndTime / 1000));
        return JsonUtil.toJson(respMap);
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
        final GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(getBucketName(bucketName), fileKey);
        generatePresignedUrlRequest.setMethod(HttpMethod.valueOf(httpMethod));
        generatePresignedUrlRequest.setExpiration(expiration);
        // ACL权限：公共读
        //        generatePresignedUrlRequest.putCustomRequestHeader("x-amz-acl", "public-read");
        final URL url = ossClient.generatePresignedUrl(generatePresignedUrlRequest);
        return url.toString();
    }

    public String getOssHost() {
        return getOssHost(ossProperties.getBucketName());
    }

    public String getOssHost(String bucketName) {
        String prefix = ossProperties.getEndpoint().contains("https://") ? "https://" : "http://";
        return prefix + getBucketName(bucketName) +
                StringPool.DOT + ossProperties.getEndpoint().replaceFirst(prefix, StringPool.EMPTY);
    }

    private String getBucketName() {
        return getBucketName(ossProperties.getBucketName());
    }


    private String getBucketName(String bucketName) {
        return ossRule.bucketName(bucketName);
    }

    private String getFileName(String originalFilename) {
        return ossRule.fileName(originalFilename);
    }
}
