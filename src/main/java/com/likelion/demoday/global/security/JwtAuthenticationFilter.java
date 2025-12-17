package com.likelion.demoday.global.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    // 게스트 허용 엔드포인트는 JWT 필터를 아예 타지 않게 스킵
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String method = request.getMethod();

        // 인증 관련은 토큰 없이 접근
        if (PATH_MATCHER.match("/api/v1/auth/**", uri)) return true;

        // 펀딩 상세 조회(게스트 허용)
        if (HttpMethod.GET.matches(method) && PATH_MATCHER.match("/api/v1/fundings/**", uri)) return true;

        // 게스트 참여 생성(게스트 허용)
        if (HttpMethod.POST.matches(method) && PATH_MATCHER.match("/api/v1/fundings/*/contributions", uri)) return true;

        // 참여/결제 상태 조회(게스트 허용)
        if (HttpMethod.GET.matches(method) && PATH_MATCHER.match("/api/v1/contributions/**", uri)) return true;

        // 토스 webhook (서버가 받는 거라 보통 permitAll)
        if (HttpMethod.POST.matches(method) && PATH_MATCHER.match("/api/v1/payments/toss/webhook", uri)) return true;

        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = getTokenFromRequest(request);

        // 토큰이 없으면 게스트 요청이므로 그대로 통과
        if (!StringUtils.hasText(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 토큰이 있으면 검증 후 SecurityContext 세팅
            if (tokenProvider.validateToken(token)) {
                Long userId = tokenProvider.getUserIdFromToken(token);

                UserDetails userDetails = userDetailsService.loadUserByUsername(String.valueOf(userId));

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            // 토큰이 "있는데" 문제 있는 경우: 인증 컨텍스트 비우고 통과
            SecurityContextHolder.clearContext();
            log.warn("JWT processing failed: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}
