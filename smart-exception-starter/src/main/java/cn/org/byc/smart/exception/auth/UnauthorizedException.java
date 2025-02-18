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
 * 未授权异常
 *
 * <p>当用户未登录或登录已过期时抛出此异常，表示用户需要进行身份认证。
 * 此异常通常用于以下场景：
 * <ul>
 *     <li>访问需要登录的资源时未提供认证信息</li>
 *     <li>提供的认证信息已过期</li>
 *     <li>认证信息无效</li>
 * </ul>
 *
 * @author Ken
 */
public class UnauthorizedException extends BaseException {

    public UnauthorizedException() {
        super(CommonError.Unauthorized);
    }

    public UnauthorizedException(String message) {
        super(CommonError.Unauthorized, message);
    }

    public UnauthorizedException(String message, Object... params) {
        super(CommonError.Unauthorized, message, params);
    }
}