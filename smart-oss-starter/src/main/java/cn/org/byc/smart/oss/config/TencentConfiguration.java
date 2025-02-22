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
import cn.org.byc.smart.oss.template.TencentOssTemplate;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.region.Region;
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
@ConditionalOnProperty(value = "smart.oss.name", havingValue = "tencent")
public class TencentConfiguration {
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
    @ConditionalOnMissingBean(COSClient.class)
    public COSClient cosClient() {
        COSCredentials cred = new BasicCOSCredentials(ossProperties.getAccessKey(), ossProperties.getSecretKey());
        // 2 设置 bucket 的地域, COS 地域的简称请参照 https://cloud.tencent.com/document/product/436/6224
        // clientConfig 中包含了设置 region, https(默认 http), 超时, 代理等 set 方法, 使用可参见源码或者常见问题 Java SDK 部分。
        Region region = new Region(ossProperties.getArgs().get("region", "ap-guangzhou"));
        ClientConfig clientConfig = new ClientConfig(region);
        // 这里建议设置使用 https 协议
        // 从 5.6.54 版本开始，默认使用了 https
        clientConfig.setHttpProtocol(HttpProtocol.https);
        // 设置建立连接的超时时间，默认为30000毫秒。
        clientConfig.setConnectionTimeout(50000);
        // 设置Client允许打开的最大HTTP连接数，默认为1024个。
        clientConfig.setMaxConnectionsCount(1024);
        // 设置Socket层传输数据的超时时间，默认为30000毫秒。
        clientConfig.setSocketTimeout(50000);
        // 设置从连接池中获取连接的超时时间（单位：毫秒），默认不超时。
        clientConfig.setConnectionRequestTimeout(1000);
        // 设置失败请求重试次数，默认为3次。
        clientConfig.setMaxErrorRetry(5);
        // 3 生成 cos 客户端。
        return new COSClient(cred, clientConfig);
    }

    @Bean
    @ConditionalOnMissingBean(TencentOssTemplate.class)
    @ConditionalOnBean({COSClient.class, OssRule.class})
    public TencentOssTemplate tencentCosTemplate(COSClient ossClient, OssRule ossRule) {
        return new TencentOssTemplate(ossClient, ossProperties, ossRule);
    }
}
