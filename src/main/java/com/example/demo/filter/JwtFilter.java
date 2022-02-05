package com.example.demo.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class JwtFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        //System.out.println("jwtfilter 실행");

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        //임시토큰 : cos
        String headerAuth = null;
        if(req.getMethod().equals("POST")){
            headerAuth = req.getHeader("Authorization");
            System.out.println("headerAuth : "+headerAuth);

            //임시 토큰이 있을때만 컨트롤러로 진입할 수 있도록
            if(headerAuth.equals("cos")){
                System.out.println("토큰있음");
                chain.doFilter(req, res);

            }else{
                //임시 토큰이 없다면 아예 실행 종료
                res.setCharacterEncoding("UTF-8");
                PrintWriter out = res.getWriter();
                out.println("not 인증안됨");   //view의 화면에 출력
            }
        }

        System.out.println("-----------------------------------");

    }
}
