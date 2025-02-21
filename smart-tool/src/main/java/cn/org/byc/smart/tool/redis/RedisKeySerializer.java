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

package cn.org.byc.smart.tool.redis;

import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.lang.Nullable;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Redis键序列化器
 * 
 * <p>该序列化器用于将Redis的key序列化为字符串。主要特点：
 * <ul>
 *   <li>支持将各种类型的对象序列化为字符串格式的key</li>
 *   <li>对Spring Cache中的简单基本类型提供特殊处理</li>
 *   <li>支持自定义字符集编码</li>
 *   <li>使用Spring的转换服务进行类型转换</li>
 * </ul>
 *
 * <p>使用示例:
 * <pre>{@code
 * // 1. 创建序列化器（使用默认UTF-8编码）
 * RedisKeySerializer serializer = new RedisKeySerializer();
 * 
 * // 2. 序列化各种类型的key
 * // 字符串key
 * byte[] strKey = serializer.serialize("user:123");
 * 
 * // 数字key
 * byte[] numKey = serializer.serialize(123);
 * 
 * // 复合对象key
 * UserKey userKey = new UserKey("admin", 1);
 * byte[] objKey = serializer.serialize(userKey);
 * 
 * // 3. 反序列化key
 * String key = (String) serializer.deserialize(strKey);
 * 
 * // 4. 使用自定义字符集
 * RedisKeySerializer gbkSerializer = new RedisKeySerializer(Charset.forName("GBK"));
 * }</pre>
 *
 * <p>特点：
 * <ul>
 *   <li>对SimpleKey类型返回空字符串</li>
 *   <li>支持自定义字符集编码</li>
 *   <li>使用Spring的转换服务确保类型转换的一致性</li>
 *   <li>线程安全</li>
 * </ul>
 *
 * @author Ken
 * @see RedisSerializer
 * @see SimpleKey
 * @see ConversionService
 */
public class RedisKeySerializer implements RedisSerializer<Object> {
    
    /**
     * 字符集编码
     */
    private final Charset charset;
    
    /**
     * 类型转换服务
     */
    private final ConversionService converter;

    /**
     * 使用默认UTF-8字符集创建序列化器
     */
    public RedisKeySerializer() {
        this(StandardCharsets.UTF_8);
    }

    /**
     * 使用指定字符集创建序列化器
     *
     * @param charset 字符集编码，不能为null
     * @throws NullPointerException 如果charset为null
     */
    public RedisKeySerializer(Charset charset) {
        Objects.requireNonNull(charset, "Charset must not be null");
        this.charset = charset;
        this.converter = DefaultConversionService.getSharedInstance();
    }

    /**
     * 反序列化Redis key
     * <p>将字节数组反序列化为字符串。如果字节数组为null，则返回null。
     *
     * @param bytes 要反序列化的字节数组
     * @return 反序列化后的字符串，如果输入为null则返回null
     */
    @Override
    public Object deserialize(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        return new String(bytes, charset);
    }

    /**
     * 序列化对象为Redis key
     * <p>将对象序列化为字节数组。处理规则：
     * <ul>
     *   <li>如果对象为null，抛出异常</li>
     *   <li>如果对象是SimpleKey类型，返回空字符串的字节数组</li>
     *   <li>如果对象是String类型，直接使用</li>
     *   <li>其他类型通过转换服务转换为字符串</li>
     * </ul>
     *
     * @param object 要序列化的对象
     * @return 序列化后的字节数组
     * @throws NullPointerException 如果object为null
     */
    @Override
    @Nullable
    public byte[] serialize(Object object) {
        Objects.requireNonNull(object, "redis key is null");
        String key;
        if (object instanceof SimpleKey) {
            key = "";
        } else if (object instanceof String) {
            key = (String) object;
        } else {
            key = converter.convert(object, String.class);
        }
        return key.getBytes(this.charset);
    }
}
