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

package cn.org.byc.smart.tool.utils;

import cn.org.byc.smart.tool.convert.SmartConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.lang.Nullable;

/**
 * 类型转换工具类，提供基于Spring ConversionService的类型转换功能
 *
 * @author Ken
 */
public class ConvertUtils {
    private ConvertUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * 将源对象转换为目标类型
     *
     * @param source 源对象
     * @param targetType 目标类型
     * @param <T> 目标类型泛型
     * @return 转换后的对象，如果源对象为null则返回null
     */
    public static <T> T convert(@Nullable Object source, Class<T> targetType) {
        if (source == null) {
            return null;
        }
        if (ClassUtil.isAssignableValue(targetType, source)) {
            return (T) source;
        }
        GenericConversionService conversionService = SmartConversionService.getInstance();
        return conversionService.convert(source, targetType);
    }

    /**
     * 将源对象根据源类型描述符转换为目标类型
     *
     * @param source 源对象
     * @param sourceType 源类型描述符
     * @param targetType 目标类型描述符
     * @param <T> 目标类型泛型
     * @return 转换后的对象，如果源对象为null则返回null
     */
    public static <T> T convert(@Nullable Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (source == null) {
            return null;
        }
        GenericConversionService conversionService = SmartConversionService.getInstance();
        return (T) conversionService.convert(source, sourceType, targetType);
    }

    /**
     * 将源对象转换为目标类型描述符指定的类型
     *
     * @param source 源对象
     * @param targetType 目标类型描述符
     * @param <T> 目标类型泛型
     * @return 转换后的对象，如果源对象为null则返回null
     */
    public static <T> T convert(@Nullable Object source, TypeDescriptor targetType) {
        if (source == null) {
            return null;
        }
        GenericConversionService conversionService = SmartConversionService.getInstance();
        return (T) conversionService.convert(source, targetType);
    }

}
