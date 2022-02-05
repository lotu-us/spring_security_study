package com.example.demo.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.demo.auth.PrincipalDetails;
import com.example.demo.domain.User;
import com.example.demo.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// 시큐리티의 BasicAuthenticationFilter는 권한이나 인증이 필요한 특정 주소를 요청했을 때 실행된다
// 권한이나 인증이 필요한 주소가 아니라면 생략됨
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private UserRepository userRepository;
    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository) {
        super(authenticationManager);
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.out.println("인증이나 권한이 필요한 주소");
        //super.doFilterInternal(request, response, chain);

        String jwtHeader = request.getHeader("Authorization");

        // header가 있는지 확인
        if(jwtHeader == null || !jwtHeader.startsWith("Bearer")){
            chain.doFilter(request, response);
            return;
        }

        //jwt검증
        String jwtToken = request.getHeader("Authorization").replace("Bearer ", "");
        String username = JWT.require(Algorithm.HMAC512("cos")).build().verify(jwtToken).getClaim("username").asString();

        //서명이 정상적으로 되었다
        if(username != null){
            User byUsername = userRepository.findByUsername(username);
            PrincipalDetails principalDetails = new PrincipalDetails(byUsername);

            //jwt 토큰 서명이 정상이면 Authentication 객체를 만들어준다
            Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());

            //세션 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);

            chain.doFilter(request, response);
        }
    }
}
