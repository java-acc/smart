package cn.org.byc.smart.log.utils;

import org.slf4j.MDC;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class TraceIdUtil {

    public static final String KEY_TRACE_ID = "traceId";

    public static final String HEADER_TRACE_ID = "x-trace-id";

    private static ApplicationContext applicationContext;

    public static String generateTraceId() {
        return SpringContextUtil.getBean(SnowFlake.class).nextId().toString();
    }

    public static String getTraceId() {
        return MDC.get(KEY_TRACE_ID);
    }

    public static void setTraceId(String traceId) {
        MDC.put(KEY_TRACE_ID, traceId);
    }

    public static void clear() {
        MDC.clear();
    }
}