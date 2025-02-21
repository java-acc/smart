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

import cn.hutool.core.util.ReflectUtil;
import cn.org.byc.smart.tool.supports.Try;
import cn.org.byc.smart.tool.utils.ClassUtil;
import cn.org.byc.smart.tool.utils.ConvertUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.core.Converter;
import org.springframework.core.convert.TypeDescriptor;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 智能Bean属性转换器
 * 
 * <p>该转换器主要用于对象属性的类型转换，是对CGLib BeanCopier的增强实现。主要功能：
 * <ul>
 *   <li>基于字段名称的类型转换</li>
 *   <li>支持缓存字段类型描述符，提高性能</li>
 *   <li>支持空值处理</li>
 *   <li>支持类型兼容性检查</li>
 * </ul>
 *
 * <p>使用示例:
 * <pre>{@code
 * // 1. 创建转换器实例
 * SmartConvert converter = new SmartConvert(TargetClass.class);
 * 
 * // 2. 在BeanCopier中使用
 * BeanCopier copier = BeanCopier.create(SourceClass.class, TargetClass.class, true);
 * TargetClass target = new TargetClass();
 * copier.copy(source, target, converter);
 * 
 * // 3. 类型转换示例
 * // 源类
 * public class Source {
 *     private String age = "25";
 * }
 * // 目标类
 * public class Target {
 *     private Integer age;
 * }
 * // 转换过程会自动将String类型的age转换为Integer类型
 * }</pre>
 *
 * <p>特点：
 * <ul>
 *   <li>支持不同类型属性间的自动转换</li>
 *   <li>使用类型描述符缓存提升性能</li>
 *   <li>支持链式属性转换</li>
 *   <li>线程安全</li>
 * </ul>
 *
 * @author Ken
 * @see org.springframework.cglib.core.Converter
 * @see org.springframework.cglib.beans.BeanCopier
 */
public class SmartConvert implements Converter {
    private static final Logger LOGGER = LoggerFactory.getLogger(SmartConvert.class);

    /**
     * 类型描述符缓存
     * <p>使用ConcurrentHashMap实现线程安全的缓存，键为"类名+字段名"，值为字段的类型描述符
     */
    private static final ConcurrentHashMap<String, TypeDescriptor> TYPE_CACHE = new ConcurrentHashMap<>(8);

    /**
     * 目标类型
     */
    private final Class<?> targetClazz;

    /**
     * 构造函数
     *
     * @param targetClazz 目标类型，不能为null
     * @throws IllegalArgumentException 如果targetClazz为null
     */
    public SmartConvert(Class<?> targetClazz) {
        if (targetClazz == null) {
            throw new IllegalArgumentException("Target class cannot be null");
        }
        this.targetClazz = targetClazz;
    }

    /**
     * 执行类型转换
     * 
     * <p>转换规则：
     * <ul>
     *   <li>如果源值为null，返回null</li>
     *   <li>如果源值类型与目标类型兼容，直接返回源值</li>
     *   <li>否则，使用类型转换服务进行转换</li>
     * </ul>
     *
     * <p>使用示例：
     * <pre>{@code
     * SmartConvert converter = new SmartConvert(TargetClass.class);
     * Object result = converter.convert("123", Integer.class, "age");
     * // result 将是 Integer 类型的 123
     * }</pre>
     *
     * @param value 源值
     * @param target 目标类型
     * @param fieldName 字段名称
     * @return 转换后的值
     * @throws IllegalArgumentException 如果fieldName为null或不是String类型
     */
    @Override
    public Object convert(Object value, Class target, Object fieldName) {
        // 空值直接返回
        if (value == null) {
            return null;
        }

        // 参数校验
        if (fieldName == null || !(fieldName instanceof String)) {
            throw new IllegalArgumentException("Field name must be a non-null String");
        }

        // 类型兼容性检查
        if (ClassUtil.isAssignableValue(target, value)) {
            return value;
        }

        // 获取目标类型的类型描述符并进行转换
        TypeDescriptor targetDescriptor = getTypeDescriptor(targetClazz, (String) fieldName);
        return ConvertUtil.convert(value, targetDescriptor);
    }

    /**
     * 获取字段的类型描述符
     * 
     * <p>该方法会缓存类型描述符以提高性能。缓存的key是类名和字段名的组合。
     *
     * @param clazz 类型
     * @param fieldName 字段名
     * @return 字段的类型描述符
     * @throws NoSuchFieldException 如果字段不存在
     */
    private static TypeDescriptor getTypeDescriptor(final Class<?> clazz, final String fieldName) {
        // 生成缓存key
        String cacheKey = clazz.getName() + fieldName;
        
        // 从缓存中获取或计算类型描述符
        return TYPE_CACHE.computeIfAbsent(cacheKey, Try.of(k -> {
            // 获取字段对象
            Field field = ReflectUtil.getField(clazz, fieldName);
            if (field == null) {
                throw new NoSuchFieldException("Field '" + fieldName + "' not found in class " + clazz.getName());
            }
            return new TypeDescriptor(field);
        }));
    }
}
