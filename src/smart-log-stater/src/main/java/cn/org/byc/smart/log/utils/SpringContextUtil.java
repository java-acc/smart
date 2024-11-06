package cn.org.byc.smart.log.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringContextUtil implements ApplicationContextAware {

    // Spring应用上下文
    private static ApplicationContext applicationContext;

    // 当Spring容器创建该类的实例时，会自动调用此方法，注入应用上下文
    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        SpringContextUtil.applicationContext = context;
    }

    // 提供一个静态方法，返回应用上下文
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    // 提供一个获取Bean的静态方法
    public static <T> T getBean(Class<T> beanClass) {
        if (applicationContext != null) {
            return applicationContext.getBean(beanClass);
        } else {
            throw new IllegalStateException("ApplicationContext is not initialized yet!");
        }
    }
}