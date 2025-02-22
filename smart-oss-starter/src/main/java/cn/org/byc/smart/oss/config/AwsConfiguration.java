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
import cn.org.byc.smart.oss.template.AwsOssTemplate;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.retries.internal.DefaultAdaptiveRetryStrategy;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;
import java.time.Duration;

@Configuration(proxyBeanMethods = false)
@AllArgsConstructor
@AutoConfigureAfter(QiNiuConfiguration.class)
@EnableConfigurationProperties(OssProperties.class)
@ConditionalOnProperty(value = "smart.oss.name", havingValue = "aws")
public class AwsConfiguration {
    private OssProperties ossProperties;

    @Bean
    @ConditionalOnMissingBean(OssRule.class)
    @ConditionalOnProperty(value = "smart.oss.tenantMode", havingValue = "false", matchIfMissing = true)
    public OssRule ossRule() {
        return new DefaultOssRule();
    }

    @Bean
    @ConditionalOnMissingBean(OssRule.class)
    @ConditionalOnProperty(value = "smart.oss.tenantMode", havingValue = "true")
    public OssRule tenantOssRule() {
        return new TenantOssRule();
    }

    @Bean
    @ConditionalOnMissingBean(S3Client.class)
    public S3Client amazonS3() {
        StaticCredentialsProvider staticCredentialsProvider = StaticCredentialsProvider.create(
                AwsBasicCredentials.create(ossProperties.getAccessKey(), ossProperties.getSecretKey())
        );

        ClientOverrideConfiguration clientConfig = ClientOverrideConfiguration.builder()
                .retryStrategy(DefaultAdaptiveRetryStrategy.builder()
                        .maxAttempts(5)
                        .build())
                .build();

        SdkHttpClient httpClient = ApacheHttpClient.builder()
                .socketTimeout(Duration.ofMillis(50000))
                .connectionTimeout(Duration.ofMillis(50000))

                .build();
        return S3Client.builder()
                .endpointOverride(URI.create(ossProperties.getEndpoint()))
                .region(Region.CN_NORTH_1)
                .credentialsProvider(staticCredentialsProvider)
                .overrideConfiguration(clientConfig)
                .build();
    }

    @Bean
    @ConditionalOnMissingBean(AwsOssTemplate.class)
    @ConditionalOnBean({S3Client.class, OssRule.class})
    public AwsOssTemplate tencentCosTemplate(S3Client ossClient, OssRule ossRule) {
        return new AwsOssTemplate(ossClient, ossProperties, ossRule);
    }

}
