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

package cn.org.byc.smart.oss.config;

import cn.org.byc.smart.oss.props.OssProperties;
import cn.org.byc.smart.oss.rule.DefaultOssRule;
import cn.org.byc.smart.oss.rule.OssRule;
import cn.org.byc.smart.oss.rule.TenantOssRule;
import cn.org.byc.smart.oss.template.QiNiuTemplate;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(OssProperties.class)
@ConditionalOnProperty(value = "smart.oss.name", havingValue = "qiniu")
public class QiNiuConfiguration {
    private final OssProperties ossProperties;

    public QiNiuConfiguration(OssProperties ossProperties) {
        this.ossProperties = ossProperties;
    }

    @Bean
    @ConditionalOnMissingBean(OssRule.class)
    @ConditionalOnProperty(value = "smart.oss.tenantMode", havingValue = "false", matchIfMissing = true)
    public OssRule ossRule(){
        return new DefaultOssRule();
    }

    @Bean
    @ConditionalOnMissingBean(OssRule.class)
    @ConditionalOnProperty(value = "smart.oss.tenantMode", havingValue = "true")
    public OssRule tenantOssRule() {
        return new TenantOssRule();
    }

    @Bean
    public com.qiniu.storage.Configuration qiniuConfiguration() {
        return new com.qiniu.storage.Configuration(Region.autoRegion());
    }

    @Bean
    public Auth auth() {
        return Auth.create(ossProperties.getAccessKey(), ossProperties.getSecretKey());
    }

    @Bean
    @ConditionalOnBean(com.qiniu.storage.Configuration.class)
    public UploadManager uploadManager(com.qiniu.storage.Configuration cfg) {
        return new UploadManager(cfg);
    }

    @Bean
    @ConditionalOnBean(com.qiniu.storage.Configuration.class)
    public BucketManager bucketManager(com.qiniu.storage.Configuration cfg) {
        return new BucketManager(Auth.create(ossProperties.getAccessKey(), ossProperties.getSecretKey()), cfg);
    }

    @Bean
    @ConditionalOnMissingBean(QiNiuTemplate.class)
    @ConditionalOnBean({Auth.class, UploadManager.class, BucketManager.class, OssRule.class})
    public QiNiuTemplate qiniuTemplate(Auth auth, UploadManager uploadManager, BucketManager bucketManager, OssRule ossRule) {
        return new QiNiuTemplate(auth, uploadManager, bucketManager, ossProperties, ossRule);
    }

}
