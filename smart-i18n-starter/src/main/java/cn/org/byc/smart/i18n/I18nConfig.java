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

package cn.org.byc.smart.i18n;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/**
 * 国际化配置类
 * 
 * <p>提供Spring Boot的国际化自动配置功能，主要职责：
 * <ul>
 *     <li>自动配置国际化消息源</li>
 *     <li>支持从配置文件读取国际化资源路径</li>
 *     <li>提供默认的国际化消息处理器</li>
 * </ul>
 * 
 * <p>配置示例（application.yml）：
 * <pre>
 * spring:
 *   messages:
 *     basename: i18n/messages,i18n/errors
 * </pre>
 *
 * @author Ken
 * @see I18nMessage
 * @see org.springframework.context.MessageSource
 */
@AutoConfiguration
public class I18nConfig {

    /**
     * 创建国际化消息处理器
     * 
     * <p>从Spring环境配置中读取国际化资源文件的基础名称。
     * 如果未配置，默认使用"i18n"作为基础名称。
     *
     * @param environment Spring环境配置，不能为null
     * @return 国际化消息处理器实例
     * @throws IllegalArgumentException 如果environment为null
     */
    @Bean
    public I18nMessage i18nMessage(Environment environment) {
        if (environment == null) {
            throw new IllegalArgumentException("Environment cannot be null");
        }
        // 从配置文件读取basename配置，默认为"i18n"
        final String[] basename = environment.getProperty("spring.messages.basename", "i18n").split(",");
        return new I18nMessage(basename);
    }
}
