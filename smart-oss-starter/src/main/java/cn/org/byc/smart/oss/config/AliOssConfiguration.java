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
import cn.org.byc.smart.oss.template.AliOssTemplate;
import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.auth.CredentialsProvider;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@AllArgsConstructor
@AutoConfigureAfter(QiNiuConfiguration.class)
@EnableConfigurationProperties(OssProperties.class)
@ConditionalOnProperty(value = "smart.oss.name", havingValue = "ali")
public class AliOssConfiguration {
    private OssProperties ossProperties;

    @Bean
    @ConditionalOnMissingBean(OssRule.class)
    @ConditionalOnProperty(value = "smart.oss.tenantMode", havingValue = "false", matchIfMissing = true)
    public OssRule ossRule(){
        return new DefaultOssRule();
    }

    @Bean
    @ConditionalOnMissingBean(OssRule.class)
    @ConditionalOnProperty(value = "smart.oss.tenantMode", havingValue = "true")
    public OssRule tenantOssRule(){
        return new TenantOssRule();
    }

    @Bean
    @ConditionalOnMissingBean(OSSClient.class)
    public OSSClient ossClient(){
        ClientConfiguration configuration = new ClientConfiguration();
        // 设置OSSClient允许打开的最大Http连接数
        configuration.setMaxConnections(ClientConfiguration.DEFAULT_MAX_CONNECTIONS);
        // 设置Socket层传输数据的超时时间
        configuration.setSocketTimeout(ClientConfiguration.DEFAULT_SOCKET_TIMEOUT);
        // 设置建立连接的超时时间
        configuration.setConnectionTimeout(ClientConfiguration.DEFAULT_CONNECTION_TIMEOUT);
        // 设置失败请求重试次数(默认3次)
        configuration.setMaxErrorRetry(ClientConfiguration.DEFAULT_MAX_RETRIES+2);
        // 设置连接空闲的超时时间，超时则关闭连接
        configuration.setIdleConnectionTime(ClientConfiguration.DEFAULT_IDLE_CONNECTION_TIME);
        // 设置从连接池中获取连接的超时时间
        configuration.setConnectionRequestTimeout(ClientConfiguration.DEFAULT_CONNECTION_REQUEST_TIMEOUT);
        CredentialsProvider credentialsProvider = new DefaultCredentialProvider(ossProperties.getAccessKey(), ossProperties.getSecretKey());
        return new OSSClient(ossProperties.getEndpoint(),credentialsProvider,configuration);
    }

    @Bean
    @ConditionalOnMissingBean(AliOssTemplate.class)
    @ConditionalOnBean({OSSClient.class, OssRule.class})
    public AliOssTemplate aliOssTemplate(OSSClient ossClient, OssRule ossRule){
        return new AliOssTemplate(ossClient, ossProperties, ossRule);
    }
}
