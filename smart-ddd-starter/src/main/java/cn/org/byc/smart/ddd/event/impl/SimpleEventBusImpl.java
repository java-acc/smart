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

package cn.org.byc.smart.ddd.event.impl;

import cn.org.byc.smart.ddd.event.DomainEvent;
import cn.org.byc.smart.ddd.event.DomainEventBus;
import cn.org.byc.smart.ddd.event.DomainEventHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * 简单事件总线实现
 * 
 * <p>基于Kafka的领域事件总线实现，提供：
 * <ul>
 *     <li>事件发布 - 将事件序列化并发送到Kafka</li>
 *     <li>处理器注册 - 支持运行时注册事件处理器</li>
 *     <li>错误重试 - 使用Spring Retry进行失败重试</li>
 *     <li>发送确认 - 等待Kafka确认消息发送成功</li>
 * </ul>
 *
 * @author Ken
 * @see DomainEventBus
 * @see DomainEvent
 */
@Component
public class SimpleEventBusImpl implements DomainEventBus {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleEventBusImpl.class);
    
    /**
     * Kafka生产者，用于发送事件消息
     */
    private final Producer<String, String> eventProducer;
    
    /**
     * JSON序列化工具
     */
    private final ObjectMapper objectMapper;
    
    /**
     * 事件处理器注册表，key为事件名称，value为对应的处理器
     */
    private final Map<String, DomainEventHandler> handlers = new ConcurrentHashMap<>();

    /**
     * 构造函数
     *
     * @param eventProducer Kafka生产者
     * @param objectMapper JSON序列化工具
     */
    public SimpleEventBusImpl(Producer<String, String> eventProducer, ObjectMapper objectMapper) {
        this.eventProducer = eventProducer;
        this.objectMapper = objectMapper;
    }

    /**
     * 发布领域事件
     * 
     * <p>将事件序列化为JSON并发送到Kafka。特点：
     * <ul>
     *     <li>自动重试 - 发送失败时自动重试3次</li>
     *     <li>等待确认 - 等待Kafka确认消息发送成功</li>
     *     <li>异常处理 - 记录详细的错误日志</li>
     * </ul>
     *
     * @param event 要发布的领域事件
     */
    @Override
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public void post(DomainEvent event) {
        if (event == null) {
            LOGGER.warn("Attempted to post null event");
            return;
        }

        try {
            // 序列化事件为JSON
            String message = objectMapper.writeValueAsString(event);
            
            // 创建Kafka消息记录
            ProducerRecord<String, String> record = new ProducerRecord<>(
                event.getEventName(),  // topic
                event.key(),          // key
                message              // value
            );
            
            // 发送消息并等待确认
            Future<RecordMetadata> future = eventProducer.send(record);
            RecordMetadata metadata = future.get(5, TimeUnit.SECONDS);
            
            LOGGER.debug("Event published successfully: type={}, key={}, partition={}, offset={}",
                event.getEventName(), event.key(), metadata.partition(), metadata.offset());
            
        } catch (Exception e) {
            LOGGER.error("Failed to publish event: type={}, key={}", 
                event.getEventName(), event.key(), e);
            throw new RuntimeException("Failed to publish event", e);
        }
    }

    /**
     * 注册事件处理器
     * 
     * <p>将处理器注册到处理器映射表中。特点：
     * <ul>
     *     <li>线程安全 - 使用ConcurrentHashMap保证并发安全</li>
     *     <li>防重复 - 检查处理器是否已注册</li>
     *     <li>日志记录 - 记录注册和覆盖操作</li>
     * </ul>
     *
     * @param handler 要注册的事件处理器
     */
    @Override
    public void register(DomainEventHandler handler) {
        if (handler == null) {
            LOGGER.warn("Attempted to register null handler");
            return;
        }

        // 获取处理器的类名作为事件类型
        String eventType = handler.getClass().getSimpleName()
            .replace("Handler", "")  // 移除Handler后缀
            .toUpperCase();         // 转换为大写
            
        // 检查是否已存在处理器
        DomainEventHandler existing = handlers.put(eventType, handler);
        if (existing != null) {
            LOGGER.warn("Replaced existing handler for event type: {}", eventType);
        } else {
            LOGGER.info("Registered new handler for event type: {}", eventType);
        }
    }
}
