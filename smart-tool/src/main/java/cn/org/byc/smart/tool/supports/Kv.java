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

package cn.org.byc.smart.tool.supports;

import org.springframework.util.LinkedCaseInsensitiveMap;

import java.util.HashMap;

/**
 * 键值对工具类
 * 
 * <p>基于LinkedCaseInsensitiveMap实现的键值对工具类，提供以下特性：
 * <ul>
 *   <li>键不区分大小写</li>
 *   <li>保持插入顺序</li>
 *   <li>支持链式调用</li>
 *   <li>提供便捷的类型转换方法</li>
 * </ul>
 *
 * <p>使用示例:
 * <pre>{@code
 * // 1. 创建并初始化
 * Kv kv = Kv.init()
 *     .set("name", "张三")
 *     .set("age", 25)
 *     .setIgnoreNull("email", null);  // null值不会被设置
 *
 * // 2. 获取值
 * String name = kv.getStr("name");     // 获取字符串
 * Integer age = kv.get("age", 0);      // 获取值，提供默认值
 * Object raw = kv.getObj("someKey");   // 获取原始对象
 *
 * // 3. 创建新的HashMap
 * Map<String, Integer> scores = Kv.newMap();
 * }</pre>
 *
 * @author Ken
 * @see LinkedCaseInsensitiveMap
 */
public class Kv extends LinkedCaseInsensitiveMap<Object> {
    
    /**
     * 私有构造函数
     * <p>使用{@link #init()}方法创建实例
     */
    private Kv() {
        super();
    }

    /**
     * 创建Kv实例
     * <p>工厂方法，返回一个新的Kv实例
     *
     * @return 新的Kv实例
     */
    public static Kv init() {
        return new Kv();
    }

    /**
     * 创建新的HashMap实例
     * <p>工厂方法，返回一个初始容量为8的HashMap
     *
     * @param <K> 键类型
     * @param <V> 值类型
     * @return 新的HashMap实例
     */
    public static <K,V> HashMap<K,V> newMap() {
        // 创建初始容量为8的HashMap
        return new HashMap<>(8);
    }

    /**
     * 设置键值对
     * <p>支持链式调用
     *
     * @param key 键
     * @param val 值
     * @return 当前Kv实例
     */
    public Kv set(String key, Object val) {
        // 存储键值对
        this.put(key, val);
        return this;
    }

    /**
     * 设置键值对，忽略null值
     * <p>当值为null时不进行设置，支持链式调用
     *
     * @param key 键
     * @param val 值
     * @return 当前Kv实例
     */
    public Kv setIgnoreNull(String key, Object val) {
        // 只有当key和val都不为null时才设置
        if (null != key && null != val) {
            set(key, val);
        }
        return this;
    }

    /**
     * 获取原始对象值
     *
     * @param key 键
     * @return 对应的值，可能为null
     */
    public Object getObj(String key) {
        return super.get(key);
    }

    /**
     * 获取值，提供默认值
     *
     * @param key 键
     * @param defaultValue 默认值
     * @param <T> 值类型
     * @return 如果值存在则返回值，否则返回默认值
     */
    public <T> T get(String key, T defaultValue) {
        // 获取值，如果为null则返回默认值
        final Object val = get(key);
        return (T) (val == null ? defaultValue : val);
    }

    /**
     * 获取字符串值
     * <p>如果值不是字符串类型，则调用toString()方法转换
     *
     * @param key 键
     * @return 字符串值，如果值为null则返回null
     */
    public String getStr(String key) {
        // 获取值并转换为字符串
        Object val = get(key, null);
        return val == null ? null : String.valueOf(val);
    }
}
