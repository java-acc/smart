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

package cn.org.byc.smart.launch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.StringUtils;

@Slf4j
@Configuration(proxyBeanMethods = false)
public class StartEventListener {

    @Async
    @Order
    @EventListener(WebServerInitializedEvent.class)
    public void afterStart(WebServerInitializedEvent event){
        Environment environment = event.getApplicationContext().getEnvironment();
        String appName = environment.getProperty("spring.application.name").toUpperCase();
        int localPort = event.getWebServer().getPort();
        String profile = StringUtils.arrayToCommaDelimitedString(environment.getActiveProfiles());
        log.info("---[{}]---启动完成，当前使用的端口:[{}]，环境变量:[{}]---", appName, localPort, profile);
    }
}
