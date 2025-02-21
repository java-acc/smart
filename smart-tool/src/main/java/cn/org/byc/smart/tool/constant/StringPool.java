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

package cn.org.byc.smart.tool.constant;

/**
 * 字符串常量池接口
 * 
 * <p>该接口定义了系统中所有通用的字符串常量,包括:
 * <ul>
 *   <li>基本符号: &, @, *, /, \, :, 等</li>
 *   <li>括号: (), [], {}, <></li>
 *   <li>空值和布尔值: "", null, true, false</li>
 *   <li>特殊字符: 换行符、回车符、制表符等</li>
 *   <li>HTTP方法: GET, POST, PUT, DELETE等</li>
 *   <li>字符编码: UTF-8, GBK, ISO-8859-1</li>
 *   <li>JSON相关: 空JSON对象、数组等</li>
 * </ul>
 *
 * <p>使用示例:
 * <pre>{@code
 * // 使用基本符号
 * String path = name + StringPool.SLASH + id;
 * 
 * // 使用括号
 * String expr = StringPool.LEFT_BRACE + value + StringPool.RIGHT_BRACE;
 * 
 * // 使用HTTP方法
 * if(StringPool.GET.equals(method)) {
 *   // 处理GET请求
 * }
 * 
 * // 使用字符编码
 * byte[] bytes = string.getBytes(StringPool.UTF_8);
 * }</pre>
 *
 * <p>优点:
 * <ul>
 *   <li>统一管理系统中的字符串常量,避免重复定义</li>
 *   <li>提高字符串常量复用性,减少内存占用</li>
 *   <li>统一命名规范,提高代码可维护性</li>
 *   <li>防止字符串拼写错误</li>
 * </ul>
 *
 * @author Ken
 * @see java.lang.String
 * @see org.springframework.util.StringUtils
 */
public interface StringPool {
    // 基本符号
    /**
     * 符号: &
     * <p>常用于URL参数连接和XML转义
     */
    String AMPERSAND         = "&";
    /**
     * 单词: and
     * <p>常用于SQL语句中的条件连接
     */
    String AND               = "and";
    /**
     * 符号: @
     * <p>常用于Java注解标记和邮件地址
     */
    String AT                = "@";
    /**
     * 符号: *
     * <p>常用于通配符匹配
     */
    String ASTERISK          = "*";
    /**
     * 符号: * 的别名
     * <p>与{@link #ASTERISK}相同,提供更直观的命名
     */
    String STAR              = ASTERISK;
    /** 符号：/ */
    String SLASH             = "/";
    /** 符号：\ */
    char   BACK_SLASH        = '\\';
    /** 符号：#// */
    String DOUBLE_SLASH      = "#//";
    /** 符号：: */
    String COLON             = ":";
    /** 符号：, */
    String COMMA             = ",";
    /** 符号：- */
    String DASH              = "-";
    /** 符号：$ */
    String DOLLAR            = "$";
    /** 符号：. */
    String DOT               = ".";

    // 空值和布尔值
    /** 空字符串 */
    String EMPTY             = "";
    /** 空JSON对象字符串 */
    String EMPTY_JSON        = "{}";
    /** 符号：= */
    String EQUALS            = "=";
    /** 布尔值：false */
    String FALSE             = "false";
    /** 符号：# */
    String HASH              = "#";
    /** 符号：^ */
    String HAT               = "^";

    // 括号
    /** 左花括号：{ */
    String LEFT_BRACE        = "{";
    /** 左圆括号：( */
    String LEFT_BRACKET      = "(";
    /** 左尖括号：< */
    String LEFT_CHEV         = "<";

    // 特殊字符
    /** 换行符：\n */
    String NEWLINE           = "\n";
    /** 字母n */
    String N                 = "n";
    /** 否定词：no */
    String NO                = "no";
    /** null值字符串 */
    String NULL              = "null";
    /** 开关状态：off */
    String OFF               = "off";
    /** 开关状态：on */
    String ON                = "on";

