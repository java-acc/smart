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

package cn.org.byc.smart.exception.constant;

/**
 * API常量定义接口
 *
 * <p>定义了API相关的常量值，包括：
 * <ul>
 *     <li>HTTP头部字段名</li>
 *     <li>追踪标识符</li>
 * </ul>
 *
 * @author Ken
 */
public interface ApiConstant {

    /**
     * 追踪ID的HTTP头部字段名
     *
     * <p>用于在HTTP请求和响应中传递追踪标识符，
     * 便于跟踪和调试分布式系统中的请求流程。
     *
     * <p>格式：X-Trace-Id
     */
    String TRACE_ID_KEY_HEADER = "X-Trace-Id";
}

