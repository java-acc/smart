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

import cn.hutool.core.util.ObjectUtil;
import cn.org.byc.smart.tool.constant.StringPool;
import cn.org.byc.smart.tool.supports.StrFormatter;
import cn.org.byc.smart.tool.supports.StrSplitter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.util.HtmlUtils;

import java.io.StringReader;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

/**
 * 字符串工具类，提供字符串常用操作的工具方法。
 * 本工具类继承自 Spring 的 StringUtils，提供了更多的字符串处理功能。
 * 
 * 主要功能包括：
 * - 字符串判空和检查
 * - 字符串截取和分割
 * - 字符串格式化和转换
 * - 字符串查找和替换
 * - 大小写转换
 * - 驼峰和下划线命名转换
 * 
 * @author Ken
 * @since 1.0
 */
public class StringUtil extends StringUtils {

    public static final int INDEX_NOT_FOUND = -1;

    /**
     * 检查字符序列是否为空白字符串。
     * 
     * <p>
     * 以下情况将返回 true：
     * <ul>
     *   <li>输入为 null</li>
     *   <li>输入为空字符串 ""</li>
     *   <li>输入仅包含空白字符（空格、制表符等）</li>
     * </ul>
     * 
     * <p>示例：
     * <pre>{@code
     * StringUtil.isBlank(null)     = true
     * StringUtil.isBlank("")       = true
     * StringUtil.isBlank(" ")      = true
     * StringUtil.isBlank("  ")     = true
     * StringUtil.isBlank("abc")    = false
     * StringUtil.isBlank(" abc ")  = false
     * }</pre>
     * 
     * @param cs 要检查的字符序列，可以为 null
     * @return 如果字符序列为 null、空串或仅包含空白字符，则返回 true
     */
    public static boolean isBlank(final CharSequence cs) {
        return !StringUtil.hasText(cs);
    }

    /**
     * 检查字符序列是否不为空白字符串。
     * 
     * <p>
     * 以下情况将返回 true：
     * <ul>
     *   <li>输入不为 null</li>
     *   <li>输入不为空字符串</li>
     *   <li>输入包含至少一个非空白字符</li>
     * </ul>
     * 
     * <p>示例：
     * <pre>{@code
     * StringUtil.isNotBlank(null)     = false
     * StringUtil.isNotBlank("")       = false
     * StringUtil.isNotBlank(" ")      = false
     * StringUtil.isNotBlank("  ")     = false
     * StringUtil.isNotBlank("abc")    = true
     * StringUtil.isNotBlank(" abc ")  = true
     * }</pre>
     * 
     * @param cs 要检查的字符序列，可以为 null
     * @return 如果字符序列不为 null 且包含至少一个非空白字符，则返回 true
     */
    public static boolean isNotBlank(final CharSequence cs) {
        return StringUtil.hasText(cs);
    }

    /**
     * 检查多个字符序列中是否存在任意一个为空白字符串。
     * 
     * <p>
     * 如果输入的字符序列数组为 null 或空数组，返回 true。
     * 只要有一个字符序列为空白，就返回 true。
     * 
     * <p>示例：
     * <pre>{@code
     * StringUtil.isAnyBlank(null)             = true
     * StringUtil.isAnyBlank(null, "abc")      = true
     * StringUtil.isAnyBlank("", "abc")        = true
     * StringUtil.isAnyBlank(" ", "abc")       = true
     * StringUtil.isAnyBlank("abc", "")        = true
     * StringUtil.isAnyBlank("abc", "  ")      = true
     * StringUtil.isAnyBlank("abc", "xyz")     = false
     * }</pre>
     * 
     * @param css 要检查的字符序列数组
     * @return 如果任意一个字符序列为空白，则返回 true
     */
    public static boolean isAnyBlank(final CharSequence... css) {
        if (ObjectUtil.isEmpty(css)) {
            return true;
        }
        return Stream.of(css).anyMatch(StringUtil::isBlank);
    }

    /**
     * 检查多个字符序列是否都不为空白字符串。
     * 
     * <p>
     * 如果输入的字符序列数组为 null 或空数组，返回 false。
     * 只有当所有字符序列都不为空白时，才返回 true。
     * 
     * <p>示例：
     * <pre>{@code
     * StringUtil.isNoneBlank(null)             = false
     * StringUtil.isNoneBlank(null, "abc")      = false
     * StringUtil.isNoneBlank("", "abc")        = false
     * StringUtil.isNoneBlank(" ", "abc")       = false
     * StringUtil.isNoneBlank("abc", "xyz")     = true
     * StringUtil.isNoneBlank("abc", "  ")      = false
     * }</pre>
     * 
     * @param css 要检查的字符序列数组
     * @return 如果所有字符序列都不为空白，则返回 true
     */
    public static boolean isNoneBlank(final CharSequence... css) {
        if (ObjectUtil.isEmpty(css)) {
            return false;
        }
        return Stream.of(css).allMatch(StringUtil::isNotBlank);
    }

    /**
     * 判断字符序列是否只包含数字字符。
     * 
     * <p>
     * 该方法会检查字符序列中的每个字符是否都是数字（0-9）。
     * 如果字符序列为 null 或空白，返回 false。
     * 
     * <p>示例：
     * <pre>{@code
     * StringUtil.isNumeric(null)     = false
     * StringUtil.isNumeric("")       = false
     * StringUtil.isNumeric(" ")      = false
     * StringUtil.isNumeric("123")    = true
     * StringUtil.isNumeric("12 3")   = false
     * StringUtil.isNumeric("ab2c")   = false
     * StringUtil.isNumeric("12-3")   = false
     * StringUtil.isNumeric("12.3")   = false
     * }</pre>
     * 
     * @param cs 要检查的字符序列，可以为 null
     * @return 如果字符序列只包含数字字符，则返回 true
     */
    public static boolean isNumeric(final CharSequence cs) {
        if (isBlank(cs)) {
            return false;
        }
        for (int i = cs.length(); --i >= 0; ) {
            int chr = cs.charAt(i);
            if (chr < 48 || chr > 57) {
                return false;
            }
        }
        return true;
    }

    /**
     * 将集合转换为以逗号分隔的字符串。
     * 
     * <p>
     * 该方法将集合中的元素使用逗号连接成一个字符串。
     * 集合中的每个元素都会调用其 toString() 方法转换为字符串。
     * 
     * <p>示例：
     * <pre>{@code
     * List<String> list = Arrays.asList("a", "b", "c");
     * StringUtil.join(list)               = "a,b,c"
     * 
     * List<Integer> numbers = Arrays.asList(1, 2, 3);
     * StringUtil.join(numbers)            = "1,2,3"
     * 
     * List<String> single = Arrays.asList("hello");
     * StringUtil.join(single)             = "hello"
     * 
     * List<String> empty = new ArrayList<>();
     * StringUtil.join(empty)              = ""
     * }</pre>
     * 
     * @param coll 要连接的集合
     * @return 连接后的字符串
     */
    public static String join(Collection<?> coll) {
        return StringUtil.collectionToCommaDelimitedString(coll);
    }

