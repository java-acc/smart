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

public interface AppConstants {

    /**
     * 应用版本
     */
    String APPLICATION_VERSION = "0.0.1";

    /**
     * 业务包
     */
    String BASE_PACKAGES = "cn.org.byc.biz";

    /**
     * smart包
     */
    String SMART_PACKAGES = "cn.org.byc.smart";

    /**
     * 应用名前缀
     */
    String APPLICATION_NAME_PREFIX = "smart-";
    /**
     * 网关模块名称
     */
    String APPLICATION_GATEWAY_NAME = APPLICATION_NAME_PREFIX + "gateway";
    /**
     * 授权模块名称
     */
    String APPLICATION_AUTH_NAME = APPLICATION_NAME_PREFIX + "auth";
    /**
     * 监控模块名称
     */
    String APPLICATION_ADMIN_NAME = APPLICATION_NAME_PREFIX + "admin";
    /**
     * 系统模块名称
     */
    String APPLICATION_SYSTEM_NAME = APPLICATION_NAME_PREFIX + "system";
    /**
     * 用户模块名称
     */
    String APPLICATION_USER_NAME = APPLICATION_NAME_PREFIX + "user";
    /**
     * 日志模块名称
     */
    String APPLICATION_LOG_NAME = APPLICATION_NAME_PREFIX + "log";
    /**
     * 开发模块名称
     */
    String APPLICATION_DEVELOP_NAME = APPLICATION_NAME_PREFIX + "develop";
    /**
     * 资源模块名称
     */
    String APPLICATION_RESOURCE_NAME = APPLICATION_NAME_PREFIX + "resource";
    /**
     * 报表系统名称
     */
    String APPLICATION_REPORT_NAME = APPLICATION_NAME_PREFIX + "report";
    /**
     * 测试模块名称
     */
    String APPLICATION_TEST_NAME = APPLICATION_NAME_PREFIX + "test";

    /**
     * 开发环境
     */
    String DEV_CODE = "dev";
    /**
     * 生产环境
     */
    String PROD_CODE = "prod";
    /**
     * 测试环境
     */
    String TEST_CODE = "test";

    /**
     * 代码部署于 linux 上，工作默认为 mac 和 Windows
     */
    String OS_NAME_LINUX = "LINUX";

}
