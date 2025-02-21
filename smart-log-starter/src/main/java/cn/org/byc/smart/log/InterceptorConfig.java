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

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 日志拦截器配置类
 * 
 * <p>该配置类用于注册日志拦截器，实现Web请求的日志记录功能。
 * 只有当{@link Interceptor}类存在于类路径时才会启用此配置。
 *
 * <p>使用示例:
 * <pre>{@code
 * @SpringBootApplication
 * public class Application {
 *     public static void main(String[] args) {
 *         SpringApplication.run(Application.class, args);
 *     }
 * }
 * }</pre>
 *
 * @author Ken
 * @see Interceptor
 * @see WebMvcConfigurer
 */
@Configuration
@ConditionalOnClass(Interceptor.class)
public class InterceptorConfig implements WebMvcConfigurer {

    private final Interceptor interceptor;

    /**
     * 构造函数，通过依赖注入获取拦截器实例
     *
     * @param interceptor 日志拦截器实例
     */
    public InterceptorConfig(Interceptor interceptor) {
        this.interceptor = interceptor;
    }

    /**
     * 创建并配置日志拦截器Bean
     *
     * @return 返回新创建的日志拦截器实例
     */
    @Bean
    public Interceptor interceptor() {
        return new Interceptor();
    }

    /**
     * 添加拦截器到拦截器注册表中
     * 
     * <p>该方法实现了{@link WebMvcConfigurer}接口的addInterceptors方法，
     * 用于将日志拦截器注册到Spring MVC的拦截器链中。
     *
     * @param registry 拦截器注册表
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor);
    }
}
