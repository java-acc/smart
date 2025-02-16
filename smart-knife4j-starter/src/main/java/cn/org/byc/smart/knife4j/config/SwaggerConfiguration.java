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

package cn.org.byc.smart.knife4j.config;

import cn.org.byc.smart.knife4j.config.model.SwaggerProperties;
import cn.org.byc.smart.knife4j.constant.Knife4jConstant;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.configuration.SpringDocConfiguration;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
@AllArgsConstructor
@AutoConfigureBefore(SpringDocConfiguration.class)
@EnableConfigurationProperties(SwaggerProperties.class)
@ConditionalOnProperty(value = "swagger.enabled", havingValue = "true", matchIfMissing = true)
public class SwaggerConfiguration {
    private static final String DEFAULT_BASE_PATH = "/**";
    private static final List<String> DEFAULT_EXCLUDE_PATH = Arrays.asList("/error", "/actuator/**");

    /**
     * 引入Swagger配置类
     */
    private final SwaggerProperties swaggerProperties;

    @Bean
    public OpenAPI openApi() {
        // 初始化OpenAPI对象，并设置API的基本信息、安全策略、联系人信息、许可信息以及外部文档链接
        return new OpenAPI()
                .components(new Components()
                        // 添加安全策略，配置API密钥（Token）和鉴权机制
                        .addSecuritySchemes(Knife4jConstant.AUTHORIZATION_HEADER,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.HEADER)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .name(Knife4jConstant.AUTHORIZATION_HEADER)
                        )
                        // 添加安全策略，配置租户ID（Tenant-Id）和鉴权机制
                        .addSecuritySchemes(Knife4jConstant.TENANT_ID_HEADER,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.HEADER)
                                        .name(Knife4jConstant.TENANT_ID_HEADER)
                        )
                )
                // 设置API文档的基本信息，包括标题、描述、联系方式和许可信息
                .info(new Info()
                        .title(this.swaggerProperties.getTitle())
                        .description(this.swaggerProperties.getDescription())
                        .termsOfService(this.swaggerProperties.getTermsOfServiceUrl())
                        .contact(new Contact()
                                .name(this.swaggerProperties.getContact().getName())
                                .email(this.swaggerProperties.getContact().getEmail())
                        )
                        .license(new License()
                                .name(this.swaggerProperties.getLicense())
                                .url(this.swaggerProperties.getLicenseUrl())
                        )
                        .version(this.swaggerProperties.getVersion())
                );
    }

    /**
     * 初始化GlobalOpenApiCustomizer对象
     */
    @Bean
    @ConditionalOnMissingBean
    public GlobalOpenApiCustomizer orderGlobalOpenApiCustomizer() {
        return openApi -> {
            if (openApi.getPaths() != null) {
                openApi.getPaths().forEach((s, pathItem) -> pathItem.readOperations().forEach(operation ->
                        operation.addSecurityItem(new SecurityRequirement()
                                .addList(Knife4jConstant.AUTHORIZATION_HEADER)
                                .addList(Knife4jConstant.TENANT_ID_HEADER))));
            }
        };
    }

    /**
     * 初始化GroupedOpenApi对象
     */
    @Bean
    @ConditionalOnMissingBean
    public GroupedOpenApi defaultApi() {
        // 如果Swagger配置中的基本路径和排除路径为空，则设置默认的基本路径和排除路径
        if (CollectionUtils.isEmpty(this.swaggerProperties.getBasePath())) {
            this.swaggerProperties.getBasePath().add(DEFAULT_BASE_PATH);
        }
        if (CollectionUtils.isEmpty(this.swaggerProperties.getExcludePath())) {
            this.swaggerProperties.getExcludePath().addAll(DEFAULT_EXCLUDE_PATH);
        }
        // 获取Swagger配置中的基本路径、排除路径、基本包路径和排除包路径
        final List<String> basePath = this.swaggerProperties.getBasePath();
        final List<String> excludePath = this.swaggerProperties.getExcludePath();
        final List<String> basePackages = this.swaggerProperties.getBasePackages();
        final List<String> excludePackages = this.swaggerProperties.getExcludePackages();
        // 创建并返回GroupedOpenApi对象
        return GroupedOpenApi.builder()
                .group("default")
                .pathsToMatch(basePath.toArray(new String[0]))
                .pathsToExclude(excludePath.toArray(new String[0]))
                .packagesToScan(basePackages.toArray(new String[0]))
                .packagesToExclude(excludePackages.toArray(new String[0]))
                .build();
    }
}
