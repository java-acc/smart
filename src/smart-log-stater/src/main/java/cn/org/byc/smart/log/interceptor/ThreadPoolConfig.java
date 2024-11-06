package cn.org.byc.smart.log.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
@Slf4j
public class ThreadPoolConfig {

    @Bean(value = "threadPoolExecutor")
    public ThreadPoolTaskExecutor threadPoolExecutor() {
        log.info("start threadPoolExecutor");
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor() {
            /**
             * 所有线程都会委托给这个execute方法，在这个方法中我们把父线程的MDC内容赋值给子线程
             * https://logback.qos.ch/manual/mdc.html#managedThreads
             *
             * @param runnable runnable
             */
            @Override
            public void execute(Runnable runnable) {
                // 获取父线程MDC中的内容，必须在run方法之前，否则等异步线程执行的时候有可能MDC里面的值已经被清空了，这个时候就会返回null
                Map<String, String> context = MDC.getCopyOfContextMap();
                super.execute(() -> {
                    // 将父线程的MDC内容传给子线程
                    if (context != null) {
                        MDC.setContextMap(context);
                    }
                    try {
                        // 执行异步操作
                        runnable.run();
                    } finally {
                        // 清空MDC内容
                        MDC.clear();
                    }
                });
            }

            @Override
            public <T> Future<T> submit(Callable<T> task) {
                // 获取父线程MDC中的内容，必须在run方法之前，否则等异步线程执行的时候有可能MDC里面的值已经被清空了，这个时候就会返回null
                Map<String, String> context = MDC.getCopyOfContextMap();
                return super.submit(() -> {
                    // 将父线程的MDC内容传给子线程
                    if (context != null) {
                        MDC.setContextMap(context);
                    }
                    try {
                        // 执行异步操作
                        return task.call();
                    } finally {
                        // 清空MDC内容
                        MDC.clear();
                    }
                });
            }
        };
        executor.setCorePoolSize(15);
        // 配置最大线程数
        executor.setMaxPoolSize(100);
        // 空线程回收时间15s
        executor.setKeepAliveSeconds(15);
        Executors.defaultThreadFactory();
        // 配置队列大小
        executor.setQueueCapacity(3000);
        // 配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("async-order-service-");
        // rejection-policy：当pool已经达到max size的时候，如何处理新任务
        // CALLER_RUNS：不在新线程中执行任务，而是由调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 执行初始化
        executor.initialize();
        return executor;
    }
}