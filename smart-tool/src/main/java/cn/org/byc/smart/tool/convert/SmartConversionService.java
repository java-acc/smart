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

package cn.org.byc.smart.tool.convert;

import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.util.StringValueResolver;

/**
 * Schedule项目的类型转换服务
 * 
 * <p>该服务扩展了Spring Boot的ApplicationConversionService，主要提供以下功能：
 * <ul>
 *     <li>统一的类型转换服务，支持各种类型之间的转换</li>
 *     <li>自定义的枚举转换器，支持字符串与枚举之间的双向转换</li>
 *     <li>支持Spring配置文件中的占位符解析</li>
 * </ul>
 * 
 * <p>该服务采用单例模式实现，确保全局使用同一个转换服务实例。
 * 使用方式：
 * <ul>
 *     <li>通过getInstance()获取全局实例</li>
 *     <li>通过构造函数创建自定义实例</li>
 * </ul>
 *
 * @author Ken
 * @see EnumToStringConverter
 * @see StringToEnumConverter
 * @see ApplicationConversionService
 */
public class SmartConversionService extends ApplicationConversionService {
    /**
     * 静态内部类，用于实现延迟加载的单例模式
     * 利用类加载机制确保线程安全
     */
    private static class Holder {
        // 创建单例实例，使用默认构造函数
        static final SmartConversionService INSTANCE = new SmartConversionService();
    }

    /**
     * 默认构造函数
     * 初始化转换服务并添加自定义的枚举转换器
     */
    public SmartConversionService() {
        super();
        // 添加自定义的枚举转换器
        super.addConverter(new EnumToStringConverter());
        super.addConverter(new StringToEnumConverter());
    }

    /**
     * 带有字符串值解析器的构造函数
     * 初始化转换服务并添加自定义的枚举转换器
     *
     * @param embeddedValueResolver Spring的字符串值解析器，用于解析配置文件中的占位符
     */
    public SmartConversionService(StringValueResolver embeddedValueResolver) {
        super(embeddedValueResolver);
        // 添加自定义的枚举转换器
        super.addConverter(new EnumToStringConverter());
        super.addConverter(new StringToEnumConverter());
    }

    /**
     * 获取ScheduleConversionService的单例实例
     * 采用延迟加载策略，在首次调用时才创建实例
     * 
     * <p>该方法是线程安全的，可以在多线程环境下使用
     *
     * @return 返回GenericConversionService类型的转换服务实例，实际上是ScheduleConversionService的实例
     */
    public static GenericConversionService getInstance() {
        return Holder.INSTANCE;
    }
}
