package cn.org.byc.smart.log.interceptor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class TraceRestTemplateBeanPostProcessor implements BeanPostProcessor {
    @Autowired
    private TraceRestTemplateInterceptor traceRestTemplateInterceptor;

    public TraceRestTemplateBeanPostProcessor() {
    }

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof RestTemplate) {
            RestTemplate restTemplate = (RestTemplate)bean;
            restTemplate.getInterceptors().add(this.traceRestTemplateInterceptor);
        }

        return bean;
    }
}
