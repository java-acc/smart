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

import cn.org.byc.smart.tool.utils.ExceptionUtils;

import java.util.Objects;
import java.util.function.Function;

/**
 * 异常处理工具类，用于处理函数式编程中的受检异常
 * 将可能抛出受检异常的函数转换为仅抛出非受检异常的函数
 *
 * @author Ken
 */
public class Try {
    /**
     * 将一个可能抛出受检异常的函数转换为仅抛出运行时异常的函数
     * 
     * @param mapper 原始函数
     * @param <T> 输入参数类型
     * @param <R> 返回值类型
     * @return 转换后的函数，该函数将受检异常包装为运行时异常
     * @throws NullPointerException 如果mapper为null
     */
    public static <T, R> Function<T, R> of(UncheckedFunction<T, R> mapper) {
        Objects.requireNonNull(mapper);
        return t -> {
            try {
                return mapper.apply(t);
            } catch (Exception e) {
                throw ExceptionUtils.unchecked(e);
            }
        };
    }

    /**
     * 可能抛出受检异常的函数接口
     * 
     * @param <T> 输入参数类型
     * @param <R> 返回值类型
     */
    @FunctionalInterface
    public interface UncheckedFunction<T, R> {
        /**
         * 对输入参数进行处理并返回结果
         *
         * @param t 输入参数
         * @return 处理结果
         * @throws Exception 处理过程中可能抛出的任何异常
         */
        R apply(T t) throws Exception;
    }
}
