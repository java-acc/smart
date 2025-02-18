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

package cn.org.byc.smart.ddd.event;

import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 领域事件监听器应用程序运行器
 * 
 * <p>负责在应用启动时初始化和启动所有领域事件监听器。主要功能：
 * <ul>
 *     <li>自动发现 - 扫描并收集所有的事件处理器</li>
 *     <li>监听器管理 - 为每个事件类型创建对应的监听器</li>
 *     <li>线程管理 - 使用线程池并发处理事件</li>
 *     <li>优雅关闭 - 确保应用关闭时正确清理资源</li>
 * </ul>
 *
 * @author Ken
 * @see DomainEventListener
 * @see DomainEventHandler
 */
@Component
public class DomainEventListenerAppRunner implements ApplicationRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(DomainEventListenerAppRunner.class);
    
    /**
     * 消费者组ID
     */
    private static final String GROUP_ID = "schedule";
    
    /**
     * 关闭超时时间（秒）
     */
    private static final long SHUTDOWN_TIMEOUT_SECONDS = 30;

    /**
     * Spring应用上下文
     */
    @Resource
    private ApplicationContext applicationContext;

    /**
     * Kafka配置属性
     */
    @Resource
    private KafkaProperties kafkaProperties;

    /**
     * 事件处理线程池
     */
    private final ExecutorService executorService;
    
    /**
     * 事件监听器映射表，key为事件类型，value为对应的监听器
     */
    private final Map<Class, DomainEventListener> listeners;

    /**
     * 构造函数
     */
    public DomainEventListenerAppRunner() {
        this.executorService = Executors.newCachedThreadPool();
        this.listeners = new ConcurrentHashMap<>();
    }

    /**
     * 应用启动时运行，初始化所有事件监听器
     *
     * @param args 应用程序参数
     */
    @Override
    public void run(ApplicationArguments args) {
        // 获取Kafka消费者配置
        Map<String, Object> kafkaConsumerConfig = kafkaProperties.buildConsumerProperties(null);
        kafkaConsumerConfig.put("group.id", GROUP_ID);  // 设置消费者组ID
        
        try {
            // 获取所有事件处理器
            Collection<DomainEventHandler> handlers = applicationContext.getBeansOfType(DomainEventHandler.class).values();
            
            // 为每个处理器创建并启动监听器
            for (DomainEventHandler handler : handlers) {
                // 获取事件类型
                Class eventType = extractEventType(handler);
                
                // 创建并注册监听器
                DomainEventListener listener = new DomainEventListener(kafkaConsumerConfig, eventType, handler);
                listeners.put(eventType, listener);
                
                // 启动监听器
                executorService.execute(listener);
                LOGGER.info("Started event listener for type: {}", eventType.getSimpleName());
            }
            
        } catch (Exception e) {
            LOGGER.error("Failed to initialize event listeners", e);
            throw new RuntimeException("Failed to initialize event listeners", e);
        }
    }

    /**
     * 提取事件处理器的事件类型
     *
     * @param handler 事件处理器
     * @return 事件类型
     */
    private Class extractEventType(DomainEventHandler handler) {
        try {
            ParameterizedType parameterizedType = (ParameterizedType) handler.getClass().getGenericSuperclass();
            return (Class) parameterizedType.getActualTypeArguments()[0];
        } catch (Exception e) {
            LOGGER.error("Failed to extract event type from handler: {}", handler.getClass().getName(), e);
            throw new RuntimeException("Failed to extract event type", e);
        }
    }

    /**
     * 应用关闭时清理资源
     */
    @PreDestroy
    public void shutdown() {
        LOGGER.info("Shutting down event listeners...");
        
        try {
            // 关闭所有监听器
            listeners.values().forEach(DomainEventListener::close);
            
            // 关闭线程池
            executorService.shutdown();
            if (!executorService.awaitTermination(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                LOGGER.warn("Event listener executor did not terminate in {} seconds", SHUTDOWN_TIMEOUT_SECONDS);
                executorService.shutdownNow();
            }
            
            LOGGER.info("Event listeners shutdown completed");
            
        } catch (Exception e) {
            LOGGER.error("Error during event listeners shutdown", e);
            executorService.shutdownNow();
        }
    }
}
