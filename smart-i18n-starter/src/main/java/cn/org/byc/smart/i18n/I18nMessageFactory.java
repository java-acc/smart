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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.Locale;

/**
 * 国际化消息源工厂类
 * 
 * <p>负责创建和配置{@link ResourceBundleMessageSource}实例。
 * 主要功能包括：
 * <ul>
 *     <li>创建消息源实例</li>
 *     <li>配置消息源的编码和默认编码</li>
 *     <li>设置消息源的基础名称</li>
 *     <li>配置默认区域为中文</li>
 *     <li>支持使用消息代码作为默认消息</li>
 * </ul>
 *
 * @author Ken
 * @see ResourceBundleMessageSource
 * @see I18nMessage
 */
public class I18nMessageFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(I18nMessageFactory.class);

    /**
     * 创建消息源实例
     *
     * <p>创建一个新的{@link ResourceBundleMessageSource}实例，并进行如下配置：
     * <ul>
     *     <li>设置UTF-8编码作为默认编码</li>
     *     <li>设置消息代码作为默认消息（当消息未找到时返回消息代码）</li>
     *     <li>设置默认区域为中文</li>
     *     <li>添加资源文件基础名称</li>
     * </ul>
     *
     * @param beanNames 消息源的基础名称数组，不能为null
     * @return 配置好的消息源实例
     * @throws IllegalArgumentException 如果beanNames为null
     */
    public static ResourceBundleMessageSource messageSource(String... beanNames) {
        if (beanNames == null) {
            throw new IllegalArgumentException("Bean names cannot be null");
        }

        final ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        
        // 记录国际化资源文件的基础名称
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("I18n Resource bean names: {}", (Object) beanNames);
        }
        
        // 添加资源文件基础名称
        messageSource.addBasenames(beanNames);
        // 设置默认编码为UTF-8
        messageSource.setDefaultEncoding("UTF-8");
        // 当消息代码未找到对应的消息时，使用消息代码作为默认消息
        messageSource.setUseCodeAsDefaultMessage(true);
        // 设置默认区域为中文
        messageSource.setDefaultLocale(Locale.CHINA);
        
        return messageSource;
    }
}
