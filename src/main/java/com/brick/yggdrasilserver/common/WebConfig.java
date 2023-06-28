package com.brick.yggdrasilserver.common;

import com.brick.yggdrasilserver.interceptor.SystemInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(new SystemInterceptor())
                .addPathPatterns("/user/**", "/web/profile/**");
    }
}
