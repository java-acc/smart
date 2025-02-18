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

package cn.org.byc.smart.xxljob.config;

import cn.org.byc.smart.xxljob.model.XxlProperties;
import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;

@AutoConfiguration
@ConditionalOnClass(XxlJobSpringExecutor.class)
@ConditionalOnProperty(name = "xxl.job.admin.address")
@EnableConfigurationProperties(XxlProperties.class)
@Slf4j
public class XxlJobConfig {

    @Bean
    @ConditionalOnMissingBean(XxlJobSpringExecutor.class)
    public XxlJobSpringExecutor xxlJobExecutor(XxlProperties properties){
        log.info(">>>>>>>>>>>>>> xxl-job config init start");
        final XxlJobSpringExecutor executor = new XxlJobSpringExecutor();
        executor.setAdminAddresses(properties.getAdminAddresses());
        executor.setAppname(properties.getAppName());
        if (StringUtils.hasText(properties.getIp())){
            executor.setIp(properties.getIp());
        }
        if (StringUtils.hasText(properties.getAccessToken())){
            executor.setAccessToken(properties.getAccessToken());
        }
        if (StringUtils.hasText(properties.getLogPath())){
            executor.setLogPath(properties.getLogPath());
        }

        if (properties.getPort()>0){
            executor.setPort(properties.getPort());
        }
        if (properties.getLogRetentionDays()>0){
            executor.setLogRetentionDays(properties.getLogRetentionDays());
        }
        log.info(">>>>>>>>>>>>>> xxl-job config init end");
        return executor;
    }
}
