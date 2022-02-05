package com.example.demo.config;

import com.example.demo.oauth.PrincipalOauth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

//@Configuration
//@EnableWebSecurity  //웹 시큐리티 활성화 : 스프링 시큐리티 필터가 스프링 기본 필터체인에 등록됨
//@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
// securedEnabled = @Secured어노테이션 활성화. controller의 info참고
// prePostEnabled = @preAuthorize 어노테이션 활성화.
public class StatefulSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired private PrincipalOauth2UserService principalOauth2UserService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();

       //form, oauth2
        http.authorizeRequests()
                .antMatchers("/user/**").authenticated()    // /user 요청에 대해서는 로그인을 요구함
                .antMatchers("/manager/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")  // /manager요청에 대해서는 역할을 가지고 있어야함
                .antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")
                .anyRequest().permitAll()                       //나머지 주소는 인증 필요 없음
                .and()
                .formLogin().loginPage("/loginForm")            // /user, /manager, /admin 에 접속하면 login페이지로 이동
                .loginProcessingUrl("/loginproc")                   // /login 주소가 호출되면 시큐리티가 낚아채서 대신 로그인을 진행해준다
                .defaultSuccessUrl("/")// 로그인이 완료되면 메인페이지로 이동
                .and()
                .oauth2Login()
                .loginPage("/loginForm")
                .userInfoEndpoint()
                .userService(principalOauth2UserService);   //구글 로그인 완료 후 후처리 필요. 액세스토큰과 사용자 프로필 정보를 받아올 수 있음.

    }

}
