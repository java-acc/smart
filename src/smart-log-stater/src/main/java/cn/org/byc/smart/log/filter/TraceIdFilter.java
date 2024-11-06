package cn.org.byc.smart.log.filter;

import cn.hutool.core.lang.UUID;
import cn.org.byc.smart.log.utils.TraceIdUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class TraceIdFilter extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String traceId = request.getHeader(TraceIdUtil.HEADER_TRACE_ID);
            if (StringUtils.isEmpty(traceId)) {
                traceId = TraceIdUtil.generateTraceId();
                request = new TraceIdRequestWrapper(request, traceId);
            }
            response.setHeader(TraceIdUtil.HEADER_TRACE_ID, traceId);
            TraceIdUtil.setTraceId(traceId);
            filterChain.doFilter(request, response);
        } finally {
            TraceIdUtil.clear();
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
            if (TraceIdUtil.HEADER_TRACE_ID.equalsIgnoreCase(name)) {
                return traceId;
            }
            return super.getHeader(name);
        }

    }
}