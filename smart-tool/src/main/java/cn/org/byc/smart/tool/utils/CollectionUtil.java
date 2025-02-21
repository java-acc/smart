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

import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * 集合工具类，提供丰富的集合操作功能。
 * 
 * <p>主要功能包括：
 * <ul>
 *   <li>集合判空操作</li>
 *   <li>数组操作</li>
 *   <li>集合元素查找</li>
 *   <li>Map 操作</li>
 * </ul>
 * 
 * <p>特点：
 * <ul>
 *   <li>支持 null 安全操作</li>
 *   <li>支持各种集合类型</li>
 *   <li>提供便捷的集合工具方法</li>
 *   <li>继承自 Spring 的 CollectionUtils</li>
 * </ul>
 * 
 * <p>使用示例：
 * <pre>{@code
 * // 集合判空
 * List<String> list = new ArrayList<>();
 * boolean isEmpty = CollectionUtil.isEmpty(list);      // 返回 true
 * boolean isNotEmpty = CollectionUtil.isNotEmpty(list);  // 返回 false
 * 
 * // 数组操作
 * String[] arr = {"a", "b", "c"};
 * boolean contains = CollectionUtil.contains(arr, "a");  // 返回 true
 * 
 * // 判断是否为数组
 * boolean isArray = CollectionUtil.isArray(arr);  // 返回 true
 * }</pre>
 *
 * @author Ken
 * @since 1.0.0
 */
public class CollectionUtil extends CollectionUtils {

    /**
     * 检查数组中是否包含指定元素。
     * 
     * <p>此方法提供了 null 安全的数组元素查找功能：
     * <ul>
     *   <li>如果数组为 null，返回 false</li>
     *   <li>使用 ObjectUtil.nullSafeEquals 进行元素比较</li>
     *   <li>支持查找 null 元素</li>
     * </ul>
     * 
     * <p>使用示例：
     * <pre>{@code
     * String[] arr = {"a", "b", "c", null};
     * 
     * boolean hasA = CollectionUtil.contains(arr, "a");     // 返回 true
     * boolean hasNull = CollectionUtil.contains(arr, null); // 返回 true
     * boolean hasD = CollectionUtil.contains(arr, "d");     // 返回 false
     * 
     * String[] nullArr = null;
     * boolean result = CollectionUtil.contains(nullArr, "a"); // 返回 false
     * }</pre>
     *
     * @param array 要搜索的数组
     * @param element 要查找的元素
     * @param <T> 数组元素类型
     * @return 如果数组包含指定元素则返回 true，否则返回 false
     */
    public static <T> boolean contains(@Nullable T[] array, final T element) {
        if (array == null) {
            return false;
        }
        return Arrays.stream(array).anyMatch(x -> ObjectUtil.nullSafeEquals(x, element));
    }

    /**
     * 判断对象是否为数组类型。
     * 
     * <p>此方法检查给定对象是否为数组：
     * <ul>
     *   <li>支持所有类型的数组（原始类型和对象类型）</li>
     *   <li>如果对象为 null，返回 false</li>
     * </ul>
     * 
     * <p>使用示例：
     * <pre>{@code
     * int[] intArr = {1, 2, 3};
     * String[] strArr = {"a", "b"};
     * List<String> list = new ArrayList<>();
     * 
     * boolean test1 = CollectionUtil.isArray(intArr);  // 返回 true
     * boolean test2 = CollectionUtil.isArray(strArr);  // 返回 true
     * boolean test3 = CollectionUtil.isArray(list);    // 返回 false
     * boolean test4 = CollectionUtil.isArray(null);    // 返回 false
     * }</pre>
     *
     * @param obj 要检查的对象
     * @return 如果对象是数组类型则返回 true，否则返回 false
     */
    public static boolean isArray(Object obj) {
        if (null == obj) {
            return false;
        }
        return obj.getClass().isArray();
    }

    /**
     * 判断集合是否非空。
     * 
     * <p>此方法是 {@link CollectionUtils#isEmpty} 的反向操作：
     * <ul>
     *   <li>如果集合为 null，返回 false</li>
     *   <li>如果集合为空，返回 false</li>
     *   <li>如果集合包含元素，返回 true</li>
     * </ul>
     * 
     * <p>使用示例：
     * <pre>{@code
     * List<String> list1 = null;
     * List<String> list2 = new ArrayList<>();
     * List<String> list3 = Arrays.asList("a", "b");
     * 
     * boolean test1 = CollectionUtil.isNotEmpty(list1);  // 返回 false
     * boolean test2 = CollectionUtil.isNotEmpty(list2);  // 返回 false
     * boolean test3 = CollectionUtil.isNotEmpty(list3);  // 返回 true
     * }</pre>
     *
     * @param coll 要检查的集合
     * @return 如果集合非空则返回 true，否则返回 false
     */
    public static boolean isNotEmpty(@Nullable Collection<?> coll) {
        return !CollectionUtils.isEmpty(coll);
    }

    /**
     * 判断 Map 是否非空。
     * 
     * <p>此方法是 {@link CollectionUtils#isEmpty} 的反向操作：
     * <ul>
     *   <li>如果 Map 为 null，返回 false</li>
     *   <li>如果 Map 为空，返回 false</li>
     *   <li>如果 Map 包含元素，返回 true</li>
     * </ul>
     * 
     * <p>使用示例：
     * <pre>{@code
     * Map<String, String> map1 = null;
     * Map<String, String> map2 = new HashMap<>();
     * Map<String, String> map3 = Collections.singletonMap("key", "value");
     * 
     * boolean test1 = CollectionUtil.isNotEmpty(map1);  // 返回 false
     * boolean test2 = CollectionUtil.isNotEmpty(map2);  // 返回 false
     * boolean test3 = CollectionUtil.isNotEmpty(map3);  // 返回 true
     * }</pre>
     *
     * @param map 要检查的 Map
     * @return 如果 Map 非空则返回 true，否则返回 false
     */
    public static boolean isNotEmpty(@Nullable Map<?, ?> map) {
        return !CollectionUtils.isEmpty(map);
    }
}
