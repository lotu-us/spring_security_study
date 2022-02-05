package com.example.demo.config;

import com.example.demo.filter.FilterInMyConfig;
import com.example.demo.filter.JwtFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class FilterConfig {

//    @Bean
//    public FilterRegistrationBean<JwtFilter> filter(){
//        FilterRegistrationBean<JwtFilter> bean = new FilterRegistrationBean<>(new JwtFilter());
//        bean.addUrlPatterns("/*");     //모든 요청에 대해 필터를 적용한다
//        bean.setOrder(0);               //낮은 번호가 필터 중에서 가장 먼저 실행된다
//        return bean;
//    }
//
//
//
//
//
//
//
//
//
//    @Bean
//    public FilterRegistrationBean<FilterInMyConfig> filterInMyConfig(){
//        FilterRegistrationBean<FilterInMyConfig> bean = new FilterRegistrationBean<>(new FilterInMyConfig());
//        bean.addUrlPatterns("/*");     //모든 요청에 대해 필터를 적용한다
//        bean.setOrder(1);               //낮은 번호가 필터 중에서 가장 먼저 실행된다
//        return bean;
//    }

}
