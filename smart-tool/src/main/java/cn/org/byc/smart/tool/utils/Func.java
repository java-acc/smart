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

import cn.org.byc.smart.tool.jackson.JsonUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.BeansException;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;

import java.io.Closeable;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * 工具包集合，只做简单的调用，不删除原有工具类。
 * 该类提供了一系列常用的工具方法，包括对象操作、字符串处理、集合操作等。
 *
 * @author Ken
 */
public class Func {

    /**
     * 检查指定的对象引用是否不为 null。此方法主要用于方法和构造函数中的参数验证。
     * 
     * <p>示例用法：
     * <blockquote><pre>
     * public Foo(Bar bar) {
     *     this.bar = $.requireNotNull(bar);
     * }
     * </pre></blockquote>
     *
     * @param obj 要检查的对象引用
     * @param <T> 引用的类型
     * @return 如果不为 null，则返回对象本身
     * @throws NullPointerException 如果对象为 null
     */
    public static <T> T requireNotNull(T obj) {
        return Objects.requireNonNull(obj);
    }

    /**
     * 检查指定的对象引用是否不为 null，如果为 null 则抛出自定义消息的 NullPointerException。
     * 此方法主要用于具有多个参数的方法和构造函数的参数验证。
     * 
     * <p>示例用法：
     * <blockquote><pre>
     * public Foo(Bar bar, Baz baz) {
     *     this.bar = $.requireNotNull(bar, "bar 不能为 null");
     *     this.baz = $.requireNotNull(baz, "baz 不能为 null");
     * }
     * </pre></blockquote>
     *
     * @param obj 要检查的对象引用
     * @param message 如果抛出 NullPointerException 时的详细消息
     * @param <T> 引用的类型
     * @return 如果不为 null，则返回对象本身
     * @throws NullPointerException 如果对象为 null
     */
    public static <T> T requireNotNull(T obj, String message) {
        return Objects.requireNonNull(obj, message);
    }

    /**
     * 检查指定的对象引用是否不为 null，如果为 null 则抛出自定义消息的 NullPointerException。
     * 
     * <p>与 {@link #requireNotNull(Object, String)} 方法不同，此方法允许延迟创建消息，
     * 直到确实需要时才创建。虽然在非 null 的情况下可能会带来性能优势，但在决定调用此方法时，
     * 应确保创建消息供应商的成本低于直接创建消息字符串的成本。
     *
     * @param obj 要检查的对象引用
     * @param messageSupplier 用于生成异常详细消息的供应商
     * @param <T> 引用的类型
     * @return 如果不为 null，则返回对象本身
     * @throws NullPointerException 如果对象为 null
     * @since 1.8
     */
    public static <T> T requireNotNull(T obj, Supplier<String> messageSupplier) {
        return Objects.requireNonNull(obj, messageSupplier);
    }

    /**
     * 判断提供的引用是否为 null。
     * 
     * <p>此方法可以作为 {@link java.util.function.Predicate} 使用，
     * 例如：{@code filter($::isNull)}
     *
     * @param obj 要检查的引用
     * @return 如果引用为 null 则返回 true，否则返回 false
     * @see java.util.function.Predicate
     * @since 1.8
     */
    public static boolean isNull(@Nullable Object obj) {
        return Objects.isNull(obj);
    }

    /**
     * 判断提供的引用是否不为 null。
     * 
     * <p>此方法可以作为 {@link java.util.function.Predicate} 使用，
     * 例如：{@code filter($::notNull)}
     *
     * @param obj 要检查的引用
     * @return 如果引用不为 null 则返回 true，否则返回 false
     * @see java.util.function.Predicate
     * @since 1.8
     */
    public static boolean notNull(@Nullable Object obj) {
        return Objects.nonNull(obj);
    }

    /**
     * 将字符串的首字母转换为小写。
     *
     * @param str 要转换的字符串
     * @return 首字母小写的字符串
     */
    public static String firstCharToLower(String str) {
        return StringUtil.lowerFirst(str);
    }

    /**
     * 将字符串的首字母转换为大写。
     *
     * @param str 要转换的字符串
     * @return 首字母大写的字符串
     */
    public static String firstCharToUpper(String str) {
        return StringUtil.upperFirst(str);
    }

    /**
     * 检查给定的字符序列是否包含实际的文本内容。
     * 
     * <p>具体来说，当字符序列不为 null，长度大于 0，
     * 并且包含至少一个非空白字符时，返回 true。
     * 
     * <pre class="code">
     * $.isBlank(null)     = true
     * $.isBlank("")       = true
     * $.isBlank(" ")      = true
     * $.isBlank("12345")  = false
     * $.isBlank(" 12345 ")= false
     * </pre>
     *
     * @param cs 要检查的字符序列（可以为 null）
     * @return 如果字符序列不为 null，长度大于 0，且不仅包含空白字符，则返回 true
     * @see Character#isWhitespace
     */
    public static boolean isBlank(@Nullable final CharSequence cs) {
        return StringUtil.isBlank(cs);
    }

    /**
     * 检查字符序列是否不为空（""），不为 null 且不仅包含空白字符。
     * 
     * <pre>
     * $.isNotBlank(null)    = false
     * $.isNotBlank("")      = false
     * $.isNotBlank(" ")     = false
     * $.isNotBlank("bob")   = true
     * $.isNotBlank("  bob  ") = true
     * </pre>
     *
     * @param cs 要检查的字符序列，可以为 null
     * @return 如果字符序列不为空且不为 null 且不仅包含空白字符，则返回 true
     * @see Character#isWhitespace
     */
    public static boolean isNotBlank(@Nullable final CharSequence cs) {
        return StringUtil.isNotBlank(cs);
    }

