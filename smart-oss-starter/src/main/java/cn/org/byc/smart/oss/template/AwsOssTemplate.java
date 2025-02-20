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
import lombok.SneakyThrows;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.HttpStatusCode;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.InputStream;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Ken
 */
public class AwsOssTemplate {
    private final S3Client ossClient;
    private final OssProperties ossProperties;
    private final OssRule ossRule;

    public AwsOssTemplate(S3Client ossClient, OssProperties ossProperties, OssRule ossRule) {
        this.ossClient = ossClient;
        this.ossProperties = ossProperties;
        this.ossRule = ossRule;
    }

    @SneakyThrows
    public void makeBucket(String bucketName) {
        if (!bucketExists(bucketName)) {
            CreateBucketRequest request = CreateBucketRequest.builder()
                    .bucket(getBucketName(bucketName))
                    .objectLockEnabledForBucket(Boolean.FALSE)
                    .build();
            ossClient.createBucket(request);
        }
    }

    @SneakyThrows
    public boolean bucketExists(String bucketName) {
        // see: https://docs.aws.amazon.com/code-library/latest/ug/java_2_s3_code_examples.html#scenarios
        try {
            final String bucket = getBucketName(bucketName);
            ossClient.getBucketAcl(x -> x.bucket(bucket));
            return true;
        } catch (AwsServiceException e) {
            // A redirect error or an AccessDenied exception means the bucket exists but it's not in this region
            // or we don't have permissions to it.
            if ((e.statusCode() == HttpStatusCode.MOVED_PERMANENTLY) || "AccessDenied".equals(e.awsErrorDetails().errorCode())) {
                return true;
            }
            if (e.statusCode() == HttpStatusCode.NOT_FOUND) {
                return false;
            }
            throw e;
        }
    }

    @SneakyThrows
    public void copyFile(String bucketName, String fileName, String destBucketName) {
        CopyObjectRequest request = CopyObjectRequest.builder()
                .sourceBucket(getBucketName(bucketName))
                .sourceKey(fileName)
                .destinationBucket(getBucketName(destBucketName))
                .destinationKey(fileName)
                .build();
        ossClient.copyObject(request);
    }

    @SneakyThrows
    public void copyFile(String bucketName, String fileName, String destBucketName, String destFileName) {
        CopyObjectRequest request = CopyObjectRequest.builder()
                .sourceKey(fileName)
                .sourceBucket(bucketName)
                .destinationKey(destFileName)
                .destinationBucket(destBucketName)
                .build();
        ossClient.copyObject(request);
    }

    @SneakyThrows
    public OssFile statFile(String fileName) {
        return statFile(ossProperties.getBucketName(), fileName);
    }

    @SneakyThrows
    public OssFile statFile(String bucketName, String fileName) {
        HeadObjectRequest request = HeadObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();
        HeadObjectResponse response = ossClient.headObject(request);
        OssFile ossFile = new OssFile();
        ossFile.setFileName(fileName);
        ossFile.setLink(fileLink(ossFile.getFileName()));
        ossFile.setHash(response.sseCustomerKeyMD5());
        ossFile.setLength(response.contentLength());
        ossFile.setPutTime(Date.from(response.lastModified()));
        ossFile.setContentType(response.contentType());
        return ossFile;
    }

    @SneakyThrows
    public String fileLink(String fileName) {
        return getOssHost() + StringPool.SLASH + fileName;
    }

    @SneakyThrows
    public String fileLink(String bucketName, String fileName) {
        return getOssHost(bucketName) + StringPool.SLASH + fileName;
    }

    @SneakyThrows
    public String filePath(String fileName) {
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
        byte[] bytes = stream.readAllBytes();
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(getBucketName(bucketName))
                .key(key)
                .build();
        // 覆盖上传
        if (cover) {
            ossClient.putObject(request, RequestBody.fromBytes(bytes));
        } else {
            PutObjectResponse response = ossClient.putObject(request, RequestBody.fromBytes(bytes));
            int retry = 0;
            int retryCount = 5;
            while (StringUtils.isEmpty(response.eTag()) && retry < retryCount) {
                response = ossClient.putObject(request, RequestBody.fromBytes(bytes));
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
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(getBucketName())
                .key(fileName)
                .build();
        ossClient.deleteObject(request);
    }

    @SneakyThrows
    public void removeFile(String bucketName, String fileName) {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .key(fileName)
                .bucket(getBucketName(bucketName))
                .build();
        ossClient.deleteObject(request);
    }

    @SneakyThrows
    public void removeFiles(List<String> fileNames) {
        fileNames.forEach(this::removeFile);
    }

    @SneakyThrows
    public void removeFiles(String bucketName, List<String> fileNames) {
        fileNames.forEach(fileName -> removeFile(getBucketName(bucketName), fileName));
    }

    public String getUploadToken(String dir) {
        return getUploadToken(ossProperties.getBucketName(), dir);
    }

    /**
     * TODO 过期时间
     * <p>
     * 获取上传凭证，普通上传
     */
    public String getUploadToken(String bucketName, String dir) {
        // 默认过期时间10分钟
        return getUploadToken(bucketName, dir, ossProperties.getArgs().get("expireTime", 600L));
    }

    /**
     * TODO 上传大小限制、基础路径
     * <p>
     * 获取上传凭证，普通上传
     */
    @SneakyThrows
    public String getUploadToken(String bucketName, String dir, long expireTime) {
        // 未实现
        return null;
    }

    /**
     * 生成预签名
     *
     * @param fileKey
     * @param metadata
     * @return
     */
    public String generatePresignedUrl(String fileKey, Map<String, String> metadata) {
        return generatePresignedUrl(getBucketName(), fileKey, metadata);
    }

    /**
     * 生成预签名url
     *
     * @param bucketName
     * @param fileKey
     * @param metadata
     * @return
     */
    private String generatePresignedUrl(String bucketName, String fileKey, Map<String, String> metadata) {
        try (S3Presigner presigner = S3Presigner.builder().s3Client(ossClient).build()) {
            PutObjectRequest objectRequest  = PutObjectRequest.builder()
                    .bucket(getBucketName(bucketName))
                    .key(fileKey)
                    .metadata(metadata)
                    .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(5))
                    .putObjectRequest(objectRequest)
                    .build();

            PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);
            return presignedRequest.url().toString();
        }
    }

    public String getOssHost() {
        return getOssHost(ossProperties.getBucketName());
    }

    public String getOssHost(String bucketName) {
        String prefix = ossProperties.getEndpoint().contains("https://") ? "https://" : "http://";
        return prefix + getBucketName(bucketName)
                + StringPool.DOT + ossProperties.getEndpoint().replaceFirst(prefix, StringPool.EMPTY);
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
