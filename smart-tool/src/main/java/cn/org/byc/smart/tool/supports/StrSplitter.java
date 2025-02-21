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

import cn.org.byc.smart.tool.constant.StringPool;
import cn.org.byc.smart.tool.utils.Func;
import cn.org.byc.smart.tool.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串分割工具类
 * 
 * <p>提供多种字符串分割方式，支持以下功能：
 * <ul>
 *   <li>按字符分割</li>
 *   <li>按字符串分割</li>
 *   <li>按正则表达式分割</li>
 *   <li>按长度分割</li>
 *   <li>支持去除空白字符</li>
 *   <li>支持忽略空值</li>
 * </ul>
 *
 * <p>使用示例:
 * <pre>{@code
 * // 1. 按字符分割
 * List<String> list = StrSplitter.split("a,b,c", ',', true, true);
 * // 结果: ["a", "b", "c"]
 *
 * // 2. 按字符串分割
 * List<String> list = StrSplitter.split("a::b::c", "::", true, true);
 * // 结果: ["a", "b", "c"]
 *
 * // 3. 分割路径
 * List<String> list = StrSplitter.splitPath("/path/to/file");
 * // 结果: ["path", "to", "file"]
 * }</pre>
 *
 * @author Ken
 */
public class StrSplitter {

    //---------------------------------------------------------------------------------------------- Split by char

    /**
     * 分割字符串路径
     * <p>仅支持Unix分隔符：/
     *
     * @param str 要分割的字符串
     * @return 分割后的字符串列表
     */
    public static List<String> splitPath(String str) {
        return splitPath(str, 0);
    }

    /**
     * 分割字符串路径为数组
     *
     * @param str 要分割的字符串
     * @return 分割后的字符串数组
     */
    public static String[] splitPathToArray(String str) {
        return toArray(splitPath(str));
    }

    /**
     * 分割字符串路径，支持限制分片数
     *
     * @param str 要分割的字符串
     * @param limit 限制分片数，0表示不限制
     * @return 分割后的字符串列表
     */
    public static List<String> splitPath(String str, int limit) {
        return split(str, StringPool.SLASH, limit, true, true);
    }

    /**
     * 分割字符串路径为数组，支持限制分片数
     *
     * @param str 要分割的字符串
     * @param limit 限制分片数，0表示不限制
     * @return 分割后的字符串数组
     */
    public static String[] splitPathToArray(String str, int limit) {
        return toArray(splitPath(str, limit));
    }

    /**
     * 按字符分割字符串
     *
     * @param str 要分割的字符串
     * @param separator 分隔符字符
     * @param isTrim 是否去除结果中的空白
     * @param ignoreEmpty 是否忽略空值
     * @return 分割后的字符串列表
     */
    public static List<String> split(String str, char separator, boolean isTrim, boolean ignoreEmpty) {
        return split(str, separator, 0, isTrim, ignoreEmpty);
    }

