package cn.org.byc.smart.log.interceptor;

import cn.org.byc.smart.log.utils.TraceIdUtil;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class RabbitMqConfig {

    @Autowired
    public void configureRabbitTemplate(RabbitTemplate rabbitTemplate) {
        // 消息发送时，携带 traceId
        rabbitTemplate.setBeforePublishPostProcessors(new RabbitTemplateSendTraceIdPostProcessor());
    }

    @Autowired
    public void configureSimpleRabbitListenerContainerFactory(SimpleRabbitListenerContainerFactory containerFactory) {
        // 消息消费时，获取 traceId
        containerFactory.setAfterReceivePostProcessors(new RabbitTemplateReceiveTraceIdPostProcessor());
    }

    public static class RabbitTemplateSendTraceIdPostProcessor implements MessagePostProcessor {
        @Override
        public Message postProcessMessage(Message message) throws AmqpException {
            Map<String, Object> headers = message.getMessageProperties().getHeaders();
            String traceId = TraceIdUtil.getTraceId();
            headers.putIfAbsent(TraceIdUtil.HEADER_TRACE_ID, traceId);
            return message;
        }
    }

    public static class RabbitTemplateReceiveTraceIdPostProcessor implements MessagePostProcessor {
        @Override
        public Message postProcessMessage(Message message) throws AmqpException {
            Map<String, Object> headers = message.getMessageProperties().getHeaders();
            String traceIdKey = TraceIdUtil.HEADER_TRACE_ID;
            if (headers.containsKey(traceIdKey)) {
                TraceIdUtil.setTraceId(headers.get(traceIdKey).toString());
            }
            return message;
        }
    }
}