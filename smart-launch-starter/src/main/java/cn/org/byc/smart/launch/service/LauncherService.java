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

package cn.org.byc.smart.launch.service;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.core.Ordered;

/**
 * launcher 扩展 用于一些组件发现
 *
 * @author Ken
 */
public interface LauncherService extends Ordered, Comparable<LauncherService>{

    /**
     * 启动时 处理 SpringApplicationBuilder
     *
     * @param builder SpringApplicationBuilder
     * @param appName SpringApplicationAppName
     * @param profile SpringApplicationProfile
     */
    void launcher(SpringApplicationBuilder builder, String appName, String profile);

    /**
     * 获取排列顺序
     *
     * @return order
     */
    @Override
    default int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    /**
     * 对比排序
     *
     * @param o LauncherService
     * @return compare
     */
    @Override
    default int compareTo(LauncherService o) {
        return Integer.compare(this.getOrder(), o.getOrder());
    }
}
