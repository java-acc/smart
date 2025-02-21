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

import org.springframework.util.Assert;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;

/**
 * AES 加密工具类，完全兼容微信所使用的 AES 加密方式。
 * 
 * <p>特点：
 * <ul>
 *   <li>使用 AES/CBC/NoPadding 加密模式</li>
 *   <li>使用 PKCS7 填充算法</li>
 *   <li>密钥长度必须为 32 字节（256 位）</li>
 *   <li>IV 向量取自密钥的前 16 字节</li>
 * </ul>
 * 
 * <p>使用示例：
 * <pre>{@code
 * // 生成 AES 密钥
 * String key = AesUtil.genAesKey();  // 生成 32 字节的随机密钥
 * 
 * // 加密字符串
 * String content = "Hello, World!";
 * byte[] encrypted = AesUtil.encrypt(content, key);
 * 
 * // 解密数据
 * String decrypted = AesUtil.decryptToStr(encrypted, key);  // 得到 "Hello, World!"
 * 
 * // 使用指定字符集
 * byte[] encryptedGbk = AesUtil.encrypt(content, Charset.forName("GBK"), key);
 * String decryptedGbk = AesUtil.decryptToStr(encryptedGbk, key, Charset.forName("GBK"));
 * }</pre>
 *
 * @author Ken
 * @since 1.0.0
 */
public class AesUtil {

    private AesUtil() {
    }

    /**
     * 生成 32 字节的随机 AES 密钥
     * 
     * <p>使用示例：
     * <pre>{@code
     * String key = AesUtil.genAesKey();  // 生成随机密钥
     * System.out.println(key.length());  // 输出: 32
     * }</pre>
     *
     * @return 32 字节的随机字符串密钥
     */
    public static String genAesKey() {
        return StringUtil.random(32);
    }

    /**
     * 使用 AES 密钥加密字节数组
     * 
     * <p>使用示例：
     * <pre>{@code
     * byte[] content = "Hello".getBytes(StandardCharsets.UTF_8);
     * String key = AesUtil.genAesKey();
     * byte[] encrypted = AesUtil.encrypt(content, key);
     * }</pre>
     *
     * @param content 要加密的内容
     * @param aesTextKey AES 密钥字符串（必须是 32 字节长度）
     * @return 加密后的字节数组
     * @throws IllegalArgumentException 如果密钥长度不是 32 字节
     * @throws RuntimeException 如果加密过程中发生错误
     */
    public static byte[] encrypt(byte[] content, String aesTextKey) {
        return encrypt(content, aesTextKey.getBytes(Charsets.UTF_8));
    }

    /**
     * 使用 AES 密钥加密字符串（使用 UTF-8 编码）
     * 
     * <p>使用示例：
     * <pre>{@code
     * String content = "Hello, World!";
     * String key = AesUtil.genAesKey();
     * byte[] encrypted = AesUtil.encrypt(content, key);
     * }</pre>
     *
     * @param content 要加密的字符串
     * @param aesTextKey AES 密钥字符串（必须是 32 字节长度）
     * @return 加密后的字节数组
     * @throws IllegalArgumentException 如果密钥长度不是 32 字节
     * @throws RuntimeException 如果加密过程中发生错误
     */
    public static byte[] encrypt(String content, String aesTextKey) {
        return encrypt(content.getBytes(Charsets.UTF_8), aesTextKey.getBytes(Charsets.UTF_8));
    }

    /**
     * 使用 AES 密钥加密字符串，支持指定字符集
     * 
     * <p>使用示例：
     * <pre>{@code
     * String content = "你好，世界！";
     * String key = AesUtil.genAesKey();
     * byte[] encrypted = AesUtil.encrypt(content, Charset.forName("GBK"), key);
     * }</pre>
     *
     * @param content 要加密的字符串
     * @param charset 字符串的字符集
     * @param aesTextKey AES 密钥字符串（必须是 32 字节长度）
     * @return 加密后的字节数组
     * @throws IllegalArgumentException 如果密钥长度不是 32 字节
     * @throws RuntimeException 如果加密过程中发生错误
     */
    public static byte[] encrypt(String content, java.nio.charset.Charset charset, String aesTextKey) {
        return encrypt(content.getBytes(charset), aesTextKey.getBytes(Charsets.UTF_8));
    }

    /**
     * 使用 AES 密钥解密数据并返回字符串（使用 UTF-8 编码）
     * 
     * <p>使用示例：
     * <pre>{@code
     * String key = AesUtil.genAesKey();
     * byte[] encrypted = AesUtil.encrypt("Hello", key);
     * String decrypted = AesUtil.decryptToStr(encrypted, key);  // 得到 "Hello"
     * }</pre>
     *
     * @param content 要解密的数据
     * @param aesTextKey AES 密钥字符串（必须是 32 字节长度）
     * @return 解密后的字符串
     * @throws IllegalArgumentException 如果密钥长度不是 32 字节
     * @throws RuntimeException 如果解密过程中发生错误
     */
    public static String decryptToStr(byte[] content, String aesTextKey) {
        return new String(decrypt(content, aesTextKey.getBytes(Charsets.UTF_8)), Charsets.UTF_8);
    }

