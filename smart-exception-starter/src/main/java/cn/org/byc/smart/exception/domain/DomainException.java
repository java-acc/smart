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

package cn.org.byc.smart.exception.domain;

import cn.hutool.core.util.StrUtil;
import cn.org.byc.smart.exception.BaseException;
import cn.org.byc.smart.exception.enums.CommonError;
import cn.org.byc.smart.exception.enums.ErrorCode;

import java.io.Serial;
/**
 * 领域异常类
 *
 * <p>表示业务领域中的异常情况，继承自{@link BaseException}。
 * 主要特点：
 * <ul>
 *     <li>支持消息参数化 - 使用{@link StrUtil#format}进行消息格式化</li>
 *     <li>默认错误码 - 默认使用{@link CommonError#UnExpected}</li>
 *     <li>支持自定义错误码 - 可以指定具体的{@link ErrorCode}</li>
 *     <li>异常链支持 - 可以包装其他异常</li>
 * </ul>
 *
 * <p>使用示例：
 * <pre>
 * // 简单消息
 * throw new DomainException("用户未找到");
 *
 * // 带参数的消息
 * throw new DomainException("用户{}未找到", userId);
 *
 * // 指定错误码
 * throw new DomainException(CommonError.NotAuthorized);
 * </pre>
 *
 * @author Ken
 * @see BaseException
 * @see CommonError
 */
public class DomainException extends BaseException {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 使用指定的错误消息构造异常
     *
     * @param message 错误消息
     */
    public DomainException(String message) {
        super(CommonError.UnExpected, message);
    }

    /**
     * 使用带参数的错误消息构造异常
     *
     * @param message 错误消息模板
     * @param params 用于格式化消息的参数
     */
    public DomainException(String message, Object... params) {
        super(CommonError.UnExpected, StrUtil.format(message, params), params);
    }

    /**
     * 使用错误消息和原因构造异常
     *
     * @param message 错误消息
     * @param cause 原因异常
     */
    public DomainException(String message, Throwable cause) {
        super(CommonError.UnExpected, message, cause);
    }

    /**
     * 使用带参数的错误消息和原因构造异常
     *
     * @param message 错误消息模板
     * @param cause 原因异常
     * @param params 用于格式化消息的参数
     */
    public DomainException(String message, Throwable cause, Object... params) {
        super(CommonError.UnExpected, StrUtil.format(message, params), cause, params);
    }

    /**
     * 使用指定的错误码构造异常
     *
     * @param error 错误码
     */
    public DomainException(ErrorCode error) {
        super(error);
    }

    /**
     * 使用指定的错误码和原因构造异常
     *
     * @param error 错误码
     * @param e 原因异常
     */
    public DomainException(ErrorCode error, Throwable e) {
        super(error, e);
    }

    /**
     * 使用指定的错误码、原因和参数构造异常
     *
     * @param error 错误码
     * @param e 原因异常
     * @param params 用于格式化消息的参数
     */
    public DomainException(ErrorCode error, Throwable e, Object... params) {
        super(error, error.getMessage(), e, params);
    }
}

