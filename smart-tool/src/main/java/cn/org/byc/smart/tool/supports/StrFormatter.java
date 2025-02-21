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

/**
 * 字符串格式化工具类
 * 
 * <p>提供基于占位符的字符串格式化功能，主要特点：
 * <ul>
 *   <li>使用{}作为占位符</li>
 *   <li>支持转义字符处理</li>
 *   <li>按顺序替换占位符</li>
 *   <li>性能优化，预分配StringBuilder容量</li>
 * </ul>
 *
 * <p>使用示例:
 * <pre>{@code
 * // 1. 基本使用
 * String result = StrFormatter.format("Hello {}!", "World");
 * // 输出: Hello World!
 *
 * // 2. 多个占位符
 * String result = StrFormatter.format("{}年{}月{}日", 2024, 1, 1);
 * // 输出: 2024年1月1日
 *
 * // 3. 转义占位符
 * String result = StrFormatter.format("转义\\{}不替换，普通{}替换", "值");
 * // 输出: 转义{}不替换，普通值替换
 * }</pre>
 *
 * @author Ken
 */
public class StrFormatter {

    /**
     * 格式化字符串
     * <p>使用参数值依次替换字符串中的占位符{}
     * 
     * <p>格式化规则：
     * <ul>
     *   <li>使用{}作为占位符</li>
     *   <li>使用\转义{</li>
     *   <li>使用\\表示\本身</li>
     * </ul>
     *
     * @param strPattern 字符串模板
     * @param argArray 参数列表
     * @return 格式化后的字符串
     */
    public static String format(final String strPattern, final Object... argArray) {
        // 如果模板为空或者参数为空，直接返回模板
        if (Func.isBlank(strPattern) || Func.isEmpty(argArray)) {
            return strPattern;
        }

        // 获取模板字符串长度
        final int strPatternLength = strPattern.length();

        // 初始化StringBuilder，预分配容量以提高性能
        StringBuilder sbuf = new StringBuilder(strPatternLength + 50);

        // 记录已处理到的位置
        int handledPosition = 0;

        // 占位符位置
        int delimIndex;

        // 遍历参数数组，替换占位符
        for (int argIndex = 0; argIndex < argArray.length; argIndex++) {
            // 查找下一个占位符的位置
            delimIndex = strPattern.indexOf(StringPool.EMPTY_JSON, handledPosition);

            // 如果找不到占位符，说明后面的字符串都是普通字符
            if (delimIndex == -1) {
                // 如果是第一个参数，说明整个模板没有占位符
                if (handledPosition == 0) {
                    return strPattern;
                }
                // 将剩余的字符串追加到结果中
                sbuf.append(strPattern, handledPosition, strPatternLength);
                return sbuf.toString();
            }

            // 处理转义字符
            if (delimIndex > 0 && strPattern.charAt(delimIndex - 1) == StringPool.BACK_SLASH) {
                // 判断是否是双转义符
                if (delimIndex > 1 && strPattern.charAt(delimIndex - 2) == StringPool.BACK_SLASH) {
                    // 双转义符时占位符依然有效
                    sbuf.append(strPattern, handledPosition, delimIndex - 1);
                    sbuf.append(Func.toStr(argArray[argIndex]));
                    handledPosition = delimIndex + 2;
                } else {
                    // 单转义符时忽略占位符
                    argIndex--;
                    sbuf.append(strPattern, handledPosition, delimIndex - 1);
                    sbuf.append(StringPool.LEFT_BRACE);
                    handledPosition = delimIndex + 1;
                }
            } else {
                // 正常的占位符
                sbuf.append(strPattern, handledPosition, delimIndex);
                sbuf.append(Func.toStr(argArray[argIndex]));
                handledPosition = delimIndex + 2;
            }
        }

        // 追加剩余的字符串
        sbuf.append(strPattern, handledPosition, strPattern.length());

        return sbuf.toString();
    }
}
