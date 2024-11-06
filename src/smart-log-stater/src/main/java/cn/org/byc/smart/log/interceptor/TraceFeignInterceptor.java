package cn.org.byc.smart.log.interceptor;

import cn.org.byc.smart.log.utils.TraceIdUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TraceFeignInterceptor implements RequestInterceptor {
    private static final Logger log = LoggerFactory.getLogger(TraceFeignInterceptor.class);

    public TraceFeignInterceptor() {
    }

    public void apply(RequestTemplate requestTemplate) {
        String traceId = TraceIdUtil.generateTraceId();
        requestTemplate.header(TraceIdUtil.HEADER_TRACE_ID, traceId);
    }
}