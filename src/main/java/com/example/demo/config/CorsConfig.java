package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter(){
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);   //내 서버가 응답할 때 json을 자바스크립트에서 처리할 수 있게 할지 설정. false이면 자바스크립트에서 서버로 요청했을떄 응답해줄수 없다
        config.addAllowedOrigin("*");       //모든 ip의 응답을 허용하겠다
        config.addAllowedHeader("*");       //모든 헤더의 응답을 허용하겠다
        config.addAllowedMethod("*");       //모든 http메서드(get, post, put, delete, patch)의 응답을 허용하겠다

        source.registerCorsConfiguration("/api/**", config);
        return new CorsFilter(source);
    }
}
