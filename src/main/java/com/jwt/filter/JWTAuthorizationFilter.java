package com.jwt.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.jwt.service.JWTServiceImpl;

@WebFilter(urlPatterns = "/chau")
public class JWTAuthorizationFilter extends BasicAuthenticationFilter{
	
	private JWTServiceImpl jwtService;

	public JWTAuthorizationFilter(AuthenticationManager authenticationManager,JWTServiceImpl jwtService) {
		super(authenticationManager);
		this.jwtService = jwtService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		String header = request.getHeader("Authorization");
		UsernamePasswordAuthenticationToken authRequest = null;

		if(!this.requireAtuhentication(header)) {
			chain.doFilter(request, response);
			return;
		}

		if(this.jwtService.validate(header)) {
			authRequest = new UsernamePasswordAuthenticationToken(this.jwtService.getUsername(header), null, this.jwtService.getAuthorities(header));
		}	

		SecurityContextHolder.getContext().setAuthentication(authRequest);
		chain.doFilter(request, response);
	}

	public boolean requireAtuhentication(String header) {
		if(header == null || !header.startsWith("Bearer ")) {
			return false;
		}
		return true;
	}
	
	

}
