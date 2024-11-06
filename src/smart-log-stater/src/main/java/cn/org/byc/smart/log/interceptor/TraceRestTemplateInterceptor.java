package cn.org.byc.smart.log.interceptor;

import cn.org.byc.smart.log.utils.TraceIdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class TraceRestTemplateInterceptor implements ClientHttpRequestInterceptor {
    private static final Logger log = LoggerFactory.getLogger(TraceRestTemplateInterceptor.class);

    public TraceRestTemplateInterceptor() {
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
        String traceId = TraceIdUtil.generateTraceId();
        HttpHeaders headers = httpRequest.getHeaders();
        headers.set(TraceIdUtil.HEADER_TRACE_ID, traceId);
        return clientHttpRequestExecution.execute(httpRequest, bytes);
    }
}