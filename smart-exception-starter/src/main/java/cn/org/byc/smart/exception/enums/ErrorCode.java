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

package cn.org.byc.smart.exception.enums;

/**
 * 错误码接口
 *
 * <p>定义了错误码的基本属性，包括：
 * <ul>
 *     <li>HTTP状态码</li>
 *     <li>业务错误码</li>
 *     <li>错误消息</li>
 * </ul>
 *
 * <p>实现此接口的类需要提供这三个基本属性的实现，通常用于错误码枚举类。
 *
 * @author Ken
 * @see CommonError
 */
public interface ErrorCode {
    /**
     * 获取HTTP状态码
     *
     * @return HTTP状态码
     */
    int getStatus();

    /**
     * 获取业务错误码
     *
     * @return 业务错误码字符串
     */
    String getCode();

    /**
     * 获取错误消息
     *
     * @return 错误消息字符串
     */
    String getMessage();
}