    /**
     * 将集合转换为使用指定分隔符分隔的字符串。
     * 
     * <p>
     * 该方法将集合中的元素使用指定的分隔符连接成一个字符串。
     * 集合中的每个元素都会调用其 toString() 方法转换为字符串。
     * 
     * <p>示例：
     * <pre>{@code
     * List<String> list = Arrays.asList("a", "b", "c");
     * StringUtil.join(list, ",")          = "a,b,c"
     * StringUtil.join(list, "-")          = "a-b-c"
     * StringUtil.join(list, " and ")      = "a and b and c"
     * 
     * List<Integer> numbers = Arrays.asList(1, 2, 3);
     * StringUtil.join(numbers, "|")       = "1|2|3"
     * 
     * List<String> single = Arrays.asList("hello");
     * StringUtil.join(single, ",")        = "hello"
     * }</pre>
     * 
     * @param coll  要连接的集合
     * @param delim 分隔符
     * @return 连接后的字符串
     */
    public static String join(Collection<?> coll, String delim) {
        return StringUtil.collectionToDelimitedString(coll, delim);
    }

    /**
     * 将数组转换为以逗号分隔的字符串。
     * 
     * <p>
     * 该方法将数组中的元素使用逗号连接成一个字符串。
     * 数组中的每个元素都会调用其 toString() 方法转换为字符串。
     * 
     * <p>示例：
     * <pre>{@code
     * String[] arr = {"a", "b", "c"};
     * StringUtil.join(arr)                = "a,b,c"
     * 
     * Integer[] numbers = {1, 2, 3};
     * StringUtil.join(numbers)            = "1,2,3"
     * 
     * String[] single = {"hello"};
     * StringUtil.join(single)             = "hello"
     * 
     * String[] empty = {};
     * StringUtil.join(empty)              = ""
     * }</pre>
     * 
     * @param arr 要连接的数组
     * @return 连接后的字符串
     */
    public static String join(Object[] arr) {
        return StringUtil.arrayToCommaDelimitedString(arr);
    }

    /**
     * 将数组转换为使用指定分隔符分隔的字符串。
     * 
     * <p>
     * 该方法将数组中的元素使用指定的分隔符连接成一个字符串。
     * 数组中的每个元素都会调用其 toString() 方法转换为字符串。
     * 
     * <p>示例：
     * <pre>{@code
     * String[] arr = {"a", "b", "c"};
     * StringUtil.join(arr, ",")           = "a,b,c"
     * StringUtil.join(arr, "-")           = "a-b-c"
     * StringUtil.join(arr, " and ")       = "a and b and c"
     * 
     * Integer[] numbers = {1, 2, 3};
     * StringUtil.join(numbers, "|")       = "1|2|3"
     * 
     * String[] single = {"hello"};
     * StringUtil.join(single, ",")        = "hello"
     * }</pre>
     * 
     * @param arr   要连接的数组
     * @param delim 分隔符
     * @return 连接后的字符串
     */
    public static String join(Object[] arr, String delim) {
        return StringUtil.arrayToDelimitedString(arr, delim);
    }

