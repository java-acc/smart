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
import org.springframework.lang.Nullable;

import java.io.Writer;

/**
 * 高性能字符串写入器
 * 
 * <p>基于StringBuilder实现的轻量级字符串写入器，相比于StringWriter具有以下优势：
 * <ul>
 *   <li>无需同步锁，性能更高</li>
 *   <li>更低的内存占用</li>
 *   <li>支持指定初始容量，减少扩容开销</li>
 *   <li>支持直接使用现有StringBuilder</li>
 * </ul>
 *
 * <p>使用示例:
 * <pre>{@code
 * // 1. 使用默认容量创建
 * FastStringWriter writer = new FastStringWriter();
 * writer.write("Hello");
 * writer.append(" World");
 * String result = writer.toString(); // "Hello World"
 * 
 * // 2. 指定初始容量
 * FastStringWriter writer = new FastStringWriter(256);
 * 
 * // 3. 使用现有StringBuilder
 * StringBuilder builder = new StringBuilder();
 * FastStringWriter writer = new FastStringWriter(builder);
 * }</pre>
 *
 * @author Ken
 * @see Writer
 * @see StringBuilder
 */
public class FastStringWriter extends Writer {
    
    // 内部缓冲区，使用StringBuilder存储字符
    private final StringBuilder builder;

    /**
     * 创建一个默认容量（64字符）的FastStringWriter
     */
    public FastStringWriter() {
        // 使用默认容量64创建StringBuilder
        builder = new StringBuilder(64);
    }

    /**
     * 创建一个指定初始容量的FastStringWriter
     *
     * @param capacity 初始容量
     * @throws IllegalArgumentException 如果容量为负数
     */
    public FastStringWriter(final int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("Negative builder size");
        }
        // 使用指定容量创建StringBuilder
        builder = new StringBuilder(capacity);
    }

    /**
     * 使用指定的StringBuilder创建FastStringWriter
     *
     * @param builder 指定的StringBuilder，如果为null则创建默认容量的新实例
     */
    public FastStringWriter(@Nullable final StringBuilder builder) {
        // 如果builder为null，创建默认容量的StringBuilder
        this.builder = builder != null ? builder : new StringBuilder(64);
    }

    /**
     * 写入单个字符
     *
     * @param c 要写入的字符
     */
    @Override
    public void write(int c) {
        // 将int类型转换为char后追加到builder
        builder.append((char) c);
    }

    /**
     * 写入字符数组的指定部分
     *
     * @param cbuilder 源字符数组
     * @param off 开始位置
     * @param len 长度
     * @throws IndexOutOfBoundsException 如果索引参数超出范围
     */
    @Override
    public void write(char[] cbuilder, int off, int len) {
        // 参数验证
        if ((off < 0) || (off > cbuilder.length) || (len < 0) ||
                ((off + len) > cbuilder.length) || ((off + len) < 0)) {
            throw new IndexOutOfBoundsException();
        }
        // 如果长度为0，直接返回
        if (len == 0) {
            return;
        }
        // 追加指定范围的字符数组
        builder.append(cbuilder, off, len);
    }

    /**
     * 写入字符串
     *
     * @param str 要写入的字符串
     */
    @Override
    public void write(String str) {
        // 直接追加字符串
        builder.append(str);
    }

    /**
     * 写入字符串的指定部分
     *
     * @param str 源字符串
     * @param off 开始位置
     * @param len 长度
     */
    @Override
    public void write(String str, int off, int len) {
        // 追加字符串的指定部分
        builder.append(str, off, off + len);
    }

    /**
     * 追加字符序列
     *
     * @param csq 要追加的字符序列，如果为null则追加"null"
     * @return 当前FastStringWriter实例
     */
    @Override
    public FastStringWriter append(CharSequence csq) {
        if (csq == null) {
            // 如果字符序列为null，写入"null"字符串
            write(StringPool.NULL);
        } else {
            // 否则写入字符序列的字符串表示
            write(csq.toString());
        }
        return this;
    }

    /**
     * 追加字符序列的指定部分
     *
     * @param csq 要追加的字符序列，如果为null则使用"null"
     * @param start 开始位置
     * @param end 结束位置
     * @return 当前FastStringWriter实例
     */
    @Override
    public FastStringWriter append(CharSequence csq, int start, int end) {
        // 如果字符序列为null，使用"null"字符串
        CharSequence cs = (csq == null ? StringPool.NULL : csq);
        // 写入指定范围的子序列
        write(cs.subSequence(start, end).toString());
        return this;
    }

    /**
     * 追加单个字符
     *
     * @param c 要追加的字符
     * @return 当前FastStringWriter实例
     */
    @Override
    public FastStringWriter append(char c) {
        // 写入单个字符
        write(c);
        return this;
    }

    /**
     * 获取当前缓冲区的字符串表示
     *
     * @return 缓冲区的字符串内容
     */
    @Override
    public String toString() {
        return builder.toString();
    }

    /**
     * 刷新缓冲区
     * <p>此方法不执行任何操作，因为StringBuilder是内存缓冲，无需刷新
     */
    @Override
    public void flush() {
        // 不需要实现，StringBuilder是内存缓冲
    }

    /**
     * 关闭写入器
     * <p>清空缓冲区并释放多余的空间
     */
    @Override
    public void close() {
        // 清空StringBuilder
        builder.setLength(0);
        // 释放多余的空间
        builder.trimToSize();
    }
}