    /**
     * 检查是否有任意一个字符序列为空白。
     *
     * @param css 要检查的字符序列数组
     * @return 如果任意一个字符序列为空白，则返回 true
     */
    public static boolean isAnyBlank(final CharSequence... css) {
        return StringUtil.isAnyBlank(css);
    }

    /**
     * 检查是否所有字符序列都不为空白。
     *
     * @param css 要检查的字符序列数组
     * @return 如果所有字符序列都不为空白，则返回 true
     */
    public static boolean isNoneBlank(final CharSequence... css) {
        return StringUtil.isNoneBlank(css);
    }

    /**
     * 判断给定对象是否为数组（对象数组或基本类型数组）。
     *
     * @param obj 要检查的对象
     * @return 如果对象是数组则返回 true
     */
    public static boolean isArray(@Nullable Object obj) {
        return ObjectUtil.isArray(obj);
    }

    /**
     * 判断给定对象是否为空，即为 null 或长度为零。
     *
     * @param obj 要检查的对象
     * @return 如果对象为空则返回 true
     */
    public static boolean isEmpty(@Nullable Object obj) {
        return ObjectUtil.isEmpty(obj);
    }

    /**
     * 判断给定对象是否不为空，即不为 null 且长度不为零。
     *
     * @param obj 要检查的对象
     * @return 如果对象不为空则返回 true
     */
    public static boolean isNotEmpty(@Nullable Object obj) {
        return !ObjectUtil.isEmpty(obj);
    }

    /**
     * 判断给定数组是否为空，即为 null 或长度为零。
     *
     * @param array 要检查的数组
     * @return 如果数组为空则返回 true
     */
    public static boolean isEmpty(@Nullable Object[] array) {
        return ObjectUtil.isEmpty(array);
    }

    /**
     * 判断给定数组是否不为空，即不为 null 且长度不为零。
     *
     * @param array 要检查的数组
     * @return 如果数组不为空则返回 true
     */
    public static boolean isNotEmpty(@Nullable Object[] array) {
        return ObjectUtil.isNotEmpty(array);
    }

