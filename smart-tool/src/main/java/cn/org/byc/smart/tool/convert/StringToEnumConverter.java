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

import cn.org.byc.smart.tool.utils.ConvertUtil;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 字符串到枚举类型的转换器
 * 
 * <p>该转换器支持将字符串转换为对应的枚举类型。转换规则如下:
 * <ul>
 *   <li>优先使用带有{@link JsonCreator}注解的构造函数或方法进行转换</li>
 *   <li>如果没有{@link JsonCreator}注解，则使用枚举的valueOf方法进行转换</li>
 *   <li>使用ConcurrentHashMap缓存注解信息，提高性能</li>
 * </ul>
 *
 * <p>使用示例:
 * <pre>{@code
 * // 1. 定义枚举类
 * public enum Status {
 *     ACTIVE("A"),
 *     INACTIVE("I");
 *     
 *     private final String code;
 *     
 *     Status(String code) {
 *         this.code = code;
 *     }
 *     
 *     @JsonCreator
 *     public static Status fromCode(String code) {
 *         for (Status status : values()) {
 *             if (status.code.equals(code)) {
 *                 return status;
 *             }
 *         }
 *         throw new IllegalArgumentException("Invalid status code: " + code);
 *     }
 * }
 * 
 * // 2. 使用转换器
 * StringToEnumConverter converter = new StringToEnumConverter();
 * Status result = (Status) converter.convert("A", 
 *                         TypeDescriptor.valueOf(String.class),
 *                         TypeDescriptor.valueOf(Status.class));
 * // result 将等于 Status.ACTIVE
 * }</pre>
 *
 * @author Ken
 * @see ConditionalGenericConverter
 * @see JsonCreator
 */
public class StringToEnumConverter implements ConditionalGenericConverter {
    private static final Logger LOGGER = LoggerFactory.getLogger(StringToEnumConverter.class);
    
    /**
     * 枚举类型的注解缓存
     * <p>缓存{@link JsonCreator}注解的Constructor或Method对象
     * 使用ConcurrentHashMap实现线程安全的缓存
     */
    private static final ConcurrentMap<Class<?>, AccessibleObject> ENUM_CACHE_MAP = new ConcurrentHashMap<>(8);

    /**
     * 获取类中带有{@link JsonCreator}注解的构造函数或方法
     * 
     * @param clazz 要检查的类
     * @return 带有{@link JsonCreator}注解的Constructor或Method对象，如果没有找到则返回null
     */
    @Nullable
    private static AccessibleObject getAnnotation(Class<?> clazz) {
        Set<AccessibleObject> accessibleObjects = new HashSet<>();
        // JsonCreator METHOD, CONSTRUCTOR
        Constructor<?>[] constructors = clazz.getConstructors();
        Collections.addAll(accessibleObjects, constructors);
        // methods
        Method[] methods = clazz.getDeclaredMethods();
        Collections.addAll(accessibleObjects, methods);
        for (AccessibleObject accessibleObject : accessibleObjects) {
            // 复用 jackson 的 JsonCreator注解
            JsonCreator jsonCreator = accessibleObject.getAnnotation(JsonCreator.class);
            if (jsonCreator != null && JsonCreator.Mode.DISABLED != jsonCreator.mode()) {
                accessibleObject.setAccessible(true);
                return accessibleObject;
            }
        }
        return null;
    }

    /**
     * 判断是否可以进行转换
     * <p>本转换器支持所有字符串到枚举类型的转换
     *
     * @param sourceType 源类型描述符
     * @param targetType 目标类型描述符
     * @return 始终返回true，表示支持所有字符串到枚举类型的转换
     */
    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        return true;
    }

    /**
     * 获取支持的转换类型对
     * <p>仅支持String到Enum的转换
     *
     * @return 支持的转换类型对集合
     */
    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new ConvertiblePair(String.class, Enum.class));
    }

    /**
     * 执行转换操作
     * 
     * @param source 源对象（字符串）
     * @param sourceType 源类型描述符
     * @param targetType 目标类型描述符
     * @return 转换后的枚举对象，如果转换失败则返回null
     */
    @Nullable
    @Override
    public Object convert(@Nullable Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (!StringUtils.hasText((String) source)) {
            return null;
        }
        Class<?> clazz = targetType.getType();
        AccessibleObject accessibleObject = ENUM_CACHE_MAP.computeIfAbsent(clazz, StringToEnumConverter::getAnnotation);
        String value = ((String) source).trim();
        // 如果为null，走默认的转换
        if (accessibleObject == null) {
            return valueOf(clazz, value);
        }
        try {
            return StringToEnumConverter.invoke(clazz, accessibleObject, value);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 使用Enum.valueOf方法将字符串转换为枚举值
     * 
     * @param clazz 枚举类
     * @param value 要转换的字符串值
     * @param <T> 枚举类型
     * @return 转换后的枚举值
     */
    private static <T extends Enum<T>> T valueOf(Class<?> clazz, String value) {
        return Enum.valueOf((Class<T>) clazz, value);
    }

    /**
     * 通过反射调用构造函数或方法创建枚举实例
     *
     * @param clazz 枚举类
     * @param accessibleObject 可访问对象（Constructor或Method）
     * @param value 要转换的字符串值
     * @return 转换后的枚举实例
     * @throws IllegalAccessException 如果访问出错
     * @throws InvocationTargetException 如果调用方法出错
     * @throws InstantiationException 如果实例化出错
     */
    @Nullable
    private static Object invoke(Class<?> clazz, AccessibleObject accessibleObject, String value)
            throws IllegalAccessException, InvocationTargetException, InstantiationException {
        if (accessibleObject instanceof Constructor constructor) {
            Class<?> paramType = constructor.getParameterTypes()[0];
            // 类型转换
            Object object = ConvertUtil.convert(value, paramType);
            return constructor.newInstance(object);
        }
        if (accessibleObject instanceof Method method) {
            Class<?> paramType = method.getParameterTypes()[0];
            // 类型转换
            Object object = ConvertUtil.convert(value, paramType);
            return method.invoke(clazz, object);
        }
        return null;
    }
}