    /**
     * 生成一个随机的 UUID 字符串，去除了连字符。
     * 
     * <p>
     * 该方法使用 ThreadLocalRandom 生成随机的 UUID，
     * 并移除了标准 UUID 中的连字符（-），返回一个32位的字符串。
     * 
     * <p>示例：
     * <pre>{@code
     * String uuid = StringUtil.randomUUID();
     * // 输出类似：550e8400e29b41d4a716446655440000
     * System.out.println(uuid);
     * }</pre>
     * 
     * @return 32位的 UUID 字符串（不含连字符）
     */
    public static String randomUUID() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return new UUID(random.nextLong(), random.nextLong()).toString().replace(StringPool.DASH, StringPool.EMPTY);
    }

    /**
     * 转义 HTML 字符串，用于防止 XSS 攻击。
     * 
     * <p>
     * 该方法将 HTML 特殊字符转换为对应的 HTML 实体，
     * 主要用于防止跨站脚本攻击（XSS）。如果输入为 null 或空字符串，
     * 则返回空字符串。
     * 
     * <p>转义对照表：
     * <ul>
     *   <li>&amp; 转换为 &amp;amp;</li>
     *   <li>&lt; 转换为 &amp;lt;</li>
     *   <li>&gt; 转换为 &amp;gt;</li>
     *   <li>&quot; 转换为 &amp;quot;</li>
     *   <li>&#39; 转换为 &amp;#39;</li>
     * </ul>
     * 
     * <p>示例：
     * <pre>{@code
     * String html = "<script>alert('XSS')</script>";
     * String escaped = StringUtil.escapeHtml(html);
     * // 输出：&lt;script&gt;alert(&#39;XSS&#39;)&lt;/script&gt;
     * System.out.println(escaped);
     * }</pre>
     * 
     * @param html 需要转义的 HTML 字符串
     * @return 转义后的字符串
     */
    public static String escapeHtml(String html) {
        return StringUtil.isBlank(html) ? StringPool.EMPTY : HtmlUtils.htmlEscape(html);
    }

    /**
     * 清理字符串中的特殊字符和空白字符。
     * 
     * <p>
     * 该方法会移除字符串中的以下字符：
     * <ul>
     *   <li>空格字符（包括全角空格）</li>
     *   <li>制表符（\t）</li>
     *   <li>换页符（\f）</li>
     *   <li>垂直制表符（\v）</li>
     *   <li>其他空白字符（\s）</li>
     *   <li>特殊字符（`·•）</li>
     * </ul>
     * 
     * <p>示例：
     * <pre>{@code
     * String text = "Hello　World\t•`·Test";
     * String cleaned = StringUtil.cleanChars(text);
     * // 输出：HelloWorldTest
     * System.out.println(cleaned);
     * }</pre>
     * 
     * @param txt 需要清理的字符串
     * @return 清理后的字符串
     */
    public static String cleanChars(String txt) {
        return txt.replaceAll("[ 　`·•\\f\\t\\v\\s]", "");
    }


    private static final String S_INT = "0123456789";
    private static final String S_STR = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final String S_ALL = S_INT + S_STR;

    /**
     * 生成指定长度的随机字符串。
     * 默认生成包含数字和大小写字母的随机字符串。
     * 
     * <p>
     * 该方法使用 ThreadLocalRandom 生成随机字符串，
     * 字符范围包括数字（0-9）和大小写字母（a-z, A-Z）。
     * 
     * <p>示例：
     * <pre>{@code
     * String str1 = StringUtil.random(5);
     * // 可能输出：Ax7Yz
     * System.out.println(str1);
     * 
     * String str2 = StringUtil.random(10);
     * // 可能输出：7bK9pN4mJx
     * System.out.println(str2);
     * }</pre>
     * 
     * @param count 要生成的字符串长度
     * @return 生成的随机字符串
     */
    public static String random(int count) {
        return StringUtil.random(count, RandomType.ALL);
    }

    /**
     * 生成指定长度和类型的随机字符串。
     * 
     * <p>
     * 该方法使用 ThreadLocalRandom 生成随机字符串，
     * 可以指定生成的字符类型：
     * <ul>
     *   <li>INT - 仅包含数字（0-9）</li>
     *   <li>STRING - 仅包含大小写字母（a-z, A-Z）</li>
     *   <li>ALL - 包含数字和大小写字母</li>
     * </ul>
     * 
     * <p>示例：
     * <pre>{@code
     * // 生成纯数字随机字符串
     * String numbers = StringUtil.random(6, RandomType.INT);
     * // 可能输出：847591
     * System.out.println(numbers);
     * 
     * // 生成纯字母随机字符串
     * String letters = StringUtil.random(6, RandomType.STRING);
     * // 可能输出：AzBxYp
     * System.out.println(letters);
     * 
     * // 生成数字和字母混合的随机字符串
     * String mixed = StringUtil.random(6, RandomType.ALL);
     * // 可能输出：7bK9pN
     * System.out.println(mixed);
     * }</pre>
     * 
     * @param count 要生成的字符串长度
     * @param randomType 随机字符串类型
     * @return 生成的随机字符串
     * @throws IllegalArgumentException 如果 count 小于 0
     */
    public static String random(int count, RandomType randomType) {
        if (count == 0) {
            return "";
        }
        Assert.isTrue(count > 0, "Requested random string length " + count + " is less than 0.");
        final ThreadLocalRandom random = ThreadLocalRandom.current();
        char[] buffer = new char[count];
        for (int i = 0; i < count; i++) {
            if (RandomType.INT == randomType) {
                buffer[i] = S_INT.charAt(random.nextInt(S_INT.length()));
            } else if (RandomType.STRING == randomType) {
                buffer[i] = S_STR.charAt(random.nextInt(S_STR.length()));
            } else {
                buffer[i] = S_ALL.charAt(random.nextInt(S_ALL.length()));
            }
        }
        return new String(buffer);
    }

    /**
     * 使用占位符格式化字符串。
     * 
     * <p>
     * 该方法使用 {} 作为占位符，将参数按顺序替换到模板字符串中。
     * 如果需要在结果字符串中保留 {} 符号，可以使用 \\ 进行转义。
     * 
     * <p>格式化规则：
     * <ul>
     *   <li>使用 {} 作为参数占位符</li>
     *   <li>使用 \\ 转义 { 字符</li>
     *   <li>使用 \\\\ 转义 \ 字符</li>
     * </ul>
     * 
     * <p>示例：
     * <pre>{@code
     * // 基本用法
     * String result1 = StringUtil.format("Hello {}!", "World");
     * // 输出：Hello World!
     * System.out.println(result1);
     * 
     * // 多个参数
     * String result2 = StringUtil.format("{}+{}={}", 1, 2, 3);
     * // 输出：1+2=3
     * System.out.println(result2);
     * 
     * // 转义 {}
     * String result3 = StringUtil.format("Set\\{} = {}", "value");
     * // 输出：Set{} = value
     * System.out.println(result3);
     * 
     * // 转义 \
     * String result4 = StringUtil.format("Path: \\\\{}", "folder");
     * // 输出：Path: \folder
     * System.out.println(result4);
     * }</pre>
     * 
     * @param template 包含占位符的模板字符串
     * @param params 要替换占位符的参数值
     * @return 格式化后的字符串
     */
    public static String format(CharSequence template, Object... params) {
        if (null == template) {
            return null;
        }
        if (Func.isEmpty(params) || isBlank(template)) {
            return template.toString();
        }
        return StrFormatter.format(template.toString(), params);
    }

    /**
     * 使用索引占位符格式化字符串。
     * 
     * <p>
     * 该方法使用 {n} 形式的占位符（n 为参数索引，从 0 开始），
     * 可以按照指定顺序将参数替换到模板字符串中。
     * 
     * <p>格式化规则：
     * <ul>
     *   <li>使用 {0}、{1}、{2} 等作为参数占位符</li>
     *   <li>占位符索引从 0 开始</li>
     *   <li>可以多次使用同一个索引</li>
     * </ul>
     * 
     * <p>示例：
     * <pre>{@code
     * // 基本用法
     * String result1 = StringUtil.indexedFormat("Hello {0}!", "World");
     * // 输出：Hello World!
     * System.out.println(result1);
     * 
     * // 指定参数顺序
     * String result2 = StringUtil.indexedFormat("{1} + {0} = {2}", "b", "a", "c");
     * // 输出：a + b = c
     * System.out.println(result2);
     * 
     * // 重复使用参数
     * String result3 = StringUtil.indexedFormat("{0}, {1}, and {0} again", "hello", "world");
     * // 输出：hello, world, and hello again
     * System.out.println(result3);
     * }</pre>
     * 
     * @param pattern 包含索引占位符的模板字符串
     * @param arguments 要替换占位符的参数值
     * @return 格式化后的字符串
     */
    public static String indexedFormat(CharSequence pattern, Object... arguments) {
        return MessageFormat.format(pattern.toString(), arguments);
    }

    /**
     * 使用命名占位符格式化字符串。
     * 
     * <p>
     * 该方法使用 {key} 形式的占位符，通过 Map 中的键值对
     * 替换模板字符串中的占位符。
     * 
     * <p>格式化规则：
     * <ul>
     *   <li>使用 {key} 形式的占位符，其中 key 为 Map 中的键名</li>
     *   <li>如果 Map 中不存在对应的键，则保持原占位符不变</li>
     *   <li>Map 中多余的键值对会被忽略</li>
     * </ul>
     * 
     * <p>示例：
     * <pre>{@code
     * Map<String, Object> map = new HashMap<>();
     * map.put("name", "Alice");
     * map.put("age", 20);
     * 
     * // 基本用法
     * String result1 = StringUtil.format("Hello {name}!", map);
     * // 输出：Hello Alice!
     * System.out.println(result1);
     * 
     * // 多个占位符
     * String result2 = StringUtil.format("Name: {name}, Age: {age}", map);
     * // 输出：Name: Alice, Age: 20
     * System.out.println(result2);
     * 
     * // 未找到的键
     * String result3 = StringUtil.format("Hello {name}, {title}!", map);
     * // 输出：Hello Alice, {title}!
     * System.out.println(result3);
     * }</pre>
     * 
     * @param template 包含命名占位符的模板字符串
     * @param map 包含键值对的 Map
     * @return 格式化后的字符串
     */
    public static String format(CharSequence template, Map<?, ?> map) {
        if (null == template) {
            return null;
        }
        if (null == map || map.isEmpty()) {
            return template.toString();
        }

        String template2 = template.toString();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            template2 = template2.replace("{" + entry.getKey() + "}", Func.toStr(entry.getValue()));
        }
        return template2;
    }

    /**
     * 获取标识符，用于参数清理
     *
     * @param param 参数
     * @return 清理后的标识符
     */
    @Nullable
    public static String cleanIdentifier(@Nullable String param) {
        if (param == null) {
            return null;
        }
        StringBuilder paramBuilder = new StringBuilder();
        for (int i = 0; i < param.length(); i++) {
            char c = param.charAt(i);
            if (Character.isJavaIdentifierPart(c)) {
                paramBuilder.append(c);
            }
        }
        return paramBuilder.toString();
    }

    /**
     * 切分字符串，不去除切分后每个元素两边的空白符，不去除空白项
     *
     * @param str       被切分的字符串
     * @param separator 分隔符字符
     * @param limit     限制分片数，-1不限制
     * @return 切分后的集合
     */
    public static List<String> split(CharSequence str, char separator, int limit) {
        return split(str, separator, limit, false, false);
    }

    /**
     * 切分字符串，去除切分后每个元素两边的空白符，去除空白项
     *
     * @param str       被切分的字符串
     * @param separator 分隔符字符
     * @return 切分后的集合
     * @since 3.1.2
     */
    public static List<String> splitTrim(CharSequence str, char separator) {
        return splitTrim(str, separator, -1);
    }

    /**
     * 切分字符串，去除切分后每个元素两边的空白符，去除空白项
     *
     * @param str       被切分的字符串
     * @param separator 分隔符字符
     * @return 切分后的集合
     * @since 3.2.0
     */
    public static List<String> splitTrim(CharSequence str, CharSequence separator) {
        return splitTrim(str, separator, -1);
    }

    /**
     * 切分字符串，去除切分后每个元素两边的空白符，去除空白项
     *
     * @param str       被切分的字符串
     * @param separator 分隔符字符
     * @param limit     限制分片数，-1不限制
     * @return 切分后的集合
     * @since 3.1.0
     */
    public static List<String> splitTrim(CharSequence str, char separator, int limit) {
        return split(str, separator, limit, true, true);
    }

    /**
     * 切分字符串，去除切分后每个元素两边的空白符，去除空白项
     *
     * @param str       被切分的字符串
     * @param separator 分隔符字符
     * @param limit     限制分片数，-1不限制
     * @return 切分后的集合
     * @since 3.2.0
     */
    public static List<String> splitTrim(CharSequence str, CharSequence separator, int limit) {
        return split(str, separator, limit, true, true);
    }

    /**
     * 切分字符串，不限制分片数量
     *
     * @param str         被切分的字符串
     * @param separator   分隔符字符
     * @param isTrim      是否去除切分字符串后每个元素两边的空格
     * @param ignoreEmpty 是否忽略空串
     * @return 切分后的集合
     * @since 3.0.8
     */
    public static List<String> split(CharSequence str, char separator, boolean isTrim, boolean ignoreEmpty) {
        return split(str, separator, 0, isTrim, ignoreEmpty);
    }

    /**
     * 切分字符串
     *
     * @param str         被切分的字符串
     * @param separator   分隔符字符
     * @param limit       限制分片数，-1不限制
     * @param isTrim      是否去除切分字符串后每个元素两边的空格
     * @param ignoreEmpty 是否忽略空串
     * @return 切分后的集合
     * @since 3.0.8
     */
    public static List<String> split(CharSequence str, char separator, int limit, boolean isTrim, boolean ignoreEmpty) {
        if (null == str) {
            return new ArrayList<>(0);
        }
        return StrSplitter.split(str.toString(), separator, limit, isTrim, ignoreEmpty);
    }

    /**
     * 切分字符串
     *
     * @param str         被切分的字符串
     * @param separator   分隔符字符
     * @param limit       限制分片数，-1不限制
     * @param isTrim      是否去除切分字符串后每个元素两边的空格
     * @param ignoreEmpty 是否忽略空串
     * @return 切分后的集合
     * @since 3.2.0
     */
    public static List<String> split(CharSequence str, CharSequence separator, int limit, boolean isTrim, boolean ignoreEmpty) {
        if (null == str) {
            return new ArrayList<>(0);
        }
        final String separatorStr = (null == separator) ? null : separator.toString();
        return StrSplitter.split(str.toString(), separatorStr, limit, isTrim, ignoreEmpty);
    }

    /**
     * 切分字符串
     *
     * @param str       被切分的字符串
     * @param separator 分隔符
     * @return 字符串
     */
    public static String[] split(CharSequence str, CharSequence separator) {
        if (str == null) {
            return new String[]{};
        }

        final String separatorStr = (null == separator) ? null : separator.toString();
        return StrSplitter.splitToArray(str.toString(), separatorStr, 0, false, false);
    }

    /**
     * 根据给定长度，将给定字符串截取为多个部分
     *
     * @param str 字符串
     * @param len 每一个小节的长度
     * @return 截取后的字符串数组
     * @see StrSplitter#splitByLength(String, int)
     */
    public static String[] split(CharSequence str, int len) {
        if (null == str) {
            return new String[]{};
        }
        return StrSplitter.splitByLength(str.toString(), len);
    }

    /**
     * 指定字符是否在字符串中出现过
     *
     * @param str        字符串
     * @param searchChar 被查找的字符
     * @return 是否包含
     * @since 3.1.2
     */
    public static boolean contains(CharSequence str, char searchChar) {
        return indexOf(str, searchChar) > -1;
    }

    /**
     * 查找指定字符串是否包含指定字符串列表中的任意一个字符串
     *
     * @param str      指定字符串
     * @param testStrs 需要检查的字符串数组
     * @return 是否包含任意一个字符串
     * @since 3.2.0
     */
    public static boolean containsAny(CharSequence str, CharSequence... testStrs) {
        return null != getContainsStr(str, testStrs);
    }

    /**
     * 查找指定字符串是否包含指定字符串列表中的任意一个字符串，如果包含返回找到的第一个字符串
     *
     * @param str      指定字符串
     * @param testStrs 需要检查的字符串数组
     * @return 被包含的第一个字符串
     * @since 3.2.0
     */
    public static String getContainsStr(CharSequence str, CharSequence... testStrs) {
        if (isEmpty(str) || Func.isEmpty(testStrs)) {
            return null;
        }
        for (CharSequence checkStr : testStrs) {
            if (str.toString().contains(checkStr)) {
                return checkStr.toString();
            }
        }
        return null;
    }

    /**
     * 是否包含特定字符，忽略大小写，如果给定两个参数都为<code>null</code>，返回true
     *
     * @param str     被检测字符串
     * @param testStr 被测试是否包含的字符串
     * @return 是否包含
     */
    public static boolean containsIgnoreCase(CharSequence str, CharSequence testStr) {
        if (null == str) {
            // 如果被监测字符串和
            return null == testStr;
        }
        return str.toString().toLowerCase().contains(testStr.toString().toLowerCase());
    }

    /**
     * 查找指定字符串是否包含指定字符串列表中的任意一个字符串<br>
     * 忽略大小写
     *
     * @param str      指定字符串
     * @param testStrs 需要检查的字符串数组
     * @return 是否包含任意一个字符串
     * @since 3.2.0
     */
    public static boolean containsAnyIgnoreCase(CharSequence str, CharSequence... testStrs) {
        return null != getContainsStrIgnoreCase(str, testStrs);
    }

    /**
     * 查找指定字符串是否包含指定字符串列表中的任意一个字符串，如果包含返回找到的第一个字符串<br>
     * 忽略大小写
     *
     * @param str      指定字符串
     * @param testStrs 需要检查的字符串数组
     * @return 被包含的第一个字符串
     * @since 3.2.0
     */
    public static String getContainsStrIgnoreCase(CharSequence str, CharSequence... testStrs) {
        if (isEmpty(str) || Func.isEmpty(testStrs)) {
            return null;
        }
        for (CharSequence testStr : testStrs) {
            if (containsIgnoreCase(str, testStr)) {
                return testStr.toString();
            }
        }
        return null;
    }

    /**
     * 改进JDK subString<br>
     * index从0开始计算，最后一个字符为-1<br>
     * 如果from和to位置一样，返回 "" <br>
     * 如果from或to为负数，则按照length从后向前数位置，如果绝对值大于字符串长度，则from归到0，to归到length<br>
     * 如果经过修正的index中from大于to，则互换from和to example: <br>
     * abcdefgh 2 3 =》 c <br>
     * abcdefgh 2 -3 =》 cde <br>
     *
     * @param str       String
     * @param fromIndex 开始的index（包括）
     * @param toIndex   结束的index（不包括）
     * @return 字串
     */
    public static String sub(CharSequence str, int fromIndex, int toIndex) {
        if (isEmpty(str)) {
            return StringPool.EMPTY;
        }
        int len = str.length();

        if (fromIndex < 0) {
            fromIndex = len + fromIndex;
            if (fromIndex < 0) {
                fromIndex = 0;
            }
        } else if (fromIndex > len) {
            fromIndex = len;
        }

        if (toIndex < 0) {
            toIndex = len + toIndex;
            if (toIndex < 0) {
                toIndex = len;
            }
        } else if (toIndex > len) {
            toIndex = len;
        }

        if (toIndex < fromIndex) {
            int tmp = fromIndex;
            fromIndex = toIndex;
            toIndex = tmp;
        }

        if (fromIndex == toIndex) {
            return StringPool.EMPTY;
        }

        return str.toString().substring(fromIndex, toIndex);
    }


    /**
     * 截取分隔字符串之前的字符串，不包括分隔字符串<br>
     * 如果给定的字符串为空串（null或""）或者分隔字符串为null，返回原字符串<br>
     * 如果分隔字符串为空串""，则返回空串，如果分隔字符串未找到，返回原字符串
     * <p>
     * 栗子：
     *
     * <pre>
     * StringUtil.subBefore(null, *)      = null
     * StringUtil.subBefore("", *)        = ""
     * StringUtil.subBefore("abc", "a")   = ""
     * StringUtil.subBefore("abcba", "b") = "a"
     * StringUtil.subBefore("abc", "c")   = "ab"
     * StringUtil.subBefore("abc", "d")   = "abc"
     * StringUtil.subBefore("abc", "")    = ""
     * StringUtil.subBefore("abc", null)  = "abc"
     * </pre>
     *
     * @param string          被查找的字符串
     * @param separator       分隔字符串（不包括）
     * @param isLastSeparator 是否查找最后一个分隔字符串（多次出现分隔字符串时选取最后一个），true为选取最后一个
     * @return 切割后的字符串
     * @since 3.1.1
     */
    public static String subBefore(CharSequence string, CharSequence separator, boolean isLastSeparator) {
        if (isEmpty(string) || separator == null) {
            return null == string ? null : string.toString();
        }

        final String str = string.toString();
        final String sep = separator.toString();
        if (sep.isEmpty()) {
            return StringPool.EMPTY;
        }
        final int pos = isLastSeparator ? str.lastIndexOf(sep) : str.indexOf(sep);
        if (pos == INDEX_NOT_FOUND) {
            return str;
        }
        return str.substring(0, pos);
    }

    /**
     * 截取分隔字符串之后的字符串，不包括分隔字符串<br>
     * 如果给定的字符串为空串（null或""），返回原字符串<br>
     * 如果分隔字符串为空串（null或""），则返回空串，如果分隔字符串未找到，返回空串
     * <p>
     * 栗子：
     *
     * <pre>
     * StringUtil.subAfter(null, *)      = null
     * StringUtil.subAfter("", *)        = ""
     * StringUtil.subAfter(*, null)      = ""
     * StringUtil.subAfter("abc", "a")   = "bc"
     * StringUtil.subAfter("abcba", "b") = "cba"
     * StringUtil.subAfter("abc", "c")   = ""
     * StringUtil.subAfter("abc", "d")   = ""
     * StringUtil.subAfter("abc", "")    = "abc"
     * </pre>
     *
     * @param string          被查找的字符串
     * @param separator       分隔字符串（不包括）
     * @param isLastSeparator 是否查找最后一个分隔字符串（多次出现分隔字符串时选取最后一个），true为选取最后一个
     * @return 切割后的字符串
     * @since 3.1.1
     */
    public static String subAfter(CharSequence string, CharSequence separator, boolean isLastSeparator) {
        if (isEmpty(string)) {
            return null == string ? null : string.toString();
        }
        if (separator == null) {
            return StringPool.EMPTY;
        }
        final String str = string.toString();
        final String sep = separator.toString();
        final int pos = isLastSeparator ? str.lastIndexOf(sep) : str.indexOf(sep);
        if (pos == INDEX_NOT_FOUND) {
            return StringPool.EMPTY;
        }
        return str.substring(pos + separator.length());
    }

    /**
     * 截取指定字符串中间部分，不包括标识字符串<br>
     * <p>
     * 栗子：
     *
     * <pre>
     * StringUtil.subBetween("wx[b]yz", "[", "]") = "b"
     * StringUtil.subBetween(null, *, *)          = null
     * StringUtil.subBetween(*, null, *)          = null
     * StringUtil.subBetween(*, *, null)          = null
     * StringUtil.subBetween("", "", "")          = ""
     * StringUtil.subBetween("", "", "]")         = null
     * StringUtil.subBetween("", "[", "]")        = null
     * StringUtil.subBetween("yabcz", "", "")     = ""
     * StringUtil.subBetween("yabcz", "y", "z")   = "abc"
     * StringUtil.subBetween("yabczyabcz", "y", "z")   = "abc"
     * </pre>
     *
     * @param str    被切割的字符串
     * @param before 截取开始的字符串标识
     * @param after  截取到的字符串标识
     * @return 截取后的字符串
     * @since 3.1.1
     */
    public static String subBetween(CharSequence str, CharSequence before, CharSequence after) {
        if (str == null || before == null || after == null) {
            return null;
        }

        final String str2 = str.toString();
        final String before2 = before.toString();
        final String after2 = after.toString();

        final int start = str2.indexOf(before2);
        if (start != INDEX_NOT_FOUND) {
            final int end = str2.indexOf(after2, start + before2.length());
            if (end != INDEX_NOT_FOUND) {
                return str2.substring(start + before2.length(), end);
            }
        }
        return null;
    }

    /**
     * 截取指定字符串中间部分，不包括标识字符串<br>
     * <p>
     * 栗子：
     *
     * <pre>
     * StringUtil.subBetween(null, *)            = null
     * StringUtil.subBetween("", "")             = ""
     * StringUtil.subBetween("", "tag")          = null
     * StringUtil.subBetween("tagabctag", null)  = null
     * StringUtil.subBetween("tagabctag", "")    = ""
     * StringUtil.subBetween("tagabctag", "tag") = "abc"
     * </pre>
     *
     * @param str            被切割的字符串
     * @param beforeAndAfter 截取开始和结束的字符串标识
     * @return 截取后的字符串
     * @since 3.1.1
     */
    public static String subBetween(CharSequence str, CharSequence beforeAndAfter) {
        return subBetween(str, beforeAndAfter, beforeAndAfter);
    }

    /**
     * 去掉指定前缀
     *
     * @param str    字符串
     * @param prefix 前缀
     * @return 切掉后的字符串，若前缀不是 preffix， 返回原字符串
     */
    public static String removePrefix(CharSequence str, CharSequence prefix) {
        if (isEmpty(str) || isEmpty(prefix)) {
            return StringPool.EMPTY;
        }

        final String str2 = str.toString();
        if (str2.startsWith(prefix.toString())) {
            return subSuf(str2, prefix.length());
        }
        return str2;
    }

    /**
     * 忽略大小写去掉指定前缀
     *
     * @param str    字符串
     * @param prefix 前缀
     * @return 切掉后的字符串，若前缀不是 prefix， 返回原字符串
     */
    public static String removePrefixIgnoreCase(CharSequence str, CharSequence prefix) {
        if (isEmpty(str) || isEmpty(prefix)) {
            return StringPool.EMPTY;
        }

        final String str2 = str.toString();
        if (str2.toLowerCase().startsWith(prefix.toString().toLowerCase())) {
            return subSuf(str2, prefix.length());
        }
        return str2;
    }

    /**
     * 去掉指定后缀
     *
     * @param str    字符串
     * @param suffix 后缀
     * @return 切掉后的字符串，若后缀不是 suffix， 返回原字符串
     */
    public static String removeSuffix(CharSequence str, CharSequence suffix) {
        if (isEmpty(str) || isEmpty(suffix)) {
            return StringPool.EMPTY;
        }

        final String str2 = str.toString();
        if (str2.endsWith(suffix.toString())) {
            return subPre(str2, str2.length() - suffix.length());
        }
        return str2;
    }

    /**
     * 去掉指定后缀，并小写首字母
     *
     * @param str    字符串
     * @param suffix 后缀
     * @return 切掉后的字符串，若后缀不是 suffix， 返回原字符串
     */
    public static String removeSufAndLowerFirst(CharSequence str, CharSequence suffix) {
        return lowerFirst(removeSuffix(str, suffix));
    }

    /**
     * 忽略大小写去掉指定后缀
     *
     * @param str    字符串
     * @param suffix 后缀
     * @return 切掉后的字符串，若后缀不是 suffix， 返回原字符串
     */
    public static String removeSuffixIgnoreCase(CharSequence str, CharSequence suffix) {
        if (isEmpty(str) || isEmpty(suffix)) {
            return StringPool.EMPTY;
        }

        final String str2 = str.toString();
        if (str2.toLowerCase().endsWith(suffix.toString().toLowerCase())) {
            return subPre(str2, str2.length() - suffix.length());
        }
        return str2;
    }

    /**
     * 首字母变小写
     *
     * @param str 字符串
     * @return {String}
     */
    public static String lowerFirst(String str) {
        char firstChar = str.charAt(0);
        if (firstChar >= StringPool.U_A && firstChar <= StringPool.U_Z) {
            char[] arr = str.toCharArray();
            arr[0] += (StringPool.L_A - StringPool.U_A);
            return new String(arr);
        }
        return str;
    }

    /**
     * 首字母变大写
     *
     * @param str 字符串
     * @return {String}
     */
    public static String upperFirst(String str) {
        char firstChar = str.charAt(0);
        if (firstChar >= StringPool.L_A && firstChar <= StringPool.L_Z) {
            char[] arr = str.toCharArray();
            arr[0] -= (StringPool.L_A - StringPool.U_A);
            return new String(arr);
        }
        return str;
    }

    /**
     * 切割指定位置之前部分的字符串
     *
     * @param string  字符串
     * @param toIndex 切割到的位置（不包括）
     * @return 切割后的剩余的前半部分字符串
     */
    public static String subPre(CharSequence string, int toIndex) {
        return sub(string, 0, toIndex);
    }

    /**
     * 切割指定位置之后部分的字符串
     *
     * @param string    字符串
     * @param fromIndex 切割开始的位置（包括）
     * @return 切割后后剩余的后半部分字符串
     */
    public static String subSuf(CharSequence string, int fromIndex) {
        if (isEmpty(string)) {
            return null;
        }
        return sub(string, fromIndex, string.length());
    }

    /**
     * 指定范围内查找指定字符
     *
     * @param str        字符串
     * @param searchChar 被查找的字符
     * @return 位置
     */
    public static int indexOf(final CharSequence str, char searchChar) {
        return indexOf(str, searchChar, 0);
    }

    /**
     * 指定范围内查找指定字符
     *
     * @param str        字符串
     * @param searchChar 被查找的字符
     * @param start      起始位置，如果小于0，从0开始查找
     * @return 位置
     */
    public static int indexOf(final CharSequence str, char searchChar, int start) {
        if (str instanceof String) {
            return ((String) str).indexOf(searchChar, start);
        } else {
            return indexOf(str, searchChar, start, -1);
        }
    }

    /**
     * 指定范围内查找指定字符
     *
     * @param str        字符串
     * @param searchChar 被查找的字符
     * @param start      起始位置，如果小于0，从0开始查找
     * @param end        终止位置，如果超过str.length()则默认查找到字符串末尾
     * @return 位置
     */
    public static int indexOf(final CharSequence str, char searchChar, int start, int end) {
        final int len = str.length();
        if (start < 0 || start > len) {
            start = 0;
        }
        if (end > len || end < 0) {
            end = len;
        }
        for (int i = start; i < end; i++) {
            if (str.charAt(i) == searchChar) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 指定范围内查找字符串，忽略大小写<br>
     *
     * <pre>
     * StringUtil.indexOfIgnoreCase(null, *, *)          = -1
     * StringUtil.indexOfIgnoreCase(*, null, *)          = -1
     * StringUtil.indexOfIgnoreCase("", "", 0)           = 0
     * StringUtil.indexOfIgnoreCase("aabaabaa", "A", 0)  = 0
     * StringUtil.indexOfIgnoreCase("aabaabaa", "B", 0)  = 2
     * StringUtil.indexOfIgnoreCase("aabaabaa", "AB", 0) = 1
     * StringUtil.indexOfIgnoreCase("aabaabaa", "B", 3)  = 5
     * StringUtil.indexOfIgnoreCase("aabaabaa", "B", 9)  = -1
     * StringUtil.indexOfIgnoreCase("aabaabaa", "B", -1) = 2
     * StringUtil.indexOfIgnoreCase("aabaabaa", "", 2)   = 2
     * StringUtil.indexOfIgnoreCase("abc", "", 9)        = -1
     * </pre>
     *
     * @param str       字符串
     * @param searchStr 需要查找位置的字符串
     * @return 位置
     * @since 3.2.1
     */
    public static int indexOfIgnoreCase(final CharSequence str, final CharSequence searchStr) {
        return indexOfIgnoreCase(str, searchStr, 0);
    }

    /**
     * 指定范围内查找字符串
     *
     * <pre>
     * StringUtil.indexOfIgnoreCase(null, *, *)          = -1
     * StringUtil.indexOfIgnoreCase(*, null, *)          = -1
     * StringUtil.indexOfIgnoreCase("", "", 0)           = 0
     * StringUtil.indexOfIgnoreCase("aabaabaa", "A", 0)  = 0
     * StringUtil.indexOfIgnoreCase("aabaabaa", "B", 0)  = 2
     * StringUtil.indexOfIgnoreCase("aabaabaa", "AB", 0) = 1
     * StringUtil.indexOfIgnoreCase("aabaabaa", "B", 3)  = 5
     * StringUtil.indexOfIgnoreCase("aabaabaa", "B", 9)  = -1
     * StringUtil.indexOfIgnoreCase("aabaabaa", "B", -1) = 2
     * StringUtil.indexOfIgnoreCase("aabaabaa", "", 2)   = 2
     * StringUtil.indexOfIgnoreCase("abc", "", 9)        = -1
     * </pre>
     *
     * @param str       字符串
     * @param searchStr 需要查找位置的字符串
     * @param fromIndex 起始位置
     * @return 位置
     * @since 3.2.1
     */
    public static int indexOfIgnoreCase(final CharSequence str, final CharSequence searchStr, int fromIndex) {
        return indexOf(str, searchStr, fromIndex, true);
    }

    /**
     * 指定范围内反向查找字符串
     *
     * @param str        字符串
     * @param searchStr  需要查找位置的字符串
     * @param fromIndex  起始位置
     * @param ignoreCase 是否忽略大小写
     * @return 位置
     * @since 3.2.1
     */
    public static int indexOf(final CharSequence str, CharSequence searchStr, int fromIndex, boolean ignoreCase) {
        if (str == null || searchStr == null) {
            return INDEX_NOT_FOUND;
        }
        if (fromIndex < 0) {
            fromIndex = 0;
        }

        final int endLimit = str.length() - searchStr.length() + 1;
        if (fromIndex > endLimit) {
            return INDEX_NOT_FOUND;
        }
        if (searchStr.isEmpty()) {
            return fromIndex;
        }

        if (!ignoreCase) {
            // 不忽略大小写调用JDK方法
            return str.toString().indexOf(searchStr.toString(), fromIndex);
        }

        for (int i = fromIndex; i < endLimit; i++) {
            if (isSubEquals(str, i, searchStr, 0, searchStr.length(), true)) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 指定范围内查找字符串，忽略大小写<br>
     *
     * @param str       字符串
     * @param searchStr 需要查找位置的字符串
     * @return 位置
     * @since 3.2.1
     */
    public static int lastIndexOfIgnoreCase(final CharSequence str, final CharSequence searchStr) {
        return lastIndexOfIgnoreCase(str, searchStr, str.length());
    }

    /**
     * 指定范围内查找字符串，忽略大小写<br>
     *
     * @param str       字符串
     * @param searchStr 需要查找位置的字符串
     * @param fromIndex 起始位置，从后往前计数
     * @return 位置
     * @since 3.2.1
     */
    public static int lastIndexOfIgnoreCase(final CharSequence str, final CharSequence searchStr, int fromIndex) {
        return lastIndexOf(str, searchStr, fromIndex, true);
    }

    /**
     * 指定范围内查找字符串<br>
     *
     * @param str        字符串
     * @param searchStr  需要查找位置的字符串
     * @param fromIndex  起始位置，从后往前计数
     * @param ignoreCase 是否忽略大小写
     * @return 位置
     * @since 3.2.1
     */
    public static int lastIndexOf(final CharSequence str, final CharSequence searchStr, int fromIndex, boolean ignoreCase) {
        if (str == null || searchStr == null) {
            return INDEX_NOT_FOUND;
        }
        if (fromIndex < 0) {
            fromIndex = 0;
        }
        fromIndex = Math.min(fromIndex, str.length());

        if (searchStr.isEmpty()) {
            return fromIndex;
        }

        if (!ignoreCase) {
            // 不忽略大小写调用JDK方法
            return str.toString().lastIndexOf(searchStr.toString(), fromIndex);
        }

        for (int i = fromIndex; i > 0; i--) {
            if (isSubEquals(str, i, searchStr, 0, searchStr.length(), true)) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 返回字符串 searchStr 在字符串 str 中第 ordinal 次出现的位置。<br>
     * 此方法来自：Apache-Commons-Lang
     * <p>
     * 栗子（*代表任意字符）：
     *
     * <pre>
     * StringUtil.ordinalIndexOf(null, *, *)          = -1
     * StringUtil.ordinalIndexOf(*, null, *)          = -1
     * StringUtil.ordinalIndexOf("", "", *)           = 0
     * StringUtil.ordinalIndexOf("aabaabaa", "a", 1)  = 0
     * StringUtil.ordinalIndexOf("aabaabaa", "a", 2)  = 1
     * StringUtil.ordinalIndexOf("aabaabaa", "b", 1)  = 2
     * StringUtil.ordinalIndexOf("aabaabaa", "b", 2)  = 5
     * StringUtil.ordinalIndexOf("aabaabaa", "ab", 1) = 1
     * StringUtil.ordinalIndexOf("aabaabaa", "ab", 2) = 4
     * StringUtil.ordinalIndexOf("aabaabaa", "", 1)   = 0
     * StringUtil.ordinalIndexOf("aabaabaa", "", 2)   = 0
     * </pre>
     *
     * @param str       被检查的字符串，可以为null
     * @param searchStr 被查找的字符串，可以为null
     * @param ordinal   第几次出现的位置
     * @return 查找到的位置
     * @since 3.2.3
     */
    public static int ordinalIndexOf(String str, String searchStr, int ordinal) {
        if (str == null || searchStr == null || ordinal <= 0) {
            return INDEX_NOT_FOUND;
        }
        if (searchStr.isEmpty()) {
            return 0;
        }
        int found = 0;
        int index = INDEX_NOT_FOUND;
        do {
            index = str.indexOf(searchStr, index + 1);
            if (index < 0) {
                return index;
            }
            found++;
        } while (found < ordinal);
        return index;
    }

    /**
     * 截取两个字符串的不同部分（长度一致），判断截取的子串是否相同<br>
     * 任意一个字符串为null返回false
     *
     * @param str1       第一个字符串
     * @param start1     第一个字符串开始的位置
     * @param str2       第二个字符串
     * @param start2     第二个字符串开始的位置
     * @param length     截取长度
     * @param ignoreCase 是否忽略大小写
     * @return 子串是否相同
     * @since 3.2.1
     */
    public static boolean isSubEquals(CharSequence str1, int start1, CharSequence str2, int start2, int length, boolean ignoreCase) {
        if (null == str1 || null == str2) {
            return false;
        }

        return str1.toString().regionMatches(ignoreCase, start1, str2.toString(), start2, length);
    }

    /**
     * 比较两个字符串（大小写敏感）。
     *
     * <pre>
     * equalsIgnoreCase(null, null)   = true
     * equalsIgnoreCase(null, &quot;abc&quot;)  = false
     * equalsIgnoreCase(&quot;abc&quot;, null)  = false
     * equalsIgnoreCase(&quot;abc&quot;, &quot;abc&quot;) = true
     * equalsIgnoreCase(&quot;abc&quot;, &quot;ABC&quot;) = true
     * </pre>
     *
     * @param str1 要比较的字符串1
     * @param str2 要比较的字符串2
     * @return 如果两个字符串相同，或者都是<code>null</code>，则返回<code>true</code>
     */
    public static boolean equals(CharSequence str1, CharSequence str2) {
        return equals(str1, str2, false);
    }

    /**
     * 比较两个字符串（大小写不敏感）。
     *
     * <pre>
     * equalsIgnoreCase(null, null)   = true
     * equalsIgnoreCase(null, &quot;abc&quot;)  = false
     * equalsIgnoreCase(&quot;abc&quot;, null)  = false
     * equalsIgnoreCase(&quot;abc&quot;, &quot;abc&quot;) = true
     * equalsIgnoreCase(&quot;abc&quot;, &quot;ABC&quot;) = true
     * </pre>
     *
     * @param str1 要比较的字符串1
     * @param str2 要比较的字符串2
     * @return 如果两个字符串相同，或者都是<code>null</code>，则返回<code>true</code>
     */
    public static boolean equalsIgnoreCase(CharSequence str1, CharSequence str2) {
        return equals(str1, str2, true);
    }

    /**
     * 比较两个字符串是否相等。
     *
     * @param str1       要比较的字符串1
     * @param str2       要比较的字符串2
     * @param ignoreCase 是否忽略大小写
     * @return 如果两个字符串相同，或者都是<code>null</code>，则返回<code>true</code>
     * @since 3.2.0
     */
    public static boolean equals(CharSequence str1, CharSequence str2, boolean ignoreCase) {
        if (null == str1) {
            // 只有两个都为null才判断相等
            return str2 == null;
        }
        if (null == str2) {
            // 字符串2空，字符串1非空，直接false
            return false;
        }

        if (ignoreCase) {
            return str1.toString().equalsIgnoreCase(str2.toString());
        } else {
            return str1.equals(str2);
        }
    }

    /**
     * 创建StringBuilder对象
     *
     * @return StringBuilder对象
     */
    public static StringBuilder builder() {
        return new StringBuilder();
    }

    /**
     * 创建StringBuilder对象
     *
     * @param capacity 初始大小
     * @return StringBuilder对象
     */
    public static StringBuilder builder(int capacity) {
        return new StringBuilder(capacity);
    }

    /**
     * 创建StringBuilder对象
     *
     * @param strs 初始字符串列表
     * @return StringBuilder对象
     */
    public static StringBuilder builder(CharSequence... strs) {
        final StringBuilder sb = new StringBuilder();
        return appendBuilder(sb, strs);
    }

    /**
     * 创建StringBuilder对象
     *
     * @param sb   初始StringBuilder
     * @param strs 初始字符串列表
     * @return StringBuilder对象
     */
    public static StringBuilder appendBuilder(StringBuilder sb, CharSequence... strs) {
        for (CharSequence str : strs) {
            sb.append(str);
        }
        return sb;
    }

    /**
     * 获得StringReader
     *
     * @param str 字符串
     * @return StringReader
     */
    public static StringReader getReader(CharSequence str) {
        if (null == str) {
            return null;
        }
        return new StringReader(str.toString());
    }

    /**
     * 获得StringWriter
     *
     * @return StringWriter
     */
    public static StringWriter getWriter() {
        return new StringWriter();
    }

    /**
     * 统计指定内容中包含指定字符串的数量<br>
     * 参数为 {@code null} 或者 "" 返回 {@code 0}.
     *
     * <pre>
     * StringUtil.count(null, *)       = 0
     * StringUtil.count("", *)         = 0
     * StringUtil.count("abba", null)  = 0
     * StringUtil.count("abba", "")    = 0
     * StringUtil.count("abba", "a")   = 2
     * StringUtil.count("abba", "ab")  = 1
     * StringUtil.count("abba", "xxx") = 0
     * </pre>
     *
     * @param content      被查找的字符串
     * @param strForSearch 需要查找的字符串
     * @return 查找到的个数
     */
    public static int count(CharSequence content, CharSequence strForSearch) {
        if (Func.hasEmpty(content, strForSearch) || strForSearch.length() > content.length()) {
            return 0;
        }

        int count = 0;
        int idx = 0;
        final String content2 = content.toString();
        final String strForSearch2 = strForSearch.toString();
        while ((idx = content2.indexOf(strForSearch2, idx)) > -1) {
            count++;
            idx += strForSearch.length();
        }
        return count;
    }

    /**
     * 统计指定内容中包含指定字符的数量
     *
     * @param content       内容
     * @param charForSearch 被统计的字符
     * @return 包含数量
     */
    public static int count(CharSequence content, char charForSearch) {
        int count = 0;
        if (isEmpty(content)) {
            return 0;
        }
        int contentLength = content.length();
        for (int i = 0; i < contentLength; i++) {
            if (charForSearch == content.charAt(i)) {
                count++;
            }
        }
        return count;
    }

    /**
     * 将下划线命名转换为驼峰命名。
     * 
     * <p>
     * 该方法将下划线分隔的命名转换为驼峰命名（小驼峰），
     * 转换规则如下：
     * <ul>
     *   <li>第一个单词全部小写</li>
     *   <li>其后的每个单词首字母大写，其余字母小写</li>
     *   <li>去除所有下划线</li>
     * </ul>
     * 
     * <p>示例：
     * <pre>{@code
     * String result1 = StringUtil.underlineToHump("user_name");
     * // 输出：userName
     * System.out.println(result1);
     * 
     * String result2 = StringUtil.underlineToHump("order_item_id");
     * // 输出：orderItemId
     * System.out.println(result2);
     * 
     * String result3 = StringUtil.underlineToHump("CUSTOMER_ADDRESS");
     * // 输出：customerAddress
     * System.out.println(result3);
     * }</pre>
     * 
     * @param para 下划线命名的字符串
     * @return 驼峰命名的字符串
     */
    public static String underlineToHump(String para) {
        StringBuilder result = new StringBuilder();
        String[] a = para.split("_");
        for (String s : a) {
            if (result.isEmpty()) {
                result.append(s.toLowerCase());
            } else {
                result.append(s.substring(0, 1).toUpperCase());
                result.append(s.substring(1).toLowerCase());
            }
        }
        return result.toString();
    }

    /**
     * 将驼峰命名转换为下划线命名。
     * 
     * <p>
     * 该方法将驼峰命名转换为下划线分隔的命名（全小写），
     * 转换规则如下：
     * <ul>
     *   <li>首字母小写</li>
     *   <li>在大写字母前添加下划线</li>
     *   <li>将所有字母转换为小写</li>
     * </ul>
     * 
     * <p>示例：
     * <pre>{@code
     * String result1 = StringUtil.humpToUnderline("userName");
     * // 输出：user_name
     * System.out.println(result1);
     * 
     * String result2 = StringUtil.humpToUnderline("orderItemId");
     * // 输出：order_item_id
     * System.out.println(result2);
     * 
     * String result3 = StringUtil.humpToUnderline("CustomerAddress");
     * // 输出：customer_address
     * System.out.println(result3);
     * }</pre>
     * 
     * @param para 驼峰命名的字符串
     * @return 下划线命名的字符串
     */
    public static String humpToUnderline(String para) {
        para = lowerFirst(para);
        StringBuilder sb = new StringBuilder(para);
        int temp = 0;
        for (int i = 0; i < para.length(); i++) {
            if (Character.isUpperCase(para.charAt(i))) {
                sb.insert(i + temp, "_");
                temp += 1;
            }
        }
        return sb.toString().toLowerCase();
    }

    /**
     * 将横线命名转换为驼峰命名。
     * 
     * <p>
     * 该方法将横线分隔的命名转换为驼峰命名（小驼峰），
     * 转换规则如下：
     * <ul>
     *   <li>第一个单词全部小写</li>
     *   <li>其后的每个单词首字母大写，其余字母小写</li>
     *   <li>去除所有横线</li>
     * </ul>
     * 
     * <p>示例：
     * <pre>{@code
     * String result1 = StringUtil.lineToHump("user-name");
     * // 输出：userName
     * System.out.println(result1);
     * 
     * String result2 = StringUtil.lineToHump("order-item-id");
     * // 输出：orderItemId
     * System.out.println(result2);
     * 
     * String result3 = StringUtil.lineToHump("CUSTOMER-ADDRESS");
     * // 输出：customerAddress
     * System.out.println(result3);
     * }</pre>
     * 
     * @param para 横线命名的字符串
     * @return 驼峰命名的字符串
     */
    public static String lineToHump(String para) {
        StringBuilder result = new StringBuilder();
        String[] a = para.split("-");
        for (String s : a) {
            if (result.isEmpty()) {
                result.append(s.toLowerCase());
            } else {
                result.append(s.substring(0, 1).toUpperCase());
                result.append(s.substring(1).toLowerCase());
            }
        }
        return result.toString();
    }

    /**
     * 将驼峰命名转换为横线命名。
     * 
     * <p>
     * 该方法将驼峰命名转换为横线分隔的命名（全小写），
     * 转换规则如下：
     * <ul>
     *   <li>首字母小写</li>
     *   <li>在大写字母前添加横线</li>
     *   <li>将所有字母转换为小写</li>
     * </ul>
     * 
     * <p>示例：
     * <pre>{@code
     * String result1 = StringUtil.humpToLine("userName");
     * // 输出：user-name
     * System.out.println(result1);
     * 
     * String result2 = StringUtil.humpToLine("orderItemId");
     * // 输出：order-item-id
     * System.out.println(result2);
     * 
     * String result3 = StringUtil.humpToLine("CustomerAddress");
     * // 输出：customer-address
     * System.out.println(result3);
     * }</pre>
     * 
     * @param para 驼峰命名的字符串
     * @return 横线命名的字符串
     */
    public static String humpToLine(String para) {
        para = lowerFirst(para);
        StringBuilder sb = new StringBuilder(para);
        int temp = 0;
        for (int i = 0; i < para.length(); i++) {
            if (Character.isUpperCase(para.charAt(i))) {
                sb.insert(i + temp, "-");
                temp += 1;
            }
        }
        return sb.toString().toLowerCase();
    }
}