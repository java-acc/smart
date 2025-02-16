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
 * 字符串常量池接口，提供系统中所有通用的字符串常量定义
 * 
 * <p>该接口的主要目的是：
 * <ul>
 *     <li>集中管理系统中常用的字符串常量，避免字符串字面量的重复定义</li>
 *     <li>提高字符串常量的复用性，减少内存占用</li>
 *     <li>统一字符串常量的命名和使用方式，提高代码可维护性</li>
 *     <li>防止字符串常量拼写错误，提高代码质量</li>
 * </ul>
 * 
 * <p>常量分类：
 * <ul>
 *     <li>基本符号：包含常用的标点符号、运算符等</li>
 *     <li>括号相关：包含各种类型的括号及其配对符号</li>
 *     <li>空值和布尔值：包含空字符串、null值、布尔值等</li>
 *     <li>特殊字符：包含换行符、回车符、制表符等</li>
 *     <li>字符编码：包含常用的字符编码标识</li>
 *     <li>HTTP方法：包含标准的HTTP请求方法</li>
 *     <li>JSON相关：包含JSON格式的特殊值</li>
 * </ul>
 * 
 * <p>使用建议：
 * <ul>
 *     <li>优先使用本接口定义的常量，而不是字面量</li>
 *     <li>当需要新增常量时，应按照对应的分类添加</li>
 *     <li>常量命名应该清晰明确，避免歧义</li>
 * </ul>
 * 
 * @author Ken
 * @see java.lang.String
 * @see org.springframework.util.StringUtils
 */
public interface StringPool {
    // 基本符号
    /** 符号：& */
    String AMPERSAND         = "&";
    /** 单词：and */
    String AND               = "and";
    /** 符号：@ */
    String AT                = "@";
    /** 符号：* */
    String ASTERISK          = "*";
    /** 符号：*的别名 */
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
    /** HTTP GET方法 */
    String GET               = "GET";
    /** HTTP POST方法 */
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
    /** 空JSON数组 */
    String EMPTY_JSON_ARRAY  = "[]";
    /** 空JSON对象 */
    String EMPTY_JSON_OBJECT = "{}";
}
