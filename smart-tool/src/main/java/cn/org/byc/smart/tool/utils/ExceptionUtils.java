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

import cn.org.byc.smart.tool.supports.FastStringWriter;

import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;

/**
 * 异常工具类，提供异常处理和转换的通用方法
 *
 * @author Ken
 */
public class ExceptionUtils {
    /**
     * 将检查型异常(CheckedException)转换为非检查型异常(UncheckedException)
     * 
     * 转换规则：
     * 1. IllegalAccessException、IllegalArgumentException、NoSuchMethodException 转换为 IllegalArgumentException
     * 2. InvocationTargetException 转换为其目标异常的RuntimeException
     * 3. 已经是RuntimeException的直接返回
     * 4. 其他异常包装为RuntimeException
     *
     * @param e 原始异常
     * @return 转换后的运行时异常
     */
    public static RuntimeException unchecked(Throwable e) {
        if (e instanceof IllegalAccessException || e instanceof IllegalArgumentException
                || e instanceof NoSuchMethodException) {
            return new IllegalArgumentException(e);
        } else if (e instanceof InvocationTargetException) {
            return new RuntimeException(((InvocationTargetException) e).getTargetException());
        } else if (e instanceof RuntimeException) {
            return (RuntimeException) e;
        } else {
            return new RuntimeException(e);
        }
    }

    /**
     * 解包代理异常，获取原始异常
     * 
     * 主要处理以下两种情况：
     * 1. InvocationTargetException：获取其目标异常
     * 2. UndeclaredThrowableException：获取其未声明的异常
     *
     * @param wrapped 包装的异常
     * @return 原始异常
     */
    public static Throwable unwrap(Throwable wrapped) {
        Throwable unwrapped = wrapped;
        while (true) {
            if (unwrapped instanceof InvocationTargetException) {
                unwrapped = ((InvocationTargetException) unwrapped).getTargetException();
            } else if (unwrapped instanceof UndeclaredThrowableException) {
                unwrapped = ((UndeclaredThrowableException) unwrapped).getUndeclaredThrowable();
            } else {
                return unwrapped;
            }
        }
    }

    /**
     * 将异常堆栈转换为字符串
     * 
     * 使用FastStringWriter提高性能，避免使用默认的StringWriter
     *
     * @param ex 需要转换的异常
     * @return 异常堆栈的字符串表示
     */
    public static String getStackTraceAsString(Throwable ex) {
        FastStringWriter stringWriter = new FastStringWriter();
        ex.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }
}