    // 其他符号
    /** 符号：% */
    String PERCENT           = "%";
    /** 符号：| */
    String PIPE              = "|";
    /** 符号：+ */
    String PLUS              = "+";
    /** 符号：? */
    String QUESTION_MARK     = "?";
    /** 符号：! */
    String EXCLAMATION_MARK  = "!";
    /** 双引号：" */
    String QUOTE             = "\"";
    /** 回车符：\r */
    String RETURN            = "\r";
    /** 制表符：\t */
    String TAB               = "\t";

    // 右括号
    /** 右花括号：} */
    String RIGHT_BRACE       = "}";
    /** 右圆括号：) */
    String RIGHT_BRACKET     = ")";
    /** 右尖括号：> */
    String RIGHT_CHEV        = ">";

    // 引号和分隔符
    /** 分号：; */
    String SEMICOLON         = ";";
    /** 单引号：' */
    String SINGLE_QUOTE      = "'";
    /** 反引号：` */
    String BACKTICK          = "`";
    /** 空格 */
    String SPACE             = " ";
    /** 波浪号：~ */
    String TILDA             = "~";

    // 方括号
    /** 左方括号：[ */
    String LEFT_SQ_BRACKET   = "[";
    /** 右方括号：] */
    String RIGHT_SQ_BRACKET  = "]";

    // 布尔值和特殊字符
    /** 布尔值：true */
    String TRUE              = "true";
    /** 下划线：_ */
    String UNDERSCORE        = "_";

    // 字符编码
    /** UTF-8编码 */
    String UTF_8             = "UTF-8";
    /** GBK编码 */
    String GBK               = "GBK";
    /** ISO-8859-1编码 */
    String ISO_8859_1        = "ISO-8859-1";

    // 是否值
    /** 是：y */
    String Y                 = "y";
    /** 是：yes */
    String YES               = "yes";
    /** 数字：1 */
    String ONE               = "1";
    /** 数字：0 */
    String ZERO              = "0";

    // 特殊标记
    /** ${占位符 */
    String DOLLAR_LEFT_BRACE = "${";
    /** 大写字母A */
    char   U_A               = 'A';
    /** 小写字母a */
    char   L_A               = 'a';
    /** 大写字母Z */
    char   U_Z               = 'Z';
    /** 小写字母z */
    char   L_Z               = 'z';
    /** 未知标记 */
    String UNKNOWN           = "unknown";

    // HTTP方法
    /**
     * HTTP GET方法
     * <p>用于从服务器获取资源
     * 
     * <p>使用示例:
     * <pre>{@code
     * if(StringPool.GET.equals(request.getMethod())) {
     *   // 处理GET请求
     * }
     * }</pre>
     */
    String GET               = "GET";
    /**
     * HTTP POST方法
     * <p>用于向服务器提交数据
     * 
     * <p>使用示例:
     * <pre>{@code
     * if(StringPool.POST.equals(request.getMethod())) {
     *   // 处理POST请求
     * }
     * }</pre>
     */
    String POST              = "POST";
    /** HTTP PUT方法 */
    String PUT               = "PUT";
    /** HTTP DELETE方法 */
    String DELETE            = "DELETE";
    /** HTTP PATCH方法 */
    String PATCH             = "PATCH";
    /** HTTP HEAD方法 */
    String HEAD              = "HEAD";
    /** HTTP OPTIONS方法 */
    String OPTIONS           = "OPTIONS";
    /** HTTP TRACE方法 */
    String TRACE             = "TRACE";
    /** HTTP CONNECT方法 */
    String CONNECT           = "CONNECT";

    // JSON相关
    /**
     * 空JSON数组字符串: []
     * <p>表示一个空的JSON数组
     * 
     * <p>使用示例:
     * <pre>{@code
     * String emptyArray = StringPool.EMPTY_JSON_ARRAY;
     * // 输出: []
     * }</pre>
     */
    String EMPTY_JSON_ARRAY  = "[]";
    /**
     * 空JSON对象字符串: {}
     * <p>表示一个空的JSON对象
     * 
     * <p>使用示例:
     * <pre>{@code
     * String emptyObject = StringPool.EMPTY_JSON_OBJECT;
     * // 输出: {}
     * }</pre>
     */
    String EMPTY_JSON_OBJECT = "{}";
}
