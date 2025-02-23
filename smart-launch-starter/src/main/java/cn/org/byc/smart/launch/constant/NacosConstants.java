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

package cn.org.byc.smart.launch.constant;

public interface NacosConstants {

    /**
     * nacos 地址
     */
    String NACOS_ADDR = "127.0.0.1:8848";

    /**
     * nacos 配置前缀
     */
    String NACOS_CONFIG_PREFIX = "smart";

    /**
     * nacos 组配置后缀
     */
    String NACOS_GROUP_SUFFIX = "-group";

    /**
     * nacos 配置文件类型
     */
    String NACOS_CONFIG_FORMAT = "yaml";

    /**
     * nacos json配置文件类型
     */
    String NACOS_CONFIG_JSON_FORMAT = "json";

    /**
     * nacos 是否刷新
     */
    String NACOS_CONFIG_REFRESH = "true";

    /**
     * nacos 分组
     */
    String NACOS_CONFIG_GROUP = "DEFAULT_GROUP";

    /**
     * 构建服务对应的 dataId
     *
     * @param appName 服务名
     * @param profile 环境变量
     * @return dataId
     */
    static String dataId(String appName, String profile) {
        return dataId(appName, profile, NACOS_CONFIG_FORMAT);
    }

    /**
     * 构建服务对应的 dataId
     *
     * @param appName 服务名
     * @param profile 环境变量
     * @param format  文件类型
     * @return dataId
     */
    static String dataId(String appName, String profile, String format) {
        return appName + "-" + profile + "." + format;
    }

}

