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

package cn.org.byc.smart.ddd.config;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;

import java.util.Map;

/**
 * Kafka配置类
 *
 * <p>提供Kafka生产者的自动配置。主要功能：
 * <ul>
 *     <li>创建事件生产者 - 用于发送领域事件</li>
 *     <li>配置生产者属性 - 设置重试、幂等性等</li>
 *     <li>条件装配 - 仅在需要时创建Bean</li>
 * </ul>
 *
 * <p>配置条件：
 * <ul>
 *     <li>需要存在KafkaProperties类</li>
 *     <li>容器中不存在名为eventProducer的Bean</li>
 * </ul>
 *
 * @author Ken
 * @see KafkaProducer
 * @see KafkaProperties
 */
@AutoConfiguration
@ConditionalOnClass(KafkaProperties.class)
public class KafkaConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConfiguration.class);

    /**
     * 创建Kafka事件生产者
     *
     * <p>配置生产者以确保可靠的事件发送：
     * <ul>
     *     <li>启用幂等性 - 防止重复发送</li>
     *     <li>设置重试策略 - 自动重试失败的发送</li>
     *     <li>配置确认机制 - 等待所有副本确认</li>
     * </ul>
     *
     * @param kafkaProperties Kafka配置属性
     * @return 配置好的Kafka生产者
     */
    @Bean("eventProducer")
    @ConditionalOnMissingBean(name = "eventProducer")
    public KafkaProducer<String, String> eventProducer(KafkaProperties kafkaProperties) {
        // 获取基础配置
        Map<String, Object> configs = kafkaProperties.buildProducerProperties(null);

        // 配置生产者属性
        configs.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);  // 启用幂等性
        configs.put(ProducerConfig.ACKS_CONFIG, "all");              // 等待所有副本确认
        configs.put(ProducerConfig.RETRIES_CONFIG, 3);              // 最大重试次数
        configs.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 1);  // 限制单连接请求数

        // 创建生产者实例
        KafkaProducer<String, String> producer = new KafkaProducer<>(configs);
        LOGGER.info("Created Kafka event producer with idempotence enabled");

        return producer;
    }
}
