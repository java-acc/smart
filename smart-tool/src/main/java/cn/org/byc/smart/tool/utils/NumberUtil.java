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
import org.springframework.util.NumberUtils;

/**
 * 数字工具类，提供丰富的数字处理和转换功能。
 * 
 * <p>主要功能包括：
 * <ul>
 *   <li>字符串与数字类型的转换</li>
 *   <li>数字格式化</li>
 *   <li>数字进制转换</li>
 *   <li>安全的数字转换（带默认值）</li>
 * </ul>
 * 
 * <p>特点：
 * <ul>
 *   <li>提供安全的类型转换方法</li>
 *   <li>支持多种数字类型（Integer、Long、Double、Float）</li>
 *   <li>支持自定义默认值</li>
 *   <li>支持进制转换（如62进制）</li>
 * </ul>
 * 
 * <p>使用示例：
 * <pre>{@code
 * // 基本类型转换
 * int num1 = NumberUtil.toInt("123");  // 返回 123
 * int num2 = NumberUtil.toInt(null, 0);  // 返回默认值 0
 * 
 * // 浮点数转换
 * double d1 = NumberUtil.toDouble("123.45");  // 返回 123.45
 * double d2 = NumberUtil.toDouble(null, 0.0);  // 返回默认值 0.0
 * 
 * // 62进制转换
 * String str = NumberUtil.to62String(12345L);  // 转换为62进制字符串
 * }</pre>
 *
 * @author Ken
 * @since 1.0.0
 */
public class NumberUtil extends NumberUtils {

    //-----------------------------------------------------------------------

    /**
     * 将字符串转换为整数，如果转换失败则返回默认值 -1。
     * 
     * <p>转换规则：
     * <ul>
     *   <li>如果字符串为 null，返回默认值</li>
     *   <li>如果字符串为空，返回默认值</li>
     *   <li>如果字符串不是有效的数字格式，返回默认值</li>
     * </ul>
     * 
     * <p>使用示例：
     * <pre>{@code
     * int num1 = NumberUtil.toInt(null);     // 返回 -1
     * int num2 = NumberUtil.toInt("");       // 返回 -1
     * int num3 = NumberUtil.toInt("123");    // 返回 123
     * }</pre>
     *
     * @param str 要转换的字符串
     * @return 转换后的整数，如果转换失败则返回 -1
     */
    public static int toInt(final String str) {
        return toInt(str, -1);
    }

    /**
     * 将字符串转换为整数，如果转换失败则返回指定的默认值。
     * 
     * <p>转换规则：
     * <ul>
     *   <li>如果字符串为 null，返回默认值</li>
     *   <li>如果字符串为空，返回默认值</li>
     *   <li>如果字符串不是有效的数字格式，返回默认值</li>
     * </ul>
     * 
     * <p>使用示例：
     * <pre>{@code
     * int num1 = NumberUtil.toInt(null, 1);    // 返回 1
     * int num2 = NumberUtil.toInt("", 1);      // 返回 1
     * int num3 = NumberUtil.toInt("123", 0);   // 返回 123
     * }</pre>
     *
     * @param str 要转换的字符串
     * @param defaultValue 转换失败时返回的默认值
     * @return 转换后的整数，如果转换失败则返回默认值
     */
    public static int toInt(@Nullable final String str, final int defaultValue) {
        if (str == null) {
            return defaultValue;
        }
        try {
            return Integer.valueOf(str);
        } catch (final NumberFormatException nfe) {
            return defaultValue;
        }
    }

    /**
     * 将字符串转换为长整数，如果转换失败则返回 0L。
     * 
     * <p>转换规则：
     * <ul>
     *   <li>如果字符串为 null，返回 0L</li>
     *   <li>如果字符串为空，返回 0L</li>
     *   <li>如果字符串不是有效的数字格式，返回 0L</li>
     * </ul>
     * 
     * <p>使用示例：
     * <pre>{@code
     * long num1 = NumberUtil.toLong(null);     // 返回 0L
     * long num2 = NumberUtil.toLong("");       // 返回 0L
     * long num3 = NumberUtil.toLong("123");    // 返回 123L
     * }</pre>
     *
     * @param str 要转换的字符串
     * @return 转换后的长整数，如果转换失败则返回 0L
     */
    public static long toLong(final String str) {
        return toLong(str, 0L);
    }

    /**
     * 将字符串转换为长整数，如果转换失败则返回指定的默认值。
     * 
     * <p>转换规则：
     * <ul>
     *   <li>如果字符串为 null，返回默认值</li>
     *   <li>如果字符串为空，返回默认值</li>
     *   <li>如果字符串不是有效的数字格式，返回默认值</li>
     * </ul>
     * 
     * <p>使用示例：
     * <pre>{@code
     * long num1 = NumberUtil.toLong(null, 1L);    // 返回 1L
     * long num2 = NumberUtil.toLong("", 1L);      // 返回 1L
     * long num3 = NumberUtil.toLong("123", 0L);   // 返回 123L
     * }</pre>
     *
     * @param str 要转换的字符串
     * @param defaultValue 转换失败时返回的默认值
     * @return 转换后的长整数，如果转换失败则返回默认值
     */
    public static long toLong(@Nullable final String str, final long defaultValue) {
        if (str == null) {
            return defaultValue;
        }
        try {
            return Long.valueOf(str);
        } catch (final NumberFormatException nfe) {
            return defaultValue;
        }
    }