    /**
     * 使用 AES 密钥解密数据并返回字符串，支持指定字符集
     * 
     * <p>使用示例：
     * <pre>{@code
     * String key = AesUtil.genAesKey();
     * byte[] encrypted = AesUtil.encrypt("你好", Charset.forName("GBK"), key);
     * String decrypted = AesUtil.decryptToStr(encrypted, key, Charset.forName("GBK"));
     * }</pre>
     *
     * @param content 要解密的数据
     * @param aesTextKey AES 密钥字符串（必须是 32 字节长度）
     * @param charset 解密后字符串的字符集
     * @return 解密后的字符串
     * @throws IllegalArgumentException 如果密钥长度不是 32 字节
     * @throws RuntimeException 如果解密过程中发生错误
     */
    public static String decryptToStr(byte[] content, String aesTextKey, java.nio.charset.Charset charset) {
        return new String(decrypt(content, aesTextKey.getBytes(Charsets.UTF_8)), charset);
    }

    /**
     * 使用 AES 密钥解密数据
     *
     * @param content 要解密的数据
     * @param aesTextKey AES 密钥字符串（必须是 32 字节长度）
     * @return 解密后的字节数组
     * @throws IllegalArgumentException 如果密钥长度不是 32 字节
     * @throws RuntimeException 如果解密过程中发生错误
     */
    public static byte[] decrypt(byte[] content, String aesTextKey) {
        return decrypt(content, aesTextKey.getBytes(Charsets.UTF_8));
    }

    /**
     * 使用 AES 密钥字节数组进行加密
     * 
     * <p>这是一个底层的加密方法，直接使用字节数组作为密钥。
     * 使用 AES/CBC/NoPadding 模式，并应用 PKCS7 填充。
     * IV 向量使用密钥的前 16 字节。
     *
     * @param content 要加密的内容
     * @param aesKey AES 密钥字节数组（必须是 32 字节长度）
     * @return 加密后的字节数组
     * @throws IllegalArgumentException 如果密钥长度不是 32 字节
     * @throws RuntimeException 如果加密过程中发生错误
     */
    public static byte[] encrypt(byte[] content, byte[] aesKey) {
        Assert.isTrue(aesKey.length == 32, "IllegalAesKey, aesKey's length must be 32");
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec keySpec = new SecretKeySpec(aesKey, "AES");
            IvParameterSpec iv = new IvParameterSpec(aesKey, 0, 16);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);
            return cipher.doFinal(Pkcs7Encoder.encode(content));
        } catch (Exception e) {
            throw Exceptions.unchecked(e);
        }
    }

    /**
     * 使用 AES 密钥字节数组进行解密
     * 
     * <p>这是一个底层的解密方法，直接使用字节数组作为密钥。
     * 使用 AES/CBC/NoPadding 模式，并应用 PKCS7 填充。
     * IV 向量使用密钥的前 16 字节。
     *
     * @param encrypted 要解密的数据
     * @param aesKey AES 密钥字节数组（必须是 32 字节长度）
     * @return 解密后的字节数组
     * @throws IllegalArgumentException 如果密钥长度不是 32 字节
     * @throws RuntimeException 如果解密过程中发生错误
     */
    public static byte[] decrypt(byte[] encrypted, byte[] aesKey) {
        Assert.isTrue(aesKey.length == 32, "IllegalAesKey, aesKey's length must be 32");
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec keySpec = new SecretKeySpec(aesKey, "AES");
            IvParameterSpec iv = new IvParameterSpec(Arrays.copyOfRange(aesKey, 0, 16));
            cipher.init(Cipher.DECRYPT_MODE, keySpec, iv);
            return Pkcs7Encoder.decode(cipher.doFinal(encrypted));
        } catch (Exception e) {
            throw Exceptions.unchecked(e);
        }
    }

    /**
     * PKCS7 填充算法的实现类
     */
    static class Pkcs7Encoder {
        /**
         * 块大小为 32 字节
         */
        static int BLOCK_SIZE = 32;

        /**
         * 对数据进行 PKCS7 填充
         *
         * @param src 要填充的数据
         * @return 填充后的数据
         */
        static byte[] encode(byte[] src) {
            int count = src.length;
            // 计算需要填充的位数
            int amountToPad = BLOCK_SIZE - (count % BLOCK_SIZE);
            if (amountToPad == 0) {
                amountToPad = BLOCK_SIZE;
            }
            // 获得补位所用的字符
            byte pad = (byte) (amountToPad & 0xFF);
            byte[] pads = new byte[amountToPad];
            for (int index = 0; index < amountToPad; index++) {
                pads[index] = pad;
            }
            int length = count + amountToPad;
            byte[] dest = new byte[length];
            System.arraycopy(src, 0, dest, 0, count);
            System.arraycopy(pads, 0, dest, count, amountToPad);
            return dest;
        }

        /**
         * 对数据进行 PKCS7 去填充
         *
         * @param decrypted 要去填充的数据
         * @return 去填充后的数据
         */
        static byte[] decode(byte[] decrypted) {
            int pad = decrypted[decrypted.length - 1];
            if (pad < 1 || pad > BLOCK_SIZE) {
                pad = 0;
            }
            if (pad > 0) {
                return Arrays.copyOfRange(decrypted, 0, decrypted.length - pad);
            }
            return decrypted;
        }
    }
}
