package com.diplom.demo.Configurations;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/categories/**")
                .addResourceLocations("file:uploads/categories/");
        registry.addResourceHandler("/uploads/Menu/**")
                .addResourceLocations("file:uploads/Menu/");
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}

