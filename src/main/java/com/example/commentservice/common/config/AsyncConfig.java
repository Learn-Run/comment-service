package com.example.commentservice.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@EnableAsync
@Configuration
public class AsyncConfig {

    /**
     * 댓글 이벤트 처리를 위한 전용 스레드 풀
     * - 코어 스레드: 2개
     * - 최대 스레드: 5개
     * - 큐 크기: 100개
     * - 스레드 이름: comment-event-
     */
    @Bean(name = "commentEventExecutor")
    public Executor commentEventExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("comment-event-");
        executor.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    /**
     * 외부 API 호출을 위한 전용 스레드 풀
     * - 코어 스레드: 1개
     * - 최대 스레드: 3개
     * - 큐 크기: 50개
     * - 스레드 이름: external-api-
     */
    @Bean(name = "externalApiExecutor")
    public Executor externalApiExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(3);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("external-api-");
        executor.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
} 