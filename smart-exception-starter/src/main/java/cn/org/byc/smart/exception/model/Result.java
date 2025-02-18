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

package cn.org.byc.smart.exception.model;

import cn.org.byc.smart.exception.constant.ApiConstant;
import cn.org.byc.smart.exception.enums.ErrorCode;
import org.slf4j.MDC;

import java.io.Serial;
import java.io.Serializable;

import static cn.org.byc.smart.exception.enums.CommonError.NoError;
import static cn.org.byc.smart.exception.enums.CommonError.UnExpected;

/**
 * API响应结果包装类
 *
 * <p>统一的API响应格式，包含：
 * <ul>
 *     <li>状态码 - HTTP状态码</li>
 *     <li>业务码 - 业务错误码</li>
 *     <li>消息 - 响应消息</li>
 *     <li>追踪ID - 请求追踪标识</li>
 *     <li>数据 - 响应数据</li>
 * </ul>
 *
 * <p>使用示例：
 * <pre>
 * // 成功响应，无数据
 * Result&lt;Void&gt; result = Result.success();
 *
 * // 成功响应，带数据
 * Result&lt;User&gt; result = Result.success(user);
 *
 * // 失败响应，使用错误码
 * Result&lt;Void&gt; result = Result.fail(CommonError.NotAuthorized);
 * </pre>
 *
 * @param <T> 响应数据的类型
 * @author Ken
 * @see ErrorCode
 */
public class Result<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * HTTP状态码
     */
    private int status;

    /**
     * 业务错误码
     */
    private String code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 请求追踪ID
     */
    private String traceId = MDC.get(ApiConstant.TRACE_ID_KEY_HEADER);

    /**
     * 响应数据
     */
    private T data;

    /**
     * 私有构造函数，创建成功响应（无数据）
     */
    private Result() {
        this.status = NoError.getStatus();
        this.message = NoError.getMessage();
        this.code = NoError.getCode();
        this.data = null;
    }

    /**
     * 私有构造函数，创建成功响应（带数据）
     *
     * @param data 响应数据
     */
    private Result(T data) {
        this.status = NoError.getStatus();
        this.message = NoError.getMessage();
        this.code = NoError.getCode();
        this.data = data;
    }

    /**
     * 私有构造函数，创建错误响应（无数据）
     *
     * @param error 错误码
     */
    private Result(ErrorCode error) {
        this.status = error.getStatus();
        this.message = error.getMessage();
        this.code = error.getCode();
        this.data = null;
    }

    /**
     * 私有构造函数，创建错误响应（带数据）
     *
     * @param error 错误码
     * @param data 响应数据
     */
    private Result(ErrorCode error, T data) {
        this.status = error.getStatus();
        this.message = error.getMessage();
        this.code = error.getCode();
        this.data = data;
    }

    /**
     * 设置自定义消息
     *
     * @param message 自定义消息
     * @return 当前结果对象
     */
    public Result<T> withMessage(String message) {
        this.message = message;
        return this;
    }

    /**
     * 创建成功响应（无数据）
     *
     * @param <T> 响应数据类型
     * @return 成功响应结果
     */
    public static <T> Result<T> success() {
        return new Result<>();
    }

    /**
     * 创建成功响应（带数据）
     *
     * @param <T> 响应数据类型
     * @param data 响应数据
     * @return 成功响应结果
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(NoError, data);
    }

    /**
     * 创建失败响应（使用默认错误码）
     *
     * @param <T> 响应数据类型
     * @return 失败响应结果
     */
    public static <T> Result<T> fail() {
        return new Result<>(UnExpected);
    }

    /**
     * 创建失败响应（使用指定错误码）
     *
     * @param <T> 响应数据类型
     * @param error 错误码
     * @return 失败响应结果
     */
    public static <T> Result<T> fail(ErrorCode error) {
        return new Result<>(error);
    }

    /**
     * 创建失败响应（使用指定错误码和数据）
     *
     * @param <T> 响应数据类型
     * @param error 错误码
     * @param data 响应数据
     * @return 失败响应结果
     */
    public static <T> Result<T> fail(ErrorCode error, T data) {
        return new Result<>(error, data);
    }

    // Getter和Setter方法
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }
}

