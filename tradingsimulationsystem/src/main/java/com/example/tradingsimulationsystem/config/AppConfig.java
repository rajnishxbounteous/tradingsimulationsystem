package com.example.tradingsimulationsystem.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.springframework.context.annotation.Bean;

@Configuration
@EnableScheduling
public class AppConfig {

    /**
     * Enable scheduling for tasks like price simulation.
     */
    // @EnableScheduling annotation activates scheduled tasks in the app

    /**
     * Define a global thread pool executor for concurrent trade processing.
     */
    @Bean
    public Executor tradeExecutor() {
        return Executors.newFixedThreadPool(10);
    }
}
