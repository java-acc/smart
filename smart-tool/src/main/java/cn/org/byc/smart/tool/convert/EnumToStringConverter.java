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
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.lang.Nullable;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 枚举类型到字符串的转换器
 * 
 * <p>该转换器支持将枚举类型转换为字符串、Integer或Long类型。转换规则如下:
 * <ul>
 *   <li>优先使用带有{@link JsonValue}注解的字段或方法的值进行转换</li>
 *   <li>如果没有{@link JsonValue}注解，则使用枚举的name()方法或ordinal()值进行转换</li>
 *   <li>使用Guava Cache缓存注解信息，提高性能</li>
 * </ul>
 *
 * <p>使用示例:
 * <pre>{@code
 * // 1. 定义枚举类
 * public enum Status {
 *     @JsonValue
 *     ACTIVE("A"),
 *     INACTIVE("I");
 *     
 *     private final String code;
 *     
 *     Status(String code) {
 *         this.code = code;
 *     }
 *     
 *     public String getCode() {
 *         return code;
 *     }
 * }
 * 
 * // 2. 使用转换器
 * EnumToStringConverter converter = new EnumToStringConverter();
 * String result = (String) converter.convert(Status.ACTIVE, 
 *                          TypeDescriptor.valueOf(Status.class),
 *                          TypeDescriptor.valueOf(String.class));
 * // result 将等于 "A"
 * }</pre>
 *
 * @author Ken
 * @see ConditionalGenericConverter
 * @see JsonValue
 */
public class EnumToStringConverter implements ConditionalGenericConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnumToStringConverter.class);

    /**
     * 枚举类型的注解缓存
     * <p>缓存{@link JsonValue}注解的Field或Method对象，使用Guava的LoadingCache实现，具有以下特性:
     * <ul>
     *   <li>最大容量300个条目</li>
     *   <li>访问后1小时过期</li>
     *   <li>写入后3小时过期</li>
     * </ul>
     */
    private static final LoadingCache<Class<?>, AccessibleObject> ENUM_CACHE = CacheBuilder.newBuilder()
            .maximumSize(300)
            .expireAfterAccess(1, TimeUnit.HOURS)
            .expireAfterWrite(3,TimeUnit.HOURS)
            .build(new CacheLoader<>() {
                @Override
                public AccessibleObject load(Class<?> key) {
                    return getAnnotation(key);
                }
            });

    /**
     * 判断是否可以进行转换
     * <p>本转换器支持所有枚举类型的转换
     *
     * @param sourceType 源类型描述符
     * @param targetType 目标类型描述符
     * @return 始终返回true，表示支持所有枚举类型的转换
     */
    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        return true;
    }

    /**
     * 获取支持的转换类型对
     * <p>支持以下转换:
     * <ul>
     *   <li>Enum -> String</li>
     *   <li>Enum -> Integer</li>
     *   <li>Enum -> Long</li>
     * </ul>
     *
     * @return 支持的转换类型对集合
     */
    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return Set.of(new ConvertiblePair(Enum.class, String.class), 
                     new ConvertiblePair(Enum.class, Integer.class), 
                     new ConvertiblePair(Enum.class, Long.class));
    }

    /**
     * 执行转换操作
     *
     * @param source 源对象
     * @param sourceType 源类型描述符
     * @param targetType 目标类型描述符
     * @return 转换后的对象，如果转换失败则返回null
     */
    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (source == null) {
            return null;
        }
        Class<?> sourceClazz = sourceType.getType();
        AccessibleObject accessibleObject = null;
        try {
            accessibleObject = ENUM_CACHE.get(sourceClazz);
        } catch (ExecutionException e) {
            LOGGER.error(e.getMessage(), e);
        }
        Class<?> targetClazz = targetType.getType();
        // 如果为null，走默认的转换
        if (accessibleObject == null) {
            if (String.class == targetClazz) {
                return ((Enum) source).name();
            }
            int ordinal = ((Enum) source).ordinal();
            return ConvertUtil.convert(ordinal, targetClazz);
        }
        try {
            return EnumToStringConverter.invoke(sourceClazz, accessibleObject, source, targetClazz);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 通过反射调用字段或方法获取值
     *
     * @param clazz 枚举类
     * @param accessibleObject 可访问对象（Field或Method）
     * @param source 源对象
     * @param targetClazz 目标类型
     * @return 转换后的值
     * @throws IllegalAccessException 如果访问出错
     * @throws InvocationTargetException 如果调用方法出错
     */
    @Nullable
    private static Object invoke(Class<?> clazz, AccessibleObject accessibleObject, Object source, Class<?> targetClazz)
            throws IllegalAccessException, InvocationTargetException {
        Object value = null;
        if (accessibleObject instanceof Field field) {
            value = field.get(source);
        } else if (accessibleObject instanceof Method method) {
            Class<?> paramType = method.getParameterTypes()[0];
            // 类型转换
            Object object = ConvertUtil.convert(source, paramType);
            value = method.invoke(clazz, object);
        }
        if (value == null) {
            return null;
        }
        return ConvertUtil.convert(value, targetClazz);
    }

    /**
     * 获取类中带有{@link JsonValue}注解的字段或方法
     *
     * @param clazz 要检查的类
     * @return 带有{@link JsonValue}注解的Field或Method对象，如果没有找到则返回null
     */
    private static AccessibleObject getAnnotation(Class<?> clazz) {
        Set<AccessibleObject> accessibleObjects = new HashSet<>();
        // JsonValue METHOD, FIELD
        Field[] fields = clazz.getDeclaredFields();
        Collections.addAll(accessibleObjects, fields);
        // methods
        Method[] methods = clazz.getDeclaredMethods();
        Collections.addAll(accessibleObjects, methods);
        for (AccessibleObject accessibleObject : accessibleObjects) {
            // 复用 jackson 的 JsonValue 注解
            JsonValue jsonValue = accessibleObject.getAnnotation(JsonValue.class);
            if (jsonValue != null && jsonValue.value()) {
                accessibleObject.setAccessible(true);
                return accessibleObject;
            }
        }
        return null;
    }
}
