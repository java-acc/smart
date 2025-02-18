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

import cn.hutool.extra.spring.SpringUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 领域事件监听器
 * 
 * <p>负责从Kafka消费领域事件并分发给对应的处理器。主要功能：
 * <ul>
 *     <li>事件消费 - 从Kafka主题订阅和消费事件</li>
 *     <li>事件分发 - 将事件分发给对应的处理器</li>
 *     <li>错误处理 - 将处理失败的事件发送到死信队列</li>
 *     <li>优雅关闭 - 支持安全的关闭和资源释放</li>
 * </ul>
 *
 * @author Ken
 * @see DomainEventHandler
 * @see DomainEvent
 */
public class DomainEventListener implements Runnable, AutoCloseable {
    private static final Logger LOGGER = LoggerFactory.getLogger(DomainEventListener.class);
    
    /**
     * 轮询超时时间（毫秒）
     */
    private static final long POLL_TIMEOUT_MS = 100;
    
    /**
     * 死信队列主题名称
     */
    private static final String DEAD_LETTER_QUEUE_TOPIC = "event.dlq-topic";

    /**
     * JSON序列化工具
     */
    private final ObjectMapper objectMapper;
    
    /**
     * Kafka消费者
     */
    private final KafkaConsumer<String, String> kafkaConsumer;
    
    /**
     * Kafka生产者（用于死信队列）
     */
    private final KafkaProducer<String, String> kafkaProducer;
    
    /**
     * 关闭标志
     */
    private final AtomicBoolean closed = new AtomicBoolean(false);
    
    /**
     * 事件处理器
     */
    private final DomainEventHandler handler;
    
    /**
     * 事件类型
     */
    private final Class eventType;

    /**
     * 构造函数
     *
     * @param kafkaConsumerConfig Kafka消费者配置
     * @param eventType 事件类型
     * @param handler 事件处理器
     */
    public DomainEventListener(Map<String, Object> kafkaConsumerConfig, Class eventType, DomainEventHandler handler) {
        if (kafkaConsumerConfig == null || eventType == null || handler == null) {
            throw new IllegalArgumentException("All parameters must not be null");
        }
        
        this.kafkaConsumer = new KafkaConsumer<>(kafkaConsumerConfig);
        this.eventType = eventType;
        this.handler = handler;
        this.kafkaProducer = SpringUtil.getBean("eventProducer");
        this.objectMapper = SpringUtil.getBean(ObjectMapper.class);
    }

    @Override
    public void run() {
        try {
            // 订阅事件主题
            String topic = eventType.getSimpleName();
            kafkaConsumer.subscribe(Arrays.asList(topic));
            LOGGER.info("Started listening for events of type: {}", topic);

            while (!closed.get()) {
                try {
                    // 轮询消息
                    ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofMillis(POLL_TIMEOUT_MS));
                    
                    // 处理每条消息
                    for (ConsumerRecord<String, String> record : records) {
                        processRecord(record);
                    }
                    
                    // 异步提交偏移量
                    kafkaConsumer.commitAsync((offsets, exception) -> {
                        if (exception != null) {
                            LOGGER.error("Failed to commit offsets", exception);
                        }
                    });
                    
                } catch (Exception e) {
                    LOGGER.error("Error processing messages for topic: {}", topic, e);
                }
            }
        } finally {
            closeResources();
        }
    }

    /**
     * 处理单条消息记录
     *
     * @param record Kafka消息记录
     */
    private void processRecord(ConsumerRecord<String, String> record) {
        LOGGER.debug("Processing message: key={}, value={}", record.key(), record.value());
        
        try {
            // 反序列化事件
            DomainEvent event = (DomainEvent) objectMapper.readValue(record.value(), eventType);
            
            // 调用处理器处理事件
            handler.onApplicationEvent(event);
            
            LOGGER.debug("Successfully processed event: type={}, key={}", 
                event.getEventName(), event.key());
                
        } catch (Exception e) {
            LOGGER.error("Failed to process message: key={}", record.key(), e);
            sendToDeadLetterQueue(record, e);
        }
    }

    @Override
    public void close() {
        if (closed.compareAndSet(false, true)) {
            closeResources();
        }
    }

    /**
     * 关闭资源
     */
    private void closeResources() {
        if (kafkaConsumer != null) {
            try {
                kafkaConsumer.close();
                LOGGER.info("Kafka consumer closed for event type: {}", eventType.getSimpleName());
            } catch (Exception e) {
                LOGGER.error("Error closing Kafka consumer for event type: {}", 
                    eventType.getSimpleName(), e);
            }
        }
    }

    /**
     * 发送消息到死信队列
     *
     * @param record 原始消息记录
     * @param error 处理错误
     */
    private void sendToDeadLetterQueue(ConsumerRecord<String, String> record, Exception error) {
        String failedMessage = String.format(
            "Failed to process event: %s, Error: %s", 
            record.value(), 
            error.getMessage()
        );
        
        ProducerRecord<String, String> dlqRecord = new ProducerRecord<>(
            DEAD_LETTER_QUEUE_TOPIC,
            record.key(),
            failedMessage
        );
        
        kafkaProducer.send(dlqRecord, (metadata, exception) -> {
            if (exception != null) {
                LOGGER.error("Failed to send message to DLQ", exception);
            } else {
                LOGGER.info("Message sent to DLQ: topic={}, partition={}, offset={}", 
                    metadata.topic(), metadata.partition(), metadata.offset());
            }
        });
    }
}
