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

package cn.org.byc.smart.knife4j.config.model;

import cn.org.byc.smart.knife4j.constant.Knife4jConstant;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@ConfigurationProperties(prefix = "smart.swagger")
public class SwaggerProperties {
    /**
     * 是否开启swagger
     */
    private boolean enabled = true;
    /**
     * swagger会解析的包路径
     **/
    private List<String> basePackages = new ArrayList<>(Collections.singletonList(Knife4jConstant.BASE_PACKAGES));
    /**
     * swagger会排除解析的包路径
     **/
    private List<String> excludePackages = new ArrayList<>();
    /**
     * swagger会解析的url规则
     **/
    private List<String> basePath = new ArrayList<>();
    /**
     * 在basePath基础上需要排除的url规则
     **/
    private List<String> excludePath = new ArrayList<>();
    /**
     * 标题
     **/
    private String title = "接口文档";
    /**
     * 描述
     **/
    private String description = "接口文档";
    /**
     * 版本
     **/
    private String version = Knife4jConstant.APPLICATION_VERSION;
    /**
     * 许可证
     **/
    private String license = "Powered By SpringBlade";
    /**
     * 许可证URL
     **/
    private String licenseUrl = "";
    /**
     * 服务条款URL
     **/
    private String termsOfServiceUrl = "";
    /**
     * host信息
     **/
    private String host = "";
    /**
     * 联系人信息
     */
    private Contact contact = new Contact();
    /**
     * 全局统一鉴权配置
     **/
    private Authorization authorization = new Authorization();

    @Data
    @NoArgsConstructor
    public static class Contact {

        /**
         * 联系人
         **/
        private String name = "Ken";
        /**
         * 联系人email
         **/
        private String email = "kan.zhang-cn@hotmail.com";

    }

    @Data
    @NoArgsConstructor
    public static class Authorization {

        /**
         * 鉴权策略ID，需要和SecurityReferences ID保持一致
         */
        private String name = "";

        /**
         * 需要开启鉴权URL的正则
         */
        private String authRegex = "^.*$";

        /**
         * 接口匹配地址
         */
        private List<String> tokenUrlList = new ArrayList<>();
    }
}
