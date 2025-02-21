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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则表达式工具类，提供常用的正则表达式模式和匹配方法。
 *
 * <p>主要功能包括：
 * <ul>
 *   <li>常用正则表达式常量定义（用户名、密码、邮箱等）</li>
 *   <li>字符串匹配验证</li>
 *   <li>正则表达式搜索</li>
 *   <li>正则表达式结果提取</li>
 * </ul>
 *
 * <p>使用示例：
 * <pre>{@code
 * // 验证邮箱格式
 * boolean isValid = RegexUtil.match(RegexUtil.EMAIL, "test@example.com");
 *
 * // 验证手机号格式
 * boolean isValid = RegexUtil.match(RegexUtil.PHONE, "13812345678");
 *
 * // 在文本中查找URL
 * boolean hasUrl = RegexUtil.find(RegexUtil.URL, "请访问 https://www.example.com");
 *
 * // 提取第一个匹配的URL
 * String url = RegexUtil.findResult(RegexUtil.URL, "网址是 https://www.example.com");
 * }</pre>
 *
 * @author Ken
 * @since 1.0.0
 */
public class RegexUtil {
    /**
     * 用户名正则表达式
     * <p>规则说明：
     * <ul>
     *   <li>必须以字母或中文开头</li>
     *   <li>可包含字母、数字、下划线和中文</li>
     *   <li>长度限制为2-12个字符</li>
     * </ul>
     */
    public static final String USER_NAME = "^[a-zA-Z\\u4E00-\\u9FA5][a-zA-Z0-9_\\u4E00-\\u9FA5]{1,11}$";

    /**
     * 密码正则表达式
     * <p>规则说明：
     * <ul>
     *   <li>允许任意字符</li>
     *   <li>长度限制为6-32个字符</li>
     * </ul>
     */
    public static final String USER_PASSWORD = "^.{6,32}$";

    /**
     * 邮箱正则表达式
     * <p>规则说明：
     * <ul>
     *   <li>用户名部分可包含字母、数字、下划线和点号</li>
     *   <li>域名部分必须符合域名规范</li>
     *   <li>支持多级域名</li>
     * </ul>
     */
    public static final String EMAIL = "^\\w+([-+.]*\\w+)*@([\\da-z](-[\\da-z])?)+(\\.{1,2}[a-z]+)+$";

    /**
     * 手机号正则表达式
     * <p>规则说明：
     * <ul>
     *   <li>必须以1开头</li>
     *   <li>第二位必须是3-9之间的数字</li>
     *   <li>后面必须是9位数字</li>
     *   <li>总长度为11位</li>
     * </ul>
     */
    public static final String PHONE = "^1[3456789]\\d{9}$";

    /**
     * 手机号或邮箱正则表达式
     * <p>匹配手机号或邮箱格式，是 {@link #PHONE} 和 {@link #EMAIL} 的组合。
     */
    public static final String EMAIL_OR_PHONE = EMAIL + "|" + PHONE;

    /**
     * URL正则表达式
     * <p>规则说明：
     * <ul>
     *   <li>支持http和https协议（可选）</li>
     *   <li>支持带端口号的URL</li>
     *   <li>支持路径参数</li>
     *   <li>支持查询参数</li>
     * </ul>
     */
    public static final String URL = "^(https?:\\/\\/)?([\\da-z\\.-]+)\\.([a-z\\.]{2,6})(:[\\d]+)?([\\/\\w\\.-]*)*\\/?$";

    /**
     * 身份证号正则表达式（简单校验）
     * <p>规则说明：
     * <ul>
     *   <li>支持15位老身份证号</li>
     *   <li>支持18位新身份证号（最后一位可以是数字或X）</li>
     *   <li>仅做格式校验，不校验校验位</li>
     * </ul>
     */
    public static final String ID_CARD = "^\\d{15}$|^\\d{17}([0-9]|X)$";

    /**
     * 域名正则表达式
     * <p>规则说明：
     * <ul>
     *   <li>必须以字母或数字开头</li>
     *   <li>可包含字母、数字、点号和连字符</li>
     *   <li>顶级域名长度为2-4个字符</li>
     * </ul>
     */
    public static final String DOMAIN = "^[0-9a-zA-Z]+[0-9a-zA-Z\\.-]*\\.[a-zA-Z]{2,4}$";

    /**
     * 编译传入正则表达式和字符串去匹配，忽略大小写
     *
     * <p>使用示例：
     * <pre>{@code
     * boolean isValid = RegexUtil.match("^test", "Test123");  // 返回 true
     * boolean isValid = RegexUtil.match("^abc", "def");       // 返回 false
     * }</pre>
     *
     * @param regex 正则表达式
     * @param beTestString 待测试的字符串
     * @return 如果字符串完全匹配正则表达式返回true，否则返回false
     */
    public static boolean match(String regex, String beTestString) {
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(beTestString);
        return matcher.matches();
    }

    /**
     * 编译传入正则表达式在字符串中寻找，如果匹配到则为true
     *
     * <p>使用示例：
     * <pre>{@code
     * boolean hasEmail = RegexUtil.find(EMAIL, "联系邮箱：test@example.com");  // 返回 true
     * boolean hasPhone = RegexUtil.find(PHONE, "文本中没有手机号");           // 返回 false
     * }</pre>
     *
     * @param regex 正则表达式
     * @param beTestString 待测试的字符串
     * @return 如果字符串中包含匹配正则表达式的子串返回true，否则返回false
     */
    public static boolean find(String regex, String beTestString) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(beTestString);
        return matcher.find();
    }

    /**
     * 编译传入正则表达式在字符串中寻找，如果找到返回第一个结果
     *
     * <p>使用示例：
     * <pre>{@code
     * String email = RegexUtil.findResult(EMAIL, "邮箱：test@example.com");  // 返回 "test@example.com"
     * String phone = RegexUtil.findResult(PHONE, "文本中没有手机号");        // 返回 null
     * }</pre>
     *
     * @param regex 正则表达式
     * @param beFoundString 待查找的字符串
     * @return 第一个匹配的子串，如果没有匹配返回null
     */
    @Nullable
    public static String findResult(String regex, String beFoundString) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(beFoundString);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }
}