    /**
     * 将字符串转换为双精度浮点数。
     * 
     * <p>转换规则：
     * <ul>
     *   <li>如果字符串为 null，返回 null</li>
     *   <li>如果字符串为空，返回 null</li>
     *   <li>如果字符串不是有效的数字格式，返回 null</li>
     * </ul>
     * 
     * <p>使用示例：
     * <pre>{@code
     * Double num1 = NumberUtil.toDouble(null);         // 返回 null
     * Double num2 = NumberUtil.toDouble("123.45");     // 返回 123.45
     * }</pre>
     *
     * @param value 要转换的字符串
     * @return 转换后的双精度浮点数，如果转换失败则返回 null
     */
    public static Double toDouble(String value) {
        return toDouble(value, null);
    }

    /**
     * 将字符串转换为双精度浮点数，如果转换失败则返回指定的默认值。
     * 
     * <p>转换规则：
     * <ul>
     *   <li>如果字符串为 null，返回默认值</li>
     *   <li>如果字符串为空，返回默认值</li>
     *   <li>如果字符串不是有效的数字格式，返回默认值</li>
     * </ul>
     * 
     * <p>使用示例：
     * <pre>{@code
     * Double num1 = NumberUtil.toDouble(null, 1.0);        // 返回 1.0
     * Double num2 = NumberUtil.toDouble("123.45", 0.0);    // 返回 123.45
     * }</pre>
     *
     * @param value 要转换的字符串
     * @param defaultValue 转换失败时返回的默认值
     * @return 转换后的双精度浮点数，如果转换失败则返回默认值
     */
    public static Double toDouble(@Nullable String value, Double defaultValue) {
        if (value != null) {
            return Double.valueOf(value.trim());
        }
        return defaultValue;
    }

    /**
     * 将字符串转换为单精度浮点数。
     * 
     * <p>转换规则：
     * <ul>
     *   <li>如果字符串为 null，返回 null</li>
     *   <li>如果字符串为空，返回 null</li>
     *   <li>如果字符串不是有效的数字格式，返回 null</li>
     * </ul>
     * 
     * <p>使用示例：
     * <pre>{@code
     * Float num1 = NumberUtil.toFloat(null);         // 返回 null
     * Float num2 = NumberUtil.toFloat("123.45");     // 返回 123.45f
     * }</pre>
     *
     * @param value 要转换的字符串
     * @return 转换后的单精度浮点数，如果转换失败则返回 null
     */
    public static Float toFloat(String value) {
        return toFloat(value, null);
    }

    /**
     * 将字符串转换为单精度浮点数，如果转换失败则返回指定的默认值。
     * 
     * <p>转换规则：
     * <ul>
     *   <li>如果字符串为 null，返回默认值</li>
     *   <li>如果字符串为空，返回默认值</li>
     *   <li>如果字符串不是有效的数字格式，返回默认值</li>
     * </ul>
     * 
     * <p>使用示例：
     * <pre>{@code
     * Float num1 = NumberUtil.toFloat(null, 1.0f);        // 返回 1.0f
     * Float num2 = NumberUtil.toFloat("123.45", 0.0f);    // 返回 123.45f
     * }</pre>
     *
     * @param value 要转换的字符串
     * @param defaultValue 转换失败时返回的默认值
     * @return 转换后的单精度浮点数，如果转换失败则返回默认值
     */
    public static Float toFloat(@Nullable String value, Float defaultValue) {
        if (value != null) {
            return Float.valueOf(value.trim());
        }
        return defaultValue;
    }

    /**
     * 用于表示数字的所有可能字符
     */
    private final static char[] DIGITS = {
            '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b',
            'c', 'd', 'e', 'f', 'g', 'h',
            'i', 'j', 'k', 'l', 'm', 'n',
            'o', 'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F',
            'G', 'H', 'I', 'J', 'K', 'L',
            'M', 'N', 'O', 'P', 'Q', 'R',
            'S', 'T', 'U', 'V', 'W', 'X',
            'Y', 'Z'
    };

    /**
     * 将长整数转换为62进制字符串。
     * 
     * <p>转换规则：
     * <ul>
     *   <li>使用 0-9、a-z、A-Z 共62个字符表示</li>
     *   <li>支持负数转换</li>
     *   <li>结果为短字符串</li>
     * </ul>
     * 
     * <p>使用示例：
     * <pre>{@code
     * String str1 = NumberUtil.to62String(12345L);    // 转换为62进制字符串
     * String str2 = NumberUtil.to62String(-12345L);   // 转换负数
     * }</pre>
     *
     * @param i 要转换的长整数
     * @return 转换后的62进制字符串
     */
    public static String to62String(long i) {
        int radix = DIGITS.length;
        char[] buf = new char[65];
        int charPos = 64;
        i = -i;
        while (i <= -radix) {
            buf[charPos--] = DIGITS[(int) (-(i % radix))];
            i = i / radix;
        }
        buf[charPos] = DIGITS[(int) (-i)];

        return new String(buf, charPos, (65 - charPos));
    }
}