package com.blue;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        registry.addResourceHandler("/img/**")
                .addResourceLocations("file:///home/ubuntu/fileUpload/img/");
                // 로컬에서 지정하는 파일 경로
                //.addResourceLocations("file:///d:/fileUpload/img/");
    }
}
