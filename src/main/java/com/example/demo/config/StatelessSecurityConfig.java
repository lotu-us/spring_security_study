package com.example.demo.config;

import com.example.demo.filter.*;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity //웹 시큐리티 활성화 : 스프링 시큐리티 필터가 스프링 기본 필터체인에 등록됨
@RequiredArgsConstructor
public class StatelessSecurityConfig extends WebSecurityConfigurerAdapter {

    private final CorsFilter corsFilter;
    private final UserRepository userRepository;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable();

        //jwt
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        //세션을 사용하지 않겠다

        http.addFilter(corsFilter); //cors 정책에서 벗어남.
        // controller에 @CrossOrigin 어노테이션을 붙여서도 해결할 수 있지만 사용하지 않는 이유는
        // @CrossOrigin을 사용하면 인증이 필요한 요청이 모두 거부된다 = 인증이 필요하지 않은 요청만 허용하게된다

        http.formLogin().disable()  //formlogin 사용안함
            .httpBasic().disable()  //기본적인 http 로그인방식 사용안함
            .addFilter(new JwtAuthenticationFilter(authenticationManager()))   // /login이 동작하게됨
            .addFilter(new JwtAuthorizationFilter(authenticationManager(), userRepository))     //
            .authorizeRequests()
                .antMatchers("/api/v1/user/**").access("hasRole('ROLE_USER') or hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
                .antMatchers("/api/v1/manager/**").access("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
                .antMatchers("/api/v1/admin/**").access("hasRole('ROLE_ADMIN')")
                .anyRequest().permitAll();

        /*
            http only ? : http가 아닌 자바스크립트 같은 곳에서는 쿠키를 건드릴 수 없게 만든다.
            웹브라우저로 www.naver.com을 접속하면 자연스럽게 이동하지만
            자바스크립트의 ajax 등으로 쿠키를 강제로 담아서 요청을 하는 경우에는 서버쪽에서 쿠키를 거부해버린다
            (ex. fetch("http://www.naver.com", {
                    headers:{ Cookie:"" }
                }).then();
            )

            http only를 false로 설정하여 쿠키를 허용해버리면, 자바스크립트에서 장난을 칠 수 있기때문에 보안적으로 안좋다


            httpBasic ?
            http headers의 Authorization키의 값에 ID와 PW를 넣는 방식 { Authorization : ID, PW }
            매 요청마다 ID와 PW를 달고 요청하게된다 => 요청이 올때마다 서버에서 인증과정을 처리해주면되기때문에 쿠키와 세션이 필요없어진다.
            But, ID와 PW가 암호화되지 않기때문에 중간에 노출되면 큰일! 해결책으로 https 를 사용해야한다


            httpBearer ?
            우리는 http headers의 Authorization키의 값에 Token을 넣는 방식을 사용할 것이다. { Authorization : token }
            매 요청마다 토큰을 달고 요청하게된다 => 요청이 올때마다 서버에서 인증과정을 처리해주면되기때문에 쿠키와 세션이 필요없어진다.
            역시 토큰값이 암호화되지 않기때문에 중간에 노출되면 안좋지만, 개인정보가 아니기때문에 httpBasic방식보다는 위험도가 적다

        * */


        //http.addFilterBefore(new JwtFilter(), SecurityContextPersistenceFilter.class);
        // 기본적으로 addFilter로 등록할수있는건 org.springframework.web.filter 패키지에 있는 필터이기때문에 (https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/filter/package-summary.html)
        // 내가 만든 필터는 addFilterAfter나 addFilterBefore로 등록해야한다. 위의 내용은 BasicAuthenticationFilter가 실행되기전에 jwtfilter를 실행한다는 것
        // FilterConfig 참고!!

        http.addFilterBefore(new FilterInSecurityBefore(), BasicAuthenticationFilter.class);
        http.addFilterAfter(new FilterInSecurityAfter(), BasicAuthenticationFilter.class);


        /*
        Filter 순서

        SecurityConfig
            http.addFilterBefore(new FilterInSecurityBefore(), BasicAuthenticationFilter.class);
            http.addFilterAfter(new FilterInSecurityAfter(), BasicAuthenticationFilter.class);
            //시큐리티의 가장 먼저 실행하고 싶다면 SecurityContextPersistenceFilter로 작성하면된다

        FilterConfig (내가 만든)
            FilterRegistrationBean<JwtFilter> filter()  bean.setOrder(0);
            FilterRegistrationBean<FilterInMyConfig> filterInMyConfig()  bean.setOrder(1);

        결과
            FilterInSecurityBefore
            FilterInSecurityAfter
            jwtfilter 실행
            FilterInMyConfig

        SecurityFilter가 모두 실행된 후에
        내 Filter가 실행된다.


        */


    }
}
