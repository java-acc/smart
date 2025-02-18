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

package cn.org.byc.smart.exception;

import cn.org.byc.smart.exception.domain.DomainException;
import cn.org.byc.smart.exception.enums.ErrorCode;
import lombok.Getter;

import java.io.Serial;

/**
 * 系统基础异常类
 *
 * <p>所有系统的自定义异常的基类，提供：
 * <ul>
 *     <li>错误码支持 - 通过{@link ErrorCode}接口</li>
 *     <li>参数传递 - 支持传递异常相关的参数数组</li>
 *     <li>异常链支持 - 支持包装其他异常</li>
 * </ul>
 *
 * @author Ken
 * @see ErrorCode
 * @see DomainException
 */
@Getter
public class BaseException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    protected ErrorCode error;

    /**
     * 国际化消息参数
     */
    protected Object[] params;

    /**
     * 构造函数
     *
     * @param error 错误码
     */
    public BaseException(ErrorCode error) {
        super(error.getMessage());
        this.error = error;
        this.params = null;
    }

    /**
     * 构造函数
     *
     * @param error 错误码
     * @param message 错误消息
     */
    public BaseException(ErrorCode error, String message) {
        super(message);
        this.error = error;
        this.params = null;
    }

    /**
     * 构造函数
     *
     * @param error 错误码
     * @param message 错误消息
     * @param params 国际化消息参数
     */
    public BaseException(ErrorCode error, String message, Object... params) {
        super(message);
        this.error = error;
        this.params = params;
    }

    /**
     * 构造函数
     *
     * @param error 错误码
     * @param cause 原始异常
     */
    public BaseException(ErrorCode error, Throwable cause) {
        super(error.getMessage(), cause);
        this.error = error;
        this.params = null;
    }

    /**
     * 构造函数
     *
     * @param error 错误码
     * @param message 错误消息
     * @param cause 原始异常
     */
    public BaseException(ErrorCode error, String message, Throwable cause) {
        super(message, cause);
        this.error = error;
        this.params = null;
    }

    /**
     * 构造函数
     *
     * @param error 错误码
     * @param message 错误消息
     * @param cause 原始异常
     * @param params 国际化消息参数
     */
    public BaseException(ErrorCode error, String message, Throwable cause, Object... params) {
        super(message, cause);
        this.error = error;
        this.params = params;
    }

    /**
     * 获取错误码
     *
     * @return 错误码对象
     */
    public ErrorCode getError() {
        return error;
    }

    /**
     * 获取异常参数数组
     *
     * @return 参数数组
     */
    public Object[] getParams() {
        return params;
    }
}