    /**
     * 按字符分割字符串，支持限制分片数
     *
     * @param str 要分割的字符串
     * @param separator 分隔符字符
     * @param limit 限制分片数，0表示不限制
     * @param isTrim 是否去除结果中的空白
     * @param ignoreEmpty 是否忽略空值
     * @return 分割后的字符串列表
     */
    public static List<String> split(String str, char separator, int limit, boolean isTrim, boolean ignoreEmpty) {
        if (StringUtil.isEmpty(str)) {
            return new ArrayList<>(0);
        }
        
        // 如果限制为1，直接返回整个字符串
        if (limit == 1) {
            return addToList(new ArrayList<>(1), str, isTrim, ignoreEmpty);
        }

        // 创建结果列表，预设容量
        final ArrayList<String> list = new ArrayList<>(limit > 0 ? limit : 16);
        
        // 记录字符串长度
        int len = str.length();
        // 记录上一个分隔符位置
        int start = 0;
        
        // 遍历字符串查找分隔符
        for (int i = 0; i < len; i++) {
            if (Func.equals(separator, str.charAt(i))) {
                // 添加分割出的子字符串到列表
                addToList(list, str.substring(start, i), isTrim, ignoreEmpty);
                // 更新起始位置
                start = i + 1;

                // 检查是否达到分片数限制
                if (limit > 0 && list.size() > limit - 2) {
                    break;
                }
            }
        }
        
        // 添加最后一个子字符串
        return addToList(list, str.substring(start, len), isTrim, ignoreEmpty);
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
     * @since 3.2.1
     */
    public static String[] splitToArray(String str, char separator, int limit, boolean isTrim, boolean ignoreEmpty) {
        return toArray(split(str, separator, limit, isTrim, ignoreEmpty));
    }

    //---------------------------------------------------------------------------------------------- Split by String

    /**
     * 切分字符串，不忽略大小写
     *
     * @param str         被切分的字符串
     * @param separator   分隔符字符串
     * @param isTrim      是否去除切分字符串后每个元素两边的空格
     * @param ignoreEmpty 是否忽略空串
     * @return 切分后的集合
     * @since 3.0.8
     */
    public static List<String> split(String str, String separator, boolean isTrim, boolean ignoreEmpty) {
        return split(str, separator, -1, isTrim, ignoreEmpty, false);
    }

    /**
     * 切分字符串，去除每个元素两边空格，忽略大小写
     *
     * @param str         被切分的字符串
     * @param separator   分隔符字符串
     * @param ignoreEmpty 是否忽略空串
     * @return 切分后的集合
     * @since 3.2.1
     */
    public static List<String> splitTrim(String str, String separator, boolean ignoreEmpty) {
        return split(str, separator, true, ignoreEmpty);
    }

    /**
     * 切分字符串，不忽略大小写
     *
     * @param str         被切分的字符串
     * @param separator   分隔符字符串
     * @param limit       限制分片数
     * @param isTrim      是否去除切分字符串后每个元素两边的空格
     * @param ignoreEmpty 是否忽略空串
     * @return 切分后的集合
     * @since 3.0.8
     */
    public static List<String> split(String str, String separator, int limit, boolean isTrim, boolean ignoreEmpty) {
        return split(str, separator, limit, isTrim, ignoreEmpty, false);
    }

    /**
     * 切分字符串，去除每个元素两边空格，忽略大小写
     *
     * @param str         被切分的字符串
     * @param separator   分隔符字符串
     * @param limit       限制分片数
     * @param ignoreEmpty 是否忽略空串
     * @return 切分后的集合
     * @since 3.2.1
     */
    public static List<String> splitTrim(String str, String separator, int limit, boolean ignoreEmpty) {
        return split(str, separator, limit, true, ignoreEmpty);
    }

    /**
     * 切分字符串，忽略大小写
     *
     * @param str         被切分的字符串
     * @param separator   分隔符字符串
     * @param limit       限制分片数
     * @param isTrim      是否去除切分字符串后每个元素两边的空格
     * @param ignoreEmpty 是否忽略空串
     * @return 切分后的集合
     * @since 3.2.1
     */
    public static List<String> splitIgnoreCase(String str, String separator, int limit, boolean isTrim, boolean ignoreEmpty) {
        return split(str, separator, limit, isTrim, ignoreEmpty, true);
    }

    /**
     * 切分字符串，去除每个元素两边空格，忽略大小写
     *
     * @param str         被切分的字符串
     * @param separator   分隔符字符串
     * @param limit       限制分片数
     * @param ignoreEmpty 是否忽略空串
     * @return 切分后的集合
     * @since 3.2.1
     */
    public static List<String> splitTrimIgnoreCase(String str, String separator, int limit, boolean ignoreEmpty) {
        return split(str, separator, limit, true, ignoreEmpty, true);
    }

    /**
     * 切分字符串
     *
     * @param str         被切分的字符串
     * @param separator   分隔符字符串
     * @param limit       限制分片数
     * @param isTrim      是否去除切分字符串后每个元素两边的空格
     * @param ignoreEmpty 是否忽略空串
     * @param ignoreCase  是否忽略大小写
     * @return 切分后的集合
     * @since 3.2.1
     */
    public static List<String> split(String str, String separator, int limit, boolean isTrim, boolean ignoreEmpty, boolean ignoreCase) {
        if (StringUtil.isEmpty(str)) {
            return new ArrayList<String>(0);
        }
        if (limit == 1) {
            return addToList(new ArrayList<String>(1), str, isTrim, ignoreEmpty);
        }

        if (StringUtil.isEmpty(separator)) {
            return split(str, limit);
        } else if (separator.length() == 1) {
            return split(str, String.valueOf(separator.charAt(0)), limit, isTrim, ignoreEmpty, ignoreCase);
        }

        final ArrayList<String> list = new ArrayList<>();
        int len = str.length();
        int separatorLen = separator.length();
        int start = 0;
        int i = 0;
        while (i < len) {
            i = StringUtil.indexOf(str, separator, start, ignoreCase);
            if (i > -1) {
                addToList(list, str.substring(start, i), isTrim, ignoreEmpty);
                start = i + separatorLen;

                //检查是否超出范围（最大允许limit-1个，剩下一个留给末尾字符串）
                if (limit > 0 && list.size() > limit - 2) {
                    break;
                }
            } else {
                break;
            }
        }
        return addToList(list, str.substring(start, len), isTrim, ignoreEmpty);
    }

    /**
     * 切分字符串为字符串数组
     *
     * @param str         被切分的字符串
     * @param separator   分隔符字符
     * @param limit       限制分片数
     * @param isTrim      是否去除切分字符串后每个元素两边的空格
     * @param ignoreEmpty 是否忽略空串
     * @return 切分后的集合
     * @since 3.0.8
     */
    public static String[] splitToArray(String str, String separator, int limit, boolean isTrim, boolean ignoreEmpty) {
        return toArray(split(str, separator, limit, isTrim, ignoreEmpty));
    }

    //---------------------------------------------------------------------------------------------- Split by Whitespace

    /**
     * 使用空白符切分字符串<br>
     * 切分后的字符串两边不包含空白符，空串或空白符串并不做为元素之一
     *
     * @param str   被切分的字符串
     * @param limit 限制分片数
     * @return 切分后的集合
     * @since 3.0.8
     */
    public static List<String> split(String str, int limit) {
        if (StringUtil.isEmpty(str)) {
            return new ArrayList<String>(0);
        }
        if (limit == 1) {
            return addToList(new ArrayList<String>(1), str, true, true);
        }

        final ArrayList<String> list = new ArrayList<>();
        int len = str.length();
        int start = 0;
        for (int i = 0; i < len; i++) {
            if (Func.isEmpty(str.charAt(i))) {
                addToList(list, str.substring(start, i), true, true);
                start = i + 1;
                if (limit > 0 && list.size() > limit - 2) {
                    break;
                }
            }
        }
        return addToList(list, str.substring(start, len), true, true);
    }

    /**
     * 切分字符串为字符串数组
     *
     * @param str   被切分的字符串
     * @param limit 限制分片数
     * @return 切分后的集合
     * @since 3.0.8
     */
    public static String[] splitToArray(String str, int limit) {
        return toArray(split(str, limit));
    }

    //---------------------------------------------------------------------------------------------- Split by regex

    /**
     * 通过正则切分字符串
     *
     * @param str              字符串
     * @param separatorPattern 分隔符正则{@link Pattern}
     * @param limit            限制分片数
     * @param isTrim           是否去除切分字符串后每个元素两边的空格
     * @param ignoreEmpty      是否忽略空串
     * @return 切分后的集合
     * @since 3.0.8
     */
    public static List<String> split(String str, Pattern separatorPattern, int limit, boolean isTrim, boolean ignoreEmpty) {
        if (StringUtil.isEmpty(str)) {
            return new ArrayList<String>(0);
        }
        if (limit == 1) {
            return addToList(new ArrayList<String>(1), str, isTrim, ignoreEmpty);
        }

        if (null == separatorPattern) {
            return split(str, limit);
        }

        final Matcher matcher = separatorPattern.matcher(str);
        final ArrayList<String> list = new ArrayList<>();
        int len = str.length();
        int start = 0;
        while (matcher.find()) {
            addToList(list, str.substring(start, matcher.start()), isTrim, ignoreEmpty);
            start = matcher.end();

            if (limit > 0 && list.size() > limit - 2) {
                break;
            }
        }
        return addToList(list, str.substring(start, len), isTrim, ignoreEmpty);
    }

    /**
     * 通过正则切分字符串为字符串数组
     *
     * @param str              被切分的字符串
     * @param separatorPattern 分隔符正则{@link Pattern}
     * @param limit            限制分片数
     * @param isTrim           是否去除切分字符串后每个元素两边的空格
     * @param ignoreEmpty      是否忽略空串
     * @return 切分后的集合
     * @since 3.0.8
     */
    public static String[] splitToArray(String str, Pattern separatorPattern, int limit, boolean isTrim, boolean ignoreEmpty) {
        return toArray(split(str, separatorPattern, limit, isTrim, ignoreEmpty));
    }

    //---------------------------------------------------------------------------------------------- Split by length

    /**
     * 根据给定长度，将给定字符串截取为多个部分
     *
     * @param str 字符串
     * @param len 每一个小节的长度
     * @return 截取后的字符串数组
     */
    public static String[] splitByLength(String str, int len) {
        int partCount = str.length() / len;
        int lastPartCount = str.length() % len;
        int fixPart = 0;
        if (lastPartCount != 0) {
            fixPart = 1;
        }

        final String[] strs = new String[partCount + fixPart];
        for (int i = 0; i < partCount + fixPart; i++) {
            if (i == partCount + fixPart - 1 && lastPartCount != 0) {
                strs[i] = str.substring(i * len, i * len + lastPartCount);
            } else {
                strs[i] = str.substring(i * len, i * len + len);
            }
        }
        return strs;
    }

    //---------------------------------------------------------------------------------------------------------- Private method start

    /**
     * 将字符串添加到列表中
     *
     * @param list 目标列表
     * @param part 要添加的字符串
     * @param isTrim 是否去除空白
     * @param ignoreEmpty 是否忽略空值
     * @return 更新后的列表
     */
    private static List<String> addToList(List<String> list, String part, boolean isTrim, boolean ignoreEmpty) {
        part = part.toString();
        // 去除空白字符
        if (isTrim) {
            part = part.trim();
        }
        // 判断是否添加到列表
        if (!ignoreEmpty || !part.isEmpty()) {
            list.add(part);
        }
        return list;
    }

    /**
     * 将字符串列表转换为数组
     *
     * @param list 字符串列表
     * @return 字符串数组
     */
    private static String[] toArray(List<String> list) {
        return list.toArray(new String[0]);
    }
    //---------------------------------------------------------------------------------------------------------- Private method end
}
