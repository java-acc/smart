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
import cn.org.byc.smart.oss.props.OssProperties;
import cn.org.byc.smart.oss.rule.OssRule;
import cn.org.byc.smart.tool.constant.StringPool;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.ObjectMetadata;
import lombok.SneakyThrows;

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

    private String fileLink(String fileName) {
        return getOssHost() + StringPool.SLASH + fileName;
    }

    private String getOssHost() {
        return getOssHost(ossProperties.getBucketName());
    }

    private String getOssHost(String bucketName) {
        String prefix = ossProperties.getEndpoint().contains("https://") ? "https://" : "http://";
        return prefix + getBucketName(bucketName) +
                StringPool.DOT + ossProperties.getEndpoint().replaceFirst(prefix, StringPool.EMPTY);
    }

    private String getBucketName(String bucketName) {
        return ossRule.bucketName(bucketName);
    }
}