    /**
     * 判断对象组中是否存在任意一个为空的对象。
     *
     * @param os 要检查的对象组
     * @return 如果存在任意一个空对象则返回 true
     */
    public static boolean hasEmpty(Object... os) {
        for (Object o : os) {
            if (isEmpty(o)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断对象组中是否所有对象都为空。
     *
     * @param os 要检查的对象组
     * @return 如果所有对象都为空则返回 true
     */
    public static boolean allEmpty(Object... os) {
        for (Object o : os) {
            if (!isEmpty(o)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 比较两个对象是否相等。
     * 相同的条件有两个，满足其一即可：
     *
     * @param obj1 对象1
     * @param obj2 对象2
     * @return 如果两个对象相等则返回 true
     */
    public static boolean equals(Object obj1, Object obj2) {
        return Objects.equals(obj1, obj2);
    }

    /**
     * 判断给定的对象是否相等，如果两个对象都为 null 则返回 true，
     * 如果只有一个为 null 则返回 false。
     * 
     * <p>对于数组，使用 Arrays.equals 进行比较，基于数组元素而不是数组引用进行相等性检查。
     *
     * @param o1 第一个要比较的对象
     * @param o2 第二个要比较的对象
     * @return 如果两个对象相等则返回 true
     * @see Object#equals(Object)
     * @see Arrays#equals
     */
    public static boolean equalsSafe(@Nullable Object o1, @Nullable Object o2) {
        return ObjectUtil.nullSafeEquals(o1, o2);
    }

    /**
     * 检查给定数组是否包含指定元素。
     *
     * @param array 要检查的数组
     * @param element 要查找的元素
     * @param <T> 泛型标记
     * @return 如果找到元素则返回 true
     */
    public static <T> boolean contains(@Nullable T[] array, final T element) {
        return CollectionUtil.contains(array, element);
    }

    /**
     * 检查给定迭代器是否包含指定元素。
     *
     * @param iterator 要检查的迭代器
     * @param element 要查找的元素
     * @return 如果找到元素则返回 true
     */
    public static boolean contains(@Nullable Iterator<?> iterator, Object element) {
        return CollectionUtil.contains(iterator, element);
    }

    /**
     * 检查给定枚举是否包含指定元素。
     *
     * @param enumeration 要检查的枚举
     * @param element 要查找的元素
     * @return 如果找到元素则返回 true
     */
    public static boolean contains(@Nullable Enumeration<?> enumeration, Object element) {
        return CollectionUtil.contains(enumeration, element);
    }

    /**
     * 将对象转换为字符串，并去掉多余空格。
     *
     * @param str 要转换的对象
     * @return 转换后的字符串
     */
    public static String toStr(Object str) {
        return toStr(str, "");
    }

    /**
     * 将对象转换为字符串，并去掉多余空格。
     *
     * @param str 要转换的对象
     * @param defaultValue 默认值
     * @return 转换后的字符串，如果对象为 null 则返回默认值
     */
    public static String toStr(Object str, String defaultValue) {
        if (null == str) {
            return defaultValue;
        }
        return String.valueOf(str);
    }

    /**
     * 判断一个字符序列是否全部由数字组成。
     *
     * @param cs 要检查的字符序列，可以为 null
     * @return 如果字符序列全部由数字组成则返回 true
     */
    public static boolean isNumeric(final CharSequence cs) {
        return StringUtil.isNumeric(cs);
    }

    /**
     * 将字符串转换为整数，如果转换失败则返回 0。
     * 
     * <p>如果字符串为 null，则返回 0。
     * 
     * <pre>
     * $.toInt(null) = 0
     * $.toInt("")   = 0
     * $.toInt("1")  = 1
     * </pre>
     *
     * @param value 要转换的字符串
     * @return 转换后的整数，如果转换失败则返回 0
     */
    public static int toInt(final Object value) {
        return NumberUtil.toInt(String.valueOf(value));
    }

    /**
     * 将字符串转换为整数，如果转换失败则返回指定的默认值。
     * 
     * <p>如果字符串为 null，则返回默认值。
     * 
     * <pre>
     * $.toInt(null, 1) = 1
     * $.toInt("", 1)   = 1
     * $.toInt("1", 0)  = 1
     * </pre>
     *
     * @param value 要转换的字符串
     * @param defaultValue 默认值
     * @return 转换后的整数，如果转换失败则返回默认值
     */
    public static int toInt(final Object value, final int defaultValue) {
        return NumberUtil.toInt(String.valueOf(value), defaultValue);
    }

    /**
     * 将字符串转换为长整数，如果转换失败则返回 0。
     * 
     * <p>如果字符串为 null，则返回 0。
     * 
     * <pre>
     * $.toLong(null) = 0L
     * $.toLong("")   = 0L
     * $.toLong("1")  = 1L
     * </pre>
     *
     * @param value 要转换的字符串
     * @return 转换后的长整数，如果转换失败则返回 0
     */
    public static long toLong(final Object value) {
        return NumberUtil.toLong(String.valueOf(value));
    }

    /**
     * 将字符串转换为长整数，如果转换失败则返回指定的默认值。
     * 
     * <p>如果字符串为 null，则返回默认值。
     * 
     * <pre>
     * $.toLong(null, 1L) = 1L
     * $.toLong("", 1L)   = 1L
     * $.toLong("1", 0L)  = 1L
     * </pre>
     *
     * @param value 要转换的字符串
     * @param defaultValue 默认值
     * @return 转换后的长整数，如果转换失败则返回默认值
     */
    public static long toLong(final Object value, final long defaultValue) {
        return NumberUtil.toLong(String.valueOf(value), defaultValue);
    }

    /**
     * 将字符串转换为双精度浮点数，如果转换失败则返回 -1.00。
     * 
     * <p>如果字符串为 null，则返回默认值。
     * 
     * <pre>
     * $.toDouble(null, 1) = 1.0
     * $.toDouble("", 1)   = 1.0
     * $.toDouble("1", 0)  = 1.0
     * </pre>
     *
     * @param value 要转换的字符串
     * @return 转换后的双精度浮点数，如果转换失败则返回 -1.00
     */
    public static Double toDouble(Object value) {
        return toDouble(String.valueOf(value), -1.00);
    }

    /**
     * 将字符串转换为双精度浮点数，如果转换失败则返回指定的默认值。
     * 
     * <p>如果字符串为 null，则返回默认值。
     * 
     * <pre>
     * $.toDouble(null, 1) = 1.0
     * $.toDouble("", 1)   = 1.0
     * $.toDouble("1", 0)  = 1.0
     * </pre>
     *
     * @param value 要转换的字符串
     * @param defaultValue 默认值
     * @return 转换后的双精度浮点数，如果转换失败则返回默认值
     */
    public static Double toDouble(Object value, Double defaultValue) {
        return NumberUtil.toDouble(String.valueOf(value), defaultValue);
    }

    /**
     * 将字符串转换为单精度浮点数，如果转换失败则返回 -1.0f。
     * 
     * <p>如果字符串为 null，则返回默认值。
     * 
     * <pre>
     * $.toFloat(null, 1) = 1.00f
     * $.toFloat("", 1)   = 1.00f
     * $.toFloat("1", 0)  = 1.00f
     * </pre>
     *
     * @param value 要转换的字符串
     * @return 转换后的单精度浮点数，如果转换失败则返回 -1.0f
     */
    public static Float toFloat(Object value) {
        return toFloat(String.valueOf(value), -1.0f);
    }

    /**
     * 将字符串转换为单精度浮点数，如果转换失败则返回指定的默认值。
     * 
     * <p>如果字符串为 null，则返回默认值。
     * 
     * <pre>
     * $.toFloat(null, 1) = 1.00f
     * $.toFloat("", 1)   = 1.00f
     * $.toFloat("1", 0)  = 1.00f
     * </pre>
     *
     * @param value 要转换的字符串
     * @param defaultValue 默认值
     * @return 转换后的单精度浮点数，如果转换失败则返回默认值
     */
    public static Float toFloat(Object value, Float defaultValue) {
        return NumberUtil.toFloat(String.valueOf(value), defaultValue);
    }

    /**
     * 将字符串转换为布尔值，如果转换失败则返回 null。
     * 
     * <p>如果字符串为 null，则返回默认值。
     * 
     * <pre>
     * $.toBoolean("true", true)  = true
     * $.toBoolean("false")       = false
     * $.toBoolean("", false)     = false
     * </pre>
     *
     * @param value 要转换的字符串
     * @return 转换后的布尔值，如果转换失败则返回 null
     */
    public static Boolean toBoolean(Object value) {
        return toBoolean(value, null);
    }

    /**
     * 将字符串转换为布尔值，如果转换失败则返回指定的默认值。
     * 
     * <p>如果字符串为 null，则返回默认值。
     * 
     * <pre>
     * $.toBoolean("true", true)  = true
     * $.toBoolean("false")       = false
     * $.toBoolean("", false)     = false
     * </pre>
     *
     * @param value 要转换的字符串
     * @param defaultValue 默认值
     * @return 转换后的布尔值，如果转换失败则返回默认值
     */
    public static Boolean toBoolean(Object value, Boolean defaultValue) {
        if (value != null) {
            String val = String.valueOf(value);
            val = val.toLowerCase().trim();
            return Boolean.parseBoolean(val);
        }
        return defaultValue;
    }

    /**
     * 将字符串转换为整数数组。
     * 使用逗号作为默认分隔符。
     *
     * @param str 要转换的字符串
     * @return 转换后的整数数组
     */
    public static Integer[] toIntArray(String str) {
        return toIntArray(",", str);
    }

    /**
     * 将字符串转换为整数数组。
     *
     * @param split 分隔符
     * @param str 要转换的字符串
     * @return 转换后的整数数组
     */
    public static Integer[] toIntArray(String split, String str) {
        if (StringUtil.isEmpty(str)) {
            return new Integer[]{};
        }
        String[] arr = str.split(split);
        final Integer[] ints = new Integer[arr.length];
        for (int i = 0; i < arr.length; i++) {
            final int v = toInt(arr[i], 0);
            ints[i] = v;
        }
        return ints;
    }

    /**
     * 将字符串转换为整数列表。
     * 使用逗号作为默认分隔符。
     *
     * @param str 要转换的字符串
     * @return 转换后的整数列表
     */
    public static List<Integer> toIntList(String str) {
        return Arrays.asList(toIntArray(str));
    }

    /**
     * 将字符串转换为整数列表。
     *
     * @param split 分隔符
     * @param str 要转换的字符串
     * @return 转换后的整数列表
     */
    public static List<Integer> toIntList(String split, String str) {
        return Arrays.asList(toIntArray(split, str));
    }

    /**
     * 将字符串转换为长整数数组。
     * 使用逗号作为默认分隔符。
     *
     * @param str 要转换的字符串
     * @return 转换后的长整数数组
     */
    public static Long[] toLongArray(String str) {
        return toLongArray(",", str);
    }

    /**
     * 将字符串转换为长整数数组。
     *
     * @param split 分隔符
     * @param str 要转换的字符串
     * @return 转换后的长整数数组
     */
    public static Long[] toLongArray(String split, String str) {
        if (StringUtil.isEmpty(str)) {
            return new Long[]{};
        }
        String[] arr = str.split(split);
        final Long[] longs = new Long[arr.length];
        for (int i = 0; i < arr.length; i++) {
            final long v = toLong(arr[i], 0);
            longs[i] = v;
        }
        return longs;
    }

    /**
     * 将字符串转换为长整数列表。
     * 使用逗号作为默认分隔符。
     *
     * @param str 要转换的字符串
     * @return 转换后的长整数列表
     */
    public static List<Long> toLongList(String str) {
        return Arrays.asList(toLongArray(str));
    }

    /**
     * 将字符串转换为长整数列表。
     *
     * @param split 分隔符
     * @param str 要转换的字符串
     * @return 转换后的长整数列表
     */
    public static List<Long> toLongList(String split, String str) {
        return Arrays.asList(toLongArray(split, str));
    }

    /**
     * 将字符串转换为字符串数组。
     * 使用逗号作为默认分隔符。
     *
     * @param str 要转换的字符串
     * @return 转换后的字符串数组
     */
    public static String[] toStrArray(String str) {
        return toStrArray(",", str);
    }

    /**
     * 将字符串转换为字符串数组。
     *
     * @param split 分隔符
     * @param str 要转换的字符串
     * @return 转换后的字符串数组
     */
    public static String[] toStrArray(String split, String str) {
        if (isBlank(str)) {
            return new String[]{};
        }
        return str.split(split);
    }

    /**
     * 将字符串转换为字符串列表。
     * 使用逗号作为默认分隔符。
     *
     * @param str 要转换的字符串
     * @return 转换后的字符串列表
     */
    public static List<String> toStrList(String str) {
        return Arrays.asList(toStrArray(str));
    }

    /**
     * 将字符串转换为字符串列表。
     *
     * @param split 分隔符
     * @param str 要转换的字符串
     * @return 转换后的字符串列表
     */
    public static List<String> toStrList(String split, String str) {
        return Arrays.asList(toStrArray(split, str));
    }

    /**
     * 将长整数转换为 62 进制的短字符串。
     *
     * @param num 要转换的数字
     * @return 转换后的 62 进制字符串
     */
    public static String to62String(long num) {
        return NumberUtil.to62String(num);
    }

    /**
     * 将集合转换为分隔的字符串（例如 CSV 格式）。
     * 使用默认的分隔符。
     * 
     * <p>此方法对于实现 toString() 方法很有用。
     *
     * @param coll 要转换的集合
     * @return 转换后的分隔字符串
     */
    public static String join(Collection<?> coll) {
        return StringUtil.join(coll);
    }

    /**
     * 将集合转换为分隔的字符串（例如 CSV 格式）。
     * 
     * <p>此方法对于实现 toString() 方法很有用。
     *
     * @param coll 要转换的集合
     * @param delim 分隔符（通常是逗号 ","）
     * @return 转换后的分隔字符串
     */
    public static String join(Collection<?> coll, String delim) {
        return StringUtil.join(coll, delim);
    }

    /**
     * 将字符串数组转换为逗号分隔的字符串（即 CSV 格式）。
     * 
     * <p>此方法对于实现 toString() 方法很有用。
     *
     * @param arr 要显示的数组
     * @return 转换后的分隔字符串
     */
    public static String join(Object[] arr) {
        return StringUtil.join(arr);
    }

    /**
     * 将字符串数组转换为分隔的字符串（例如 CSV 格式）。
     * 
     * <p>此方法对于实现 toString() 方法很有用。
     *
     * @param arr 要显示的数组
     * @param delim 分隔符（通常是逗号 ","）
     * @return 转换后的分隔字符串
     */
    public static String join(Object[] arr, String delim) {
        return StringUtil.join(arr, delim);
    }

    /**
     * 生成 UUID。
     *
     * @return 生成的 UUID 字符串
     */
    public static String randomUUID() {
        return StringUtil.randomUUID();
    }

    /**
     * 转义 HTML 字符串以进行安全过滤。
     *
     * @param html 要转义的 HTML 字符串
     * @return 转义后的字符串
     */
    public static String escapeHtml(String html) {
        return StringUtil.escapeHtml(html);
    }

    /**
     * 生成指定长度的随机字符串。
     *
     * @param count 要生成的字符串长度
     * @return 生成的随机字符串
     */
    public static String random(int count) {
        return StringUtil.random(count);
    }

    /**
     * 生成指定长度和类型的随机字符串。
     *
     * @param count 要生成的字符串长度
     * @param randomType 随机字符串的类型
     * @return 生成的随机字符串
     */
    public static String random(int count, RandomType randomType) {
        return StringUtil.random(count, randomType);
    }

    /**
     * 计算字符串的 MD5 摘要，并返回 32 位十六进制字符串。
     *
     * @param data 要计算摘要的数据
     * @return MD5 摘要的十六进制字符串
     */
    public static String md5Hex(final String data) {
        return DigestUtil.md5Hex(data);
    }

    /**
     * 计算字节数组的 MD5 摘要，并返回十六进制字符串。
     *
     * @param bytes 要计算摘要的字节数组
     * @return MD5 摘要的十六进制字符串
     */
    public static String md5Hex(final byte[] bytes) {
        return DigestUtil.md5Hex(bytes);
    }

    /**
     * 计算字符串的 SHA-1 摘要。
     *
     * @param srcStr 要计算摘要的源字符串
     * @return SHA-1 摘要字符串
     */
    public static String sha1(String srcStr) {
        return DigestUtil.sha1(srcStr);
    }

    /**
     * 计算字符串的 SHA-256 摘要。
     *
     * @param srcStr 要计算摘要的源字符串
     * @return SHA-256 摘要字符串
     */
    public static String sha256(String srcStr) {
        return DigestUtil.sha256(srcStr);
    }

    /**
     * 计算字符串的 SHA-384 摘要。
     *
     * @param srcStr 要计算摘要的源字符串
     * @return SHA-384 摘要字符串
     */
    public static String sha384(String srcStr) {
        return DigestUtil.sha384(srcStr);
    }

    /**
     * 计算字符串的 SHA-512 摘要。
     *
     * @param srcStr 要计算摘要的源字符串
     * @return SHA-512 摘要字符串
     */
    public static String sha512(String srcStr) {
        return DigestUtil.sha512(srcStr);
    }

    /**
     * 自定义加密算法，先进行 MD5 加密，然后进行 SHA1 加密。
     *
     * @param data 要加密的字符串
     * @return 加密后的字符串
     */
    public static String encrypt(String data) {
        return DigestUtil.encrypt(data);
    }

    /**
     * 使用 Base64 编码字符串。
     * 使用默认字符集。
     *
     * @param value 要编码的字符串
     * @return 编码后的字符串
     */
    public static String encodeBase64(String value) {
        return Base64Util.encode(value);
    }

    /**
     * 使用 Base64 编码字符串。
     *
     * @param value 要编码的字符串
     * @param charset 字符集
     * @return 编码后的字符串
     */
    public static String encodeBase64(String value, Charset charset) {
        return Base64Util.encode(value, charset);
    }

    /**
     * 使用 URL 安全的 Base64 编码字符串。
     * 使用默认字符集。
     *
     * @param value 要编码的字符串
     * @return 编码后的字符串
     */
    public static String encodeBase64UrlSafe(String value) {
        return Base64Util.encodeUrlSafe(value);
    }

    /**
     * 使用 URL 安全的 Base64 编码字符串。
     *
     * @param value 要编码的字符串
     * @param charset 字符集
     * @return 编码后的字符串
     */
    public static String encodeBase64UrlSafe(String value, Charset charset) {
        return Base64Util.encodeUrlSafe(value, charset);
    }

    /**
     * 解码 Base64 编码的字符串。
     * 使用默认字符集。
     *
     * @param value 要解码的字符串
     * @return 解码后的字符串
     */
    public static String decodeBase64(String value) {
        return Base64Util.decode(value);
    }

    /**
     * 解码 Base64 编码的字符串。
     *
     * @param value 要解码的字符串
     * @param charset 字符集
     * @return 解码后的字符串
     */
    public static String decodeBase64(String value, Charset charset) {
        return Base64Util.decode(value, charset);
    }

    /**
     * 解码 URL 安全的 Base64 编码字符串。
     * 使用默认字符集。
     *
     * @param value 要解码的字符串
     * @return 解码后的字符串
     */
    public static String decodeBase64UrlSafe(String value) {
        return Base64Util.decodeUrlSafe(value);
    }

    /**
     * 解码 URL 安全的 Base64 编码字符串。
     *
     * @param value 要解码的字符串
     * @param charset 字符集
     * @return 解码后的字符串
     */
    public static String decodeBase64UrlSafe(String value, Charset charset) {
        return Base64Util.decodeUrlSafe(value, charset);
    }

    /**
     * 安静地关闭可关闭的资源。
     * 不会抛出任何异常。
     *
     * @param closeable 要关闭的资源
     */
    public static void closeQuietly(@Nullable Closeable closeable) {
        IoUtil.closeQuietly(closeable);
    }

    /**
     * 将输入流转换为字符串。
     * 使用 UTF-8 字符集。
     *
     * @param input 要读取的输入流
     * @return 转换后的字符串
     * @throws NullPointerException 如果输入流为 null
     */
    public static String toString(InputStream input) {
        return IoUtil.toString(input);
    }

    /**
     * 将输入流转换为字符串。
     *
     * @param input 要读取的输入流
     * @param charset 字符集
     * @return 转换后的字符串
     * @throws NullPointerException 如果输入流为 null
     */
    public static String toString(@Nullable InputStream input, Charset charset) {
        return IoUtil.toString(input, charset);
    }

    /**
     * 将输入流转换为字节数组。
     *
     * @param input 要读取的输入流
     * @return 转换后的字节数组
     */
    public static byte[] toByteArray(@Nullable InputStream input) {
        return IoUtil.toByteArray(input);
    }

    /**
     * 将对象序列化为 JSON 字符串。
     *
     * @param object 要序列化的 Java 对象
     * @return 序列化后的 JSON 字符串
     */
    public static String toJson(Object object) {
        return JsonUtil.toJson(object);
    }

    /**
     * 将对象序列化为 JSON 字节数组。
     *
     * @param object 要序列化的 Java 对象
     * @return 序列化后的 JSON 字节数组
     */
    public static byte[] toJsonAsBytes(Object object) {
        return JsonUtil.toJsonAsBytes(object);
    }

    /**
     * 将 JSON 字符串解析为 JsonNode 对象。
     *
     * @param jsonString JSON 字符串
     * @return 解析后的 JsonNode 对象
     */
    public static JsonNode readTree(String jsonString) {
        return JsonUtil.readTree(jsonString);
    }

    /**
     * 从输入流中读取并解析 JSON 为 JsonNode 对象。
     *
     * @param in 包含 JSON 的输入流
     * @return 解析后的 JsonNode 对象
     */
    public static JsonNode readTree(InputStream in) {
        return JsonUtil.readTree(in);
    }

    /**
     * 从字节数组中读取并解析 JSON 为 JsonNode 对象。
     *
     * @param content 包含 JSON 的字节数组
     * @return 解析后的 JsonNode 对象
     */
    public static JsonNode readTree(byte[] content) {
        return JsonUtil.readTree(content);
    }

    /**
     * 从 JsonParser 中读取并解析 JSON 为 JsonNode 对象。
     *
     * @param jsonParser JSON 解析器
     * @return 解析后的 JsonNode 对象
     */
    public static JsonNode readTree(JsonParser jsonParser) {
        return JsonUtil.readTree(jsonParser);
    }

    /**
     * 将 JSON 字节数组反序列化为指定类型的对象。
     *
     * @param bytes JSON 字节数组
     * @param valueType 目标类型的 Class 对象
     * @param <T> 目标类型
     * @return 反序列化后的对象
     */
    public static <T> T parse(byte[] bytes, Class<T> valueType) {
        return JsonUtil.parse(bytes, valueType);
    }

    /**
     * 将 JSON 字符串反序列化为指定类型的对象。
     *
     * @param jsonString JSON 字符串
     * @param valueType 目标类型的 Class 对象
     * @param <T> 目标类型
     * @return 反序列化后的对象
     */
    public static <T> T parse(String jsonString, Class<T> valueType) {
        return JsonUtil.parse(jsonString, valueType);
    }

    /**
     * 从输入流中读取 JSON 并反序列化为指定类型的对象。
     *
     * @param in 包含 JSON 的输入流
     * @param valueType 目标类型的 Class 对象
     * @param <T> 目标类型
     * @return 反序列化后的对象
     */
    public static <T> T parse(InputStream in, Class<T> valueType) {
        return JsonUtil.parse(in, valueType);
    }

    /**
     * 将 JSON 字节数组反序列化为指定泛型类型的对象。
     *
     * @param bytes JSON 字节数组
     * @param typeReference 目标类型的类型引用
     * @param <T> 目标类型
     * @return 反序列化后的对象
     */
    public static <T> T parse(byte[] bytes, TypeReference<T> typeReference) {
        return JsonUtil.parse(bytes, typeReference);
    }

    /**
     * 将 JSON 字符串反序列化为指定泛型类型的对象。
     *
     * @param jsonString JSON 字符串
     * @param typeReference 目标类型的类型引用
     * @param <T> 目标类型
     * @return 反序列化后的对象
     */
    public static <T> T parse(String jsonString, TypeReference<T> typeReference) {
        return JsonUtil.parse(jsonString, typeReference);
    }

    /**
     * 从输入流中读取 JSON 并反序列化为指定泛型类型的对象。
     *
     * @param in 包含 JSON 的输入流
     * @param typeReference 目标类型的类型引用
     * @param <T> 目标类型
     * @return 反序列化后的对象
     */
    public static <T> T parse(InputStream in, TypeReference<T> typeReference) {
        return JsonUtil.parse(in, typeReference);
    }

    /**
     * 对 URI 中的所有非法字符或具有特殊含义的字符进行编码。
     * 
     * <p>根据 <a href="https://tools.ietf.org/html/rfc3986">RFC 3986</a> 的定义，
     * 此方法会编码所有在 URI 中非法或具有特殊含义的字符。这样可以确保给定的字符串
     * 被完整保留，且不会影响 URI 的结构或含义。
     *
     * @param source 要编码的字符串
     * @return 编码后的字符串
     */
    public static String encode(String source) {
        return UrlUtil.encode(source, Charsets.UTF_8);
    }

    /**
     * 对 URI 中的所有非法字符或具有特殊含义的字符进行编码。
     * 
     * <p>根据 <a href="https://tools.ietf.org/html/rfc3986">RFC 3986</a> 的定义，
     * 此方法会编码所有在 URI 中非法或具有特殊含义的字符。这样可以确保给定的字符串
     * 被完整保留，且不会影响 URI 的结构或含义。
     *
     * @param source 要编码的字符串
     * @param charset 字符编码
     * @return 编码后的字符串
     */
    public static String encode(String source, Charset charset) {
        return UrlUtil.encode(source, charset);
    }

    /**
     * 解码已编码的 URI 组件。
     * 
     * <p>有关解码规则，请参见 {@link StringUtils#uriDecode(String, Charset)}。
     *
     * @param source 要解码的字符串
     * @return 解码后的值
     * @throws IllegalArgumentException 如果给定的源字符串包含无效的编码序列
     * @see StringUtils#uriDecode(String, Charset)
     * @see java.net.URLDecoder#decode(String, String)
     */
    public static String decode(String source) {
        return StringUtils.uriDecode(source, Charsets.UTF_8);
    }

    /**
     * 解码已编码的 URI 组件。
     * 
     * <p>有关解码规则，请参见 {@link StringUtils#uriDecode(String, Charset)}。
     *
     * @param source 要解码的字符串
     * @param charset 字符编码
     * @return 解码后的值
     * @throws IllegalArgumentException 如果给定的源字符串包含无效的编码序列
     * @see StringUtils#uriDecode(String, Charset)
     * @see java.net.URLDecoder#decode(String, String)
     */
    public static String decode(String source, Charset charset) {
        return StringUtils.uriDecode(source, charset);
    }

    /**
     * 格式化日期时间。
     * 使用默认的日期时间格式。
     *
     * @param date 要格式化的日期
     * @return 格式化后的日期时间字符串
     */
    public static String formatDateTime(Date date) {
        return DateUtil.formatDateTime(date);
    }

    /**
     * 格式化日期。
     * 使用默认的日期格式。
     *
     * @param date 要格式化的日期
     * @return 格式化后的日期字符串
     */
    public static String formatDate(Date date) {
        return DateUtil.formatDate(date);
    }

    /**
     * 格式化时间。
     * 使用默认的时间格式。
     *
     * @param date 要格式化的日期
     * @return 格式化后的时间字符串
     */
    public static String formatTime(Date date) {
        return DateUtil.formatTime(date);
    }

    /**
     * 使用指定的模式格式化日期。
     *
     * @param date 要格式化的日期
     * @param pattern 日期格式模式
     * @return 格式化后的日期字符串
     */
    public static String format(Date date, String pattern) {
        return DateUtil.format(date, pattern);
    }

    /**
     * 将字符串解析为日期。
     *
     * @param dateStr 要解析的日期字符串
     * @param pattern 日期格式模式
     * @return 解析后的日期对象
     */
    public static Date parseDate(String dateStr, String pattern) {
        return DateUtil.parse(dateStr, pattern);
    }

    /**
     * 将字符串解析为日期。
     *
     * @param dateStr 要解析的日期字符串
     * @param format 日期格式对象
     * @return 解析后的日期对象
     */
    public static Date parse(String dateStr, ConcurrentDateFormat format) {
        return DateUtil.parse(dateStr, format);
    }

    /**
     * 格式化日期时间。
     * 使用默认的日期时间格式。
     *
     * @param temporal 要格式化的时间对象
     * @return 格式化后的日期时间字符串
     */
    public static String formatDateTime(TemporalAccessor temporal) {
        return DateTimeUtil.formatDateTime(temporal);
    }

    /**
     * 格式化日期。
     * 使用默认的日期格式。
     *
     * @param temporal 要格式化的时间对象
     * @return 格式化后的日期字符串
     */
    public static String formatDate(TemporalAccessor temporal) {
        return DateTimeUtil.formatDate(temporal);
    }

    /**
     * 格式化时间。
     * 使用默认的时间格式。
     *
     * @param temporal 要格式化的时间对象
     * @return 格式化后的时间字符串
     */
    public static String formatTime(TemporalAccessor temporal) {
        return DateTimeUtil.formatTime(temporal);
    }

    /**
     * 日期格式化
     *
     * @param temporal 时间
     * @param pattern  表达式
     * @return 格式化后的时间
     */
    public static String format(TemporalAccessor temporal, String pattern) {
        return DateTimeUtil.format(temporal, pattern);
    }

    /**
     * 将字符串转换为时间
     *
     * @param dateStr 时间字符串
     * @param pattern 表达式
     * @return 时间
     */
    public static TemporalAccessor parse(String dateStr, String pattern) {
        return DateTimeUtil.parse(dateStr, pattern);
    }

    /**
     * 将字符串转换为时间
     *
     * @param dateStr   时间字符串
     * @param formatter DateTimeFormatter
     * @return 时间
     */
    public static TemporalAccessor parse(String dateStr, DateTimeFormatter formatter) {
        return DateTimeUtil.parse(dateStr, formatter);
    }

    /**
     * 时间比较
     *
     * @param startInclusive the start instant, inclusive, not null
     * @param endExclusive   the end instant, exclusive, not null
     * @return a {@code Duration}, not null
     */
    public static Duration between(Temporal startInclusive, Temporal endExclusive) {
        return Duration.between(startInclusive, endExclusive);
    }

    /**
     * 获取方法参数信息
     *
     * @param constructor    构造器
     * @param parameterIndex 参数序号
     * @return {MethodParameter}
     */
    public static MethodParameter getMethodParameter(Constructor<?> constructor, int parameterIndex) {
        return ClassUtil.getMethodParameter(constructor, parameterIndex);
    }

    /**
     * 获取方法参数信息
     *
     * @param method         方法
     * @param parameterIndex 参数序号
     * @return {MethodParameter}
     */
    public static MethodParameter getMethodParameter(Method method, int parameterIndex) {
        return ClassUtil.getMethodParameter(method, parameterIndex);
    }

    /**
     * 获取Annotation
     *
     * @param annotatedElement AnnotatedElement
     * @param annotationType   注解类
     * @param <A>              泛型标记
     * @return {Annotation}
     */
    @Nullable
    public static <A extends Annotation> A getAnnotation(AnnotatedElement annotatedElement, Class<A> annotationType) {
        return AnnotatedElementUtils.findMergedAnnotation(annotatedElement, annotationType);
    }

    /**
     * 获取Annotation
     *
     * @param method         Method
     * @param annotationType 注解类
     * @param <A>            泛型标记
     * @return {Annotation}
     */
    @Nullable
    public static <A extends Annotation> A getAnnotation(Method method, Class<A> annotationType) {
        return ClassUtil.getAnnotation(method, annotationType);
    }

    /**
     * 获取Annotation
     *
     * @param handlerMethod  HandlerMethod
     * @param annotationType 注解类
     * @param <A>            泛型标记
     * @return {Annotation}
     */
    @Nullable
    public static <A extends Annotation> A getAnnotation(HandlerMethod handlerMethod, Class<A> annotationType) {
        return ClassUtil.getAnnotation(handlerMethod, annotationType);
    }

    /**
     * 实例化对象
     *
     * @param clazz 类
     * @param <T>   泛型标记
     * @return 对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T newInstance(Class<?> clazz) {
        return (T) BeanUtil.instantiateClass(clazz);
    }

    /**
     * 实例化对象
     *
     * @param clazzStr 类名
     * @param <T>      泛型标记
     * @return 对象
     */
    public static <T> T newInstance(String clazzStr) {
        return BeanUtil.newInstance(clazzStr);
    }

    /**
     * 获取Bean的属性
     *
     * @param bean         bean
     * @param propertyName 属性名
     * @return 属性值
     */
    public static Object getProperty(Object bean, String propertyName) {
        return BeanUtil.getProperty(bean, propertyName);
    }

    /**
     * 设置Bean属性
     *
     * @param bean         bean
     * @param propertyName 属性名
     * @param value        属性值
     */
    public static void setProperty(Object bean, String propertyName, Object value) {
        BeanUtil.setProperty(bean, propertyName, value);
    }

    /**
     * 深复制
     * <p>
     * 注意：不支持链式Bean
     *
     * @param source 源对象
     * @param <T>    泛型标记
     * @return T
     */
    public static <T> T clone(T source) {
        return BeanUtil.clone(source);
    }

    /**
     * copy 对象属性到另一个对象，默认不使用Convert
     * <p>
     * 注意：不支持链式Bean，链式用 copyProperties
     *
     * @param source 源对象
     * @param clazz  类名
     * @param <T>    泛型标记
     * @return T
     */
    public static <T> T copy(Object source, Class<T> clazz) {
        return BeanUtil.copy(source, clazz);
    }

    /**
     * 拷贝对象
     * <p>
     * 注意：不支持链式Bean，链式用 copyProperties
     *
     * @param source     源对象
     * @param targetBean 需要赋值的对象
     */
    public static void copy(Object source, Object targetBean) {
        BeanUtil.copy(source, targetBean);
    }

    /**
     * Copy the property values of the given source bean into the target class.
     * <p>Note: The source and target classes do not have to match or even be derived
     * from each other, as long as the properties match. Any bean properties that the
     * source bean exposes but the target bean does not will silently be ignored.
     * <p>This is just a convenience method. For more complex transfer needs,
     *
     * @param source the source bean
     * @param clazz  the target bean class
     * @param <T>    泛型标记
     * @return T
     * @throws BeansException if the copying failed
     */
    public static <T> T copyProperties(Object source, Class<T> clazz) throws BeansException {
        return BeanUtil.copyProperties(source, clazz);
    }

    /**
     * 将对象装成map形式
     *
     * @param bean 源对象
     * @return {Map}
     */
    public static Map<String, Object> toMap(Object bean) {
        return BeanUtil.toMap(bean);
    }

    /**
     * 将map 转为 bean
     *
     * @param beanMap   map
     * @param valueType 对象类型
     * @param <T>       泛型标记
     * @return {T}
     */
    public static <T> T toBean(Map<String, Object> beanMap, Class<T> valueType) {
        return BeanUtil.toBean(beanMap, valueType);
    }

}
