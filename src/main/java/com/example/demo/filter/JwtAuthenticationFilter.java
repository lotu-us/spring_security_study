package com.example.demo.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.demo.auth.PrincipalDetails;
import com.example.demo.domain.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

//스프링 시큐리티에서는 /login요청이 들어오면 UsernamePasswordAuthenticationFilter 동작
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    public JwtAuthenticationFilter(AuthenticationManager authenticationManager){
        this.authenticationManager = authenticationManager;
    }

    // /login 요청 시 동작함
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        System.out.println("JwtAuthenticationFilter attemptAuthentication 로그인 시도중");

        // 1. ID, PW 받아서
        ObjectMapper mapper = new ObjectMapper();
        User user = null;
        try {
            user = mapper.readValue(request.getInputStream(), User.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 2. authenticationManager로 로그인 시도 => PrincipalDetailsService의 loadUserByUsername() 실행
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());

        // 3. PrincipalDetails를 세션에 저장 (권한 관리를 위해)
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        // 4. JWT 발급하여 응답
        return authentication;
    }

    // attemptAuthentication 이후 인증되었다면 실행됨
    // jwt토큰을 만들어서 사용자에게 return하면됨
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

        PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();

        int oneMinute = 1000 * 60;       //1000 = 1초
        int availableTime = oneMinute * 10;    //10분
        String secretKey = "cos";

        // Hash암호방식 (RSA 아님)
        String jwtToken = JWT.create()
                .withSubject("token")    // 토큰이름 입력
                .withExpiresAt(new Date(System.currentTimeMillis() + availableTime)) //토큰이 언제까지 유효할지
                .withClaim("id", principalDetails.getUser().getId())
                .withClaim("username", principalDetails.getUser().getUsername())
                .sign(Algorithm.HMAC512(secretKey));

        response.addHeader("Authorization", "Bearer " + jwtToken);
    }
}
