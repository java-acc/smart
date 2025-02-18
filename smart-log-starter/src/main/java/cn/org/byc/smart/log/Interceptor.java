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

package cn.org.byc.smart.log;

import cn.org.byc.smart.exception.constant.ApiConstant;
import cn.org.byc.smart.tool.id.IdFactory;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @author Ken
 */
@Slf4j
public class Interceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String traceId = request.getHeader(ApiConstant.TRACE_ID_HEADER_KEY);
        final String userId = request.getHeader(ApiConstant.USER_ID_HEADER_KEY);
        final String userName = request.getHeader(ApiConstant.USER_NAME_HEADER_KEY);
        if (StringUtils.isEmpty(traceId)) {
            traceId = String.valueOf(IdFactory.getInstance().getLocalId());
        }
        if (StringUtils.hasText(userId)) {
            MDC.put(ApiConstant.USER_ID_HEADER_KEY, userId);
        }
        if (StringUtils.hasText(userName)) {
            MDC.put(ApiConstant.USER_NAME_HEADER_KEY, userName);
        }
        MDC.put(ApiConstant.TRACE_ID_HEADER_KEY, traceId);
        log.info("[Interceptor]:{}", traceId);
        return true;
    }
}
