package com.reminder.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;

@Configuration
public class PathMatcherConfig {
    @Bean
    public AntPathMatcher matcher(){
        return new AntPathMatcher();
    }
}
