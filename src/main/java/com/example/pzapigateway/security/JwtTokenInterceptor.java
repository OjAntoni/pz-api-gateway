package com.example.pzapigateway.security;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@AllArgsConstructor
public class JwtTokenInterceptor implements HandlerInterceptor {
    private JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String jwt = jwtTokenProvider.resolveToken(request);

        boolean b = jwt != null && jwtTokenProvider.validateToken(jwt);
        if(b) return true;
        else {
            response.sendError(403);
            return false;
        }
    }
}
