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

package cn.org.byc.smart.exception.auth;

import cn.org.byc.smart.exception.BaseException;
import cn.org.byc.smart.exception.enums.CommonError;

/**
 * 认证异常
 *
 * <p>当用户认证失败时抛出此异常，包括但不限于：
 * <ul>
 *     <li>未登录访问需要认证的资源</li>
 *     <li>登录凭证已过期</li>
 *     <li>用户名或密码错误</li>
 * </ul>
 *
 * @author Ken
 */
public class AuthenticationException extends BaseException {

    public AuthenticationException() {
        super(CommonError.AuthenticationFailed);
    }

    public AuthenticationException(String message) {
        super(CommonError.AuthenticationFailed, message);
    }

    public AuthenticationException(String message, Object... params) {
        super(CommonError.AuthenticationFailed, message, params);
    }
}
