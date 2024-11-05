package cn.org.byc.smart.log.utils;

import cn.hutool.core.lang.UUID;
import org.slf4j.MDC;

public class TraceIdUtil {

    public static final String KEY_TRACE_ID = "traceId";

    public static final String HEADER_TRACE_ID = "x-trace-id";

    public static String generateTraceId() {
        return UUID.randomUUID().toString().replace("-", "");
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