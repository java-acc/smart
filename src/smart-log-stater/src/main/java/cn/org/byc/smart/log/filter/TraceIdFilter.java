package cn.org.byc.smart.log.filter;

import cn.hutool.core.lang.UUID;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;

public class TraceIdFilter extends OncePerRequestFilter {

    public static final String TRACE_ID = "x-trace-id";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String traceId = request.getHeader(TRACE_ID);
            if (StringUtils.isEmpty(traceId)) {
                traceId = UUID.randomUUID().toString().replace("-", "");
                request = new TraceIdRequestWrapper(request, traceId);
            }
            response.setHeader(TRACE_ID, traceId);
            MDC.put(TRACE_ID, traceId);
            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }

    private static class TraceIdRequestWrapper extends HttpServletRequestWrapper {
        private final String traceId;

        public TraceIdRequestWrapper(HttpServletRequest request, String traceId) {
            super(request);
            this.traceId = traceId;
        }

        @Override
        public String getHeader(String name) {
            if (TRACE_ID.equalsIgnoreCase(name)) {
                return traceId;
            }
            return super.getHeader(name);
        }

    }
}