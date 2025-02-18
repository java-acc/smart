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
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.Locale;

/**
 * 国际化消息处理类
 * 
 * <p>提供基于Spring ResourceBundle的国际化消息处理功能，支持：
 * <ul>
 *     <li>中英文动态切换</li>
 *     <li>消息参数替换</li>
 *     <li>默认消息回退</li>
 *     <li>自动编码处理</li>
 * </ul>
 * 
 * <p>使用示例：
 * <pre>
 * I18nMessage i18n = new I18nMessage("messages");
 * String message = i18n.toLocale("user.greeting", "Hello {0}", "Ken");
 * </pre>
 *
 * @author Ken
 * @see ResourceBundleMessageSource
 * @see LocaleContextHolder
 */
public class I18nMessage {

    private static final Logger LOGGER = LoggerFactory.getLogger(I18nMessage.class);

    /**
     * 消息源，用于加载和解析国际化资源文件
     */
    private final ResourceBundleMessageSource messageSource;

    /**
     * 构造函数
     *
     * @param beanNames 资源文件的基础名称数组，不能为null或空
     * @throws IllegalArgumentException 如果beanNames为null或空
     */
    public I18nMessage(String... beanNames) {
        if (beanNames == null || beanNames.length == 0) {
            throw new IllegalArgumentException("Bean names cannot be null or empty");
        }
        this.messageSource = I18nMessageFactory.messageSource(beanNames);
    }

    /**
     * 将消息键转换为当前语言环境的消息
     *
     * @param key 消息键
     * @param params 消息参数，用于替换消息模板中的占位符
     * @return 本地化后的消息，如果未找到则返回消息键
     */
    public String toLocale(final String key, final Object... params) {
        return this.toLocale(key, null, params);
    }

    /**
     * 将消息键转换为当前语言环境的消息，支持默认消息
     *
     * @param key 消息键
     * @param defaultMessage 默认消息，当消息键未找到时返回此消息
     * @param params 消息参数，用于替换消息模板中的占位符
     * @return 本地化后的消息
     */
    public String toLocale(final String key, final String defaultMessage, final Object... params) {
        if (key == null) {
            LOGGER.warn("Message key is null, returning default message");
            return defaultMessage;
        }
        return this.messageSource.getMessage(key, params, defaultMessage, isEngLocale() ? Locale.US : Locale.CHINA);
    }

    /**
     * 判断当前是否为英语环境
     * 
     * <p>通过检查LocaleContextHolder中的语言设置来判断。
     * 如果获取语言设置时发生异常，默认返回false（中文环境）。
     *
     * @return 如果当前是英语环境返回true，否则返回false
     */
    public static boolean isEngLocale() {
        try {
            final String language = LocaleContextHolder.getLocale().getLanguage();
            return "en".equalsIgnoreCase(language);
        } catch (final Exception e) {
            LOGGER.warn("Failed to get locale, defaulting to Chinese", e);
            return false;
        }
    }
}
