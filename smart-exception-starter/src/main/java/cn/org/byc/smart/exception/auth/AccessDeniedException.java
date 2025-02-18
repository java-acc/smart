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
 * 访问拒绝异常
 *
 * <p>当用户尝试访问没有权限的资源时抛出此异常。
 * 此异常通常用于以下场景：
 * <ul>
 *     <li>用户访问未被授权的API</li>
 *     <li>用户尝试执行未被授权的操作</li>
 *     <li>用户角色权限不足</li>
 * </ul>
 *
 * @author Ken
 */
public class AccessDeniedException extends BaseException {

    public AccessDeniedException() {
        super(CommonError.AccessDenied);
    }

    public AccessDeniedException(String message) {
        super(CommonError.AccessDenied, message);
    }

    public AccessDeniedException(String message, Object... params) {
        super(CommonError.AccessDenied, message, params);
    }
}
