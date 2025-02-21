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
 * 类型转换工具类，提供基于 Spring ConversionService 的类型转换功能。
 * 
 * <p>主要功能包括：
 * <ul>
 *   <li>基本类型之间的转换</li>
 *   <li>复杂对象之间的转换</li>
 *   <li>自定义类型转换支持</li>
 *   <li>类型描述符支持</li>
 * </ul>
 * 
 * <p>与 {@link ConvertUtil} 的区别：
 * <ul>
 *   <li>ConvertUtil 是工具类，不能实例化</li>
 *   <li>ConvertUtil 提供静态方法访问</li>
 *   <li>ConvertUtil 更适合在静态上下文中使用</li>
 * </ul>
 * 
 * <p>使用示例：
 * <pre>{@code
 * // 基本类型转换
 * Integer num = ConvertUtil.convert("123", Integer.class);
 * 
 * // 复杂对象转换
 * UserDTO dto = ConvertUtil.convert(userEntity, UserDTO.class);
 * 
 * // 使用类型描述符转换
 * TypeDescriptor sourceType = TypeDescriptor.valueOf(String.class);
 * TypeDescriptor targetType = TypeDescriptor.valueOf(Integer.class);
 * Integer result = ConvertUtil.convert("123", sourceType, targetType);
 * }</pre>
 * 
 * <p>特点：
 * <ul>
 *   <li>基于 Spring ConversionService，提供强大的类型转换能力</li>
 *   <li>支持自定义转换器注册</li>
 *   <li>线程安全</li>
 *   <li>支持 null 值处理</li>
 * </ul>
 *
 * @author Ken
 * @since 1.0.0
 */
public class ConvertUtil {
    /**
     * 私有构造函数，防止实例化
     * 
     * @throws UnsupportedOperationException 总是抛出此异常
     */
    private ConvertUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * 将源对象转换为目标类型
     * 
     * <p>转换规则：
     * <ul>
     *   <li>如果源对象为 null，返回 null</li>
     *   <li>如果源对象类型与目标类型兼容，直接返回源对象</li>
     *   <li>否则使用 ConversionService 进行转换</li>
     * </ul>
     * 
     * <p>使用示例：
     * <pre>{@code
     * // 字符串转整数
     * Integer num = ConvertUtil.convert("123", Integer.class);  // 返回 123
     * 
     * // 对象转换
     * UserDTO dto = ConvertUtil.convert(userEntity, UserDTO.class);
     * 
     * // null 值处理
     * Integer result = ConvertUtil.convert(null, Integer.class);  // 返回 null
     * }</pre>
     *
     * @param source 源对象
     * @param targetType 目标类型
     * @param <T> 目标类型的泛型标记
     * @return 转换后的对象，如果源对象为null则返回null
     * @throws IllegalArgumentException 如果目标类型为null，或者源对象不为null但源类型为null
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
     * <p>此方法允许在转换时提供更详细的类型信息，特别适用于：
     * <ul>
     *   <li>泛型类型转换</li>
     *   <li>数组类型转换</li>
     *   <li>集合类型转换</li>
     * </ul>
     * 
     * <p>使用示例：
     * <pre>{@code
     * // 使用类型描述符进行转换
     * TypeDescriptor sourceType = TypeDescriptor.valueOf(String.class);
     * TypeDescriptor targetType = TypeDescriptor.valueOf(Integer.class);
     * Integer result = ConvertUtil.convert("123", sourceType, targetType);
     * 
     * // 集合类型转换
     * TypeDescriptor sourceType = TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(String.class));
     * TypeDescriptor targetType = TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(Integer.class));
     * List<Integer> numbers = ConvertUtil.convert(Arrays.asList("1", "2", "3"), sourceType, targetType);
     * }</pre>
     *
     * @param source 源对象
     * @param sourceType 源类型描述符
     * @param targetType 目标类型描述符
     * @param <T> 目标类型的泛型标记
     * @return 转换后的对象，如果源对象为null则返回null
     * @throws IllegalArgumentException 如果目标类型为null，或者源对象不为null但源类型为null
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
     * <p>此方法是 {@link #convert(Object, TypeDescriptor, TypeDescriptor)} 的简化版本，
     * 会自动根据源对象推断源类型描述符。适用于：
     * <ul>
     *   <li>简单类型转换</li>
     *   <li>当源对象类型信息足够时的转换</li>
     *   <li>不需要特别指定源类型信息的场景</li>
     * </ul>
     * 
     * <p>使用示例：
     * <pre>{@code
     * // 简单类型转换
     * TypeDescriptor targetType = TypeDescriptor.valueOf(Integer.class);
     * Integer result = ConvertUtil.convert("123", targetType);
     * 
     * // 集合类型转换
     * TypeDescriptor targetType = TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(Integer.class));
     * List<Integer> numbers = ConvertUtil.convert(Arrays.asList("1", "2", "3"), targetType);
     * }</pre>
     *
     * @param source 源对象
     * @param targetType 目标类型描述符
     * @param <T> 目标类型的泛型标记
     * @return 转换后的对象，如果源对象为null则返回null
     * @throws IllegalArgumentException 如果目标类型为null，或者源对象不为null但源类型为null
     */
    public static <T> T convert(@Nullable Object source, TypeDescriptor targetType) {
        if (source == null) {
            return null;
        }
        GenericConversionService conversionService = SmartConversionService.getInstance();
        return (T) conversionService.convert(source, targetType);
    }
}
