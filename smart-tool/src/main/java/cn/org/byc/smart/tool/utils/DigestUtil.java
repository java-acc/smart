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
import org.springframework.util.DigestUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 消息摘要工具类，提供常用的哈希算法实现。
 * 
 * <p>支持的算法包括：
 * <ul>
 *   <li>MD5</li>
 *   <li>SHA-1</li>
 *   <li>SHA-256</li>
 *   <li>SHA-384</li>
 *   <li>SHA-512</li>
 * </ul>
 * 
 * <p>主要功能：
 * <ul>
 *   <li>字符串和字节数组的哈希计算</li>
 *   <li>安全的字符串比较（防止时序攻击）</li>
 *   <li>自定义组合加密（MD5+SHA1）</li>
 *   <li>十六进制编码转换</li>
 * </ul>
 * 
 * <p>使用示例：
 * <pre>{@code
 * // MD5 哈希
 * String md5 = DigestUtil.md5Hex("Hello, World!");
 * 
 * // SHA 系列哈希
 * String sha1 = DigestUtil.sha1("Hello, World!");
 * String sha256 = DigestUtil.sha256("Hello, World!");
 * 
 * // 安全的字符串比较
 * boolean isEqual = DigestUtil.slowEquals("hash1", "hash2");
 * 
 * // 自定义组合加密
 * String encrypted = DigestUtil.encrypt("password");  // 先 MD5 后 SHA1
 * }</pre>
 *
 * @author Ken
 * @since 1.0.0
 */
public class DigestUtil extends DigestUtils {

    private static final char[] HEX_DIGITS = "0123456789abcdef".toCharArray();

    /**
     * 计算字符串的 MD5 哈希值，并返回 32 位十六进制字符串
     * 
     * <p>使用示例：
     * <pre>{@code
     * String hash = DigestUtil.md5Hex("Hello, World!");
     * System.out.println(hash);  // 输出 32 位十六进制字符串
     * }</pre>
     *
     * @param data 要计算哈希的字符串（使用 UTF-8 编码）
     * @return 32 位十六进制的 MD5 哈希字符串
     */
    public static String md5Hex(final String data) {
        return DigestUtil.md5DigestAsHex(data.getBytes(Charsets.UTF_8));
    }

    /**
     * 计算字节数组的 MD5 哈希值，并返回 32 位十六进制字符串
     * 
     * <p>使用示例：
     * <pre>{@code
     * byte[] data = "Hello, World!".getBytes(StandardCharsets.UTF_8);
     * String hash = DigestUtil.md5Hex(data);
     * }</pre>
     *
     * @param bytes 要计算哈希的字节数组
     * @return 32 位十六进制的 MD5 哈希字符串
     */
    public static String md5Hex(final byte[] bytes) {
        return DigestUtil.md5DigestAsHex(bytes);
    }

    /**
     * 计算字符串的 SHA-1 哈希值
     * 
     * <p>使用示例：
     * <pre>{@code
     * String hash = DigestUtil.sha1("Hello, World!");
     * System.out.println(hash);  // 输出 40 位十六进制字符串
     * }</pre>
     *
     * @param srcStr 要计算哈希的字符串
     * @return 40 位十六进制的 SHA-1 哈希字符串
     */
    public static String sha1(String srcStr) {
        return hash("SHA-1", srcStr);
    }

    /**
     * 计算字符串的 SHA-256 哈希值
     * 
     * <p>使用示例：
     * <pre>{@code
     * String hash = DigestUtil.sha256("Hello, World!");
     * System.out.println(hash);  // 输出 64 位十六进制字符串
     * }</pre>
     *
     * @param srcStr 要计算哈希的字符串
     * @return 64 位十六进制的 SHA-256 哈希字符串
     */
    public static String sha256(String srcStr) {
        return hash("SHA-256", srcStr);
    }

    /**
     * 计算字符串的 SHA-384 哈希值
     * 
     * <p>使用示例：
     * <pre>{@code
     * String hash = DigestUtil.sha384("Hello, World!");
     * System.out.println(hash);  // 输出 96 位十六进制字符串
     * }</pre>
     *
     * @param srcStr 要计算哈希的字符串
     * @return 96 位十六进制的 SHA-384 哈希字符串
     */
    public static String sha384(String srcStr) {
        return hash("SHA-384", srcStr);
    }

