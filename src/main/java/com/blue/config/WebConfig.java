package com.blue.config;

import com.blue.util.LoginCheckInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {



    // 인스턴스 내부에 저장되어있는 정적인 이미지들의 경로를 지정하는 부분
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        registry.addResourceHandler("/img/**")
                .addResourceLocations("file:///home/ubuntu/fileUpload/img/");
    }


    // 로그인 확인 인터셉터 등록하기
    // order() : 인터셉트의 호출 순서를 지정한다. 낮을 수록 먼저 호출됨.
    // addPathPatterns("/**") : 인터셉터를 적용할 URL 패턴을 지정함.
    // 1개의 "어떠한" 경로에 상관없이 지정하려면 -> /index/*
    // 1개를 넘어서 몇개의 어디든지의 경로에 추가하고 싶으면 -> /index/**
    // excludePathPatterns("/error") : 인터셉터에서 제외한 패턴을 지정함.
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginCheckInterceptor())
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns("/error", "/", "/logout", "/404", "/join_view", "/changePassword", "/find_info", "/checkDuplicate", "/checkPassword", "/create_form",
                                     "/loginProc", "/memberSearch", "/pwdauth", "/kakao", "/naver", "/favicon.ico", "/js", "/css", "/img");
    }
}