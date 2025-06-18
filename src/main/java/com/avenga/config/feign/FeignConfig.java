package com.avenga.config.feign;

import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Feign configuration class responsible for defining and setting up beans related to Feign client behavior
 */
@Configuration
public class FeignConfig {

    /**
     * Defines and registers the {@link CustomFeignLogger} bean in the Spring application context
     *
     * @return an instance of {@link CustomFeignLogger}.
     */
    @Bean
    public CustomFeignLogger customFeignLogging() {
        return new CustomFeignLogger();
    }

    /**
     * Defines the global {@link Logger.Level} for all Feign clients in the application
     *
     * @return {@link Logger.Level} indicating the desired Feign logging level
     */
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }
}
