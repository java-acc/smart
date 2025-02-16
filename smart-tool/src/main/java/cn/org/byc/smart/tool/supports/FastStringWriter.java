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
 * 高性能的字符串写入器，基于StringBuilder实现
 * 相比于StringWriter，该实现更加轻量和高效，不需要同步锁
 *
 * @author Ken
 */
public class FastStringWriter extends Writer {
    // 内部使用StringBuilder作为缓冲区
    private final StringBuilder builder;

    /**
     * 创建一个默认容量（64字符）的FastStringWriter
     */
    public FastStringWriter() {
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
        builder = new StringBuilder(capacity);
    }

    /**
     * 使用指定的StringBuilder创建FastStringWriter
     * 如果提供的builder为null，则创建一个新的默认容量（64字符）的StringBuilder
     *
     * @param builder 指定的StringBuilder，可以为null
     */
    public FastStringWriter(@Nullable final StringBuilder builder) {
        this.builder = builder != null ? builder : new StringBuilder(64);
    }

    /**
     * 写入单个字符
     *
     * @param c 要写入的字符
     */
    @Override
    public void write(int c) {
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
        if ((off < 0) || (off > cbuilder.length) || (len < 0) ||
                ((off + len) > cbuilder.length) || ((off + len) < 0)) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return;
        }
        builder.append(cbuilder, off, len);
    }

    /**
     * 写入字符串
     *
     * @param str 要写入的字符串
     */
    @Override
    public void write(String str) {
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
        builder.append(str, off, off + len);
    }

    /**
     * 追加字符序列
     * 如果字符序列为null，则追加"null"字符串
     *
     * @param csq 要追加的字符序列
     * @return 当前FastStringWriter实例
     */
    @Override
    public FastStringWriter append(CharSequence csq) {
        if (csq == null) {
            write(StringPool.NULL);
        } else {
            write(csq.toString());
        }
        return this;
    }

    /**
     * 追加字符序列的指定部分
     * 如果字符序列为null，则使用"null"字符串的指定部分
     *
     * @param csq 要追加的字符序列
     * @param start 开始位置
     * @param end 结束位置
     * @return 当前FastStringWriter实例
     */
    @Override
    public FastStringWriter append(CharSequence csq, int start, int end) {
        CharSequence cs = (csq == null ? StringPool.NULL : csq);
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
        write(c);
        return this;
    }

    /**
     * 返回缓冲区中的字符串
     *
     * @return 缓冲区的字符串表示
     */
    @Override
    public String toString() {
        return builder.toString();
    }

    /**
     * 刷新缓冲区
     * 由于使用StringBuilder实现，此方法不执行任何操作
     */
    @Override
    public void flush() {
    }

    /**
     * 关闭写入器
     * 清空缓冲区并释放多余的空间
     */
    @Override
    public void close() {
        builder.setLength(0);
        builder.trimToSize();
    }
}
