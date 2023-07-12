package org.edupoll.config.support;

import java.io.IOException;
import java.util.List;

import org.edupoll.service.JWTService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class JWTAuthenticationFiter extends OncePerRequestFilter {
	private final JWTService jwtService;
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// 사용자가 JWT 토큰을 가지고 왔다면
		String authorization = request.getHeader("Authorization");
		log.info("Authorization header value : {}", authorization);
		
		if(authorization == null) {
			log.info("Did not process authentication request since failed to find authorization header", authorization);
			filterChain.doFilter(request, response);
			return;
		}
		// JWT 유효성 검사해서 통과하면
		try {
			String email = jwtService.verifyToken(authorization);			
			Authentication authentication = new UsernamePasswordAuthenticationToken(email, authorization, List.of(new SimpleGrantedAuthority("ROLE_MEMBER")));
											// principal ==> 인증 주체자 : UserDetails 객체가 보통 설정됨.
												// ㄴ @AuthenticationPrincipal 했을때 나오는 값
											// credentials ==> 인증에 사용됐던 정보
											// authorities ==> 권한 : role 에 따른 차단  
			log.info("{}", authentication);
			SecurityContextHolder.getContext().setAuthentication(authentication);
		} catch (Exception e) {
			// 토큰이 만료됐거나 위조 됐거나 한 상황
			throw new BadCredentialsException("Invalid authentication token");
		}
		
		// 인증통과 상태로 만들어 버리자.
		filterChain.doFilter(request, response);
	}

}