    /**
     * 计算字符串的 SHA-512 哈希值
     * 
     * <p>使用示例：
     * <pre>{@code
     * String hash = DigestUtil.sha512("Hello, World!");
     * System.out.println(hash);  // 输出 128 位十六进制字符串
     * }</pre>
     *
     * @param srcStr 要计算哈希的字符串
     * @return 128 位十六进制的 SHA-512 哈希字符串
     */
    public static String sha512(String srcStr) {
        return hash("SHA-512", srcStr);
    }

    /**
     * 使用指定的哈希算法计算字符串的哈希值
     * 
     * <p>支持的算法包括但不限于：
     * <ul>
     *   <li>MD5</li>
     *   <li>SHA-1</li>
     *   <li>SHA-256</li>
     *   <li>SHA-384</li>
     *   <li>SHA-512</li>
     * </ul>
     *
     * @param algorithm 哈希算法名称
     * @param srcStr 要计算哈希的字符串
     * @return 十六进制的哈希字符串
     * @throws RuntimeException 如果指定的算法不存在
     */
    public static String hash(String algorithm, String srcStr) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] bytes = md.digest(srcStr.getBytes(Charsets.UTF_8));
            return toHex(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw Exceptions.unchecked(e);
        }
    }

    /**
     * 将字节数组转换为十六进制字符串
     * 
     * <p>使用示例：
     * <pre>{@code
     * byte[] bytes = new byte[]{(byte)0xCA, (byte)0xFE};
     * String hex = DigestUtil.toHex(bytes);  // 返回 "cafe"
     * }</pre>
     *
     * @param bytes 要转换的字节数组
     * @return 十六进制字符串（小写）
     */
    public static String toHex(byte[] bytes) {
        StringBuilder ret = new StringBuilder(bytes.length * 2);
        for (byte aByte : bytes) {
            ret.append(HEX_DIGITS[(aByte >> 4) & 0x0f]);
            ret.append(HEX_DIGITS[aByte & 0x0f]);
        }
        return ret.toString();
    }

    /**
     * 安全地比较两个字符串是否相等，可以防止时序攻击
     * 
     * <p>此方法的执行时间只取决于字符串的长度，而与字符串的内容无关，
     * 这可以防止通过计时攻击来推测密码或哈希值。
     * 
     * <p>使用示例：
     * <pre>{@code
     * // 安全地比较两个哈希值
     * boolean isEqual = DigestUtil.slowEquals(hash1, hash2);
     * 
     * // 处理 null 值
     * boolean isEqual2 = DigestUtil.slowEquals(null, hash);  // 返回 false
     * }</pre>
     *
     * @param a 第一个字符串
     * @param b 第二个字符串
     * @return 如果两个字符串相等返回 true，否则返回 false
     */
    public static boolean slowEquals(@Nullable String a, @Nullable String b) {
        if (a == null || b == null) {
            return false;
        }
        return slowEquals(a.getBytes(Charsets.UTF_8), b.getBytes(Charsets.UTF_8));
    }

    /**
     * 安全地比较两个字节数组是否相等，可以防止时序攻击
     * 
     * <p>此方法的执行时间只取决于数组的长度，而与数组的内容无关，
     * 这可以防止通过计时攻击来推测密码或哈希值。
     * 
     * <p>使用示例：
     * <pre>{@code
     * byte[] hash1 = DigestUtil.sha256("password1").getBytes();
     * byte[] hash2 = DigestUtil.sha256("password2").getBytes();
     * boolean isEqual = DigestUtil.slowEquals(hash1, hash2);
     * }</pre>
     *
     * @param a 第一个字节数组
     * @param b 第二个字节数组
     * @return 如果两个数组相等返回 true，否则返回 false
     */
    public static boolean slowEquals(@Nullable byte[] a, @Nullable byte[] b) {
        if (a == null || b == null) {
            return false;
        }
        if (a.length != b.length) {
            return false;
        }
        int diff = a.length ^ b.length;
        for (int i = 0; i < a.length; i++) {
            diff |= a[i] ^ b[i];
        }
        return diff == 0;
    }

    /**
     * 对字符串进行自定义组合加密（先 MD5 后 SHA1）
     * 
     * <p>此方法首先计算字符串的 MD5 哈希值，然后再对该哈希值计算 SHA1，
     * 提供了更强的加密强度。
     * 
     * <p>使用示例：
     * <pre>{@code
     * String password = "myPassword123";
     * String encrypted = DigestUtil.encrypt(password);
     * }</pre>
     *
     * @param data 要加密的字符串
     * @return 加密后的字符串（40 位十六进制）
     */
    public static String encrypt(String data) {
        return sha1(md5Hex(data));
    }
}
