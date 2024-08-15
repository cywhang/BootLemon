package com.blue.util;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        // 요청된 URI(사용자가 접속한 주소)를 가져옴
        String requestURI = request.getRequestURI();
        // 해당 URI를 콘솔에 출력
        System.out.println("[interceptor] : " + requestURI);

        // 현재 세션이 존재하는지 확인. 세션이 없다면 null을 반환
        HttpSession session = request.getSession(false);

        // 세션이 없거나, 세션에 'loginUser'객체가 없으면
        if(session == null || session.getAttribute("loginUser") == null) {
            // 로그인되지 않은 상태의 접근요청 콘솔에 출력
            System.out.println("[미인증 사용자 요청]");
            
            // 로그인페이지로 리다이렉트
            response.sendRedirect("/");
            return false;
        }
        // 로그인이 되어있을때
        return true;
    }
}
