package com.jwt.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.json.JsonParseException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwt.model.User;
import com.jwt.service.JWTServiceImpl;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter{
	
	private JWTServiceImpl jwtService;
	private AuthenticationManager authenticationManager;

	public JWTAuthenticationFilter(JWTServiceImpl jwtService,
			AuthenticationManager authenticationManager) {
		this.jwtService = jwtService;
		this.authenticationManager = authenticationManager;
		
		//If I want to change the path. It is "/login" by default
		this.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/api/login","POST"));
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		String username = obtainUsername(request);
		String password = obtainPassword(request);
		
		if(username!=null && password!=null) {
			
			logger.error("Username (form-data) is " + username);
			logger.error("Password (form-data) is: " + password);
			
		}else {
			User user = null;
			
			try {
				user = new ObjectMapper().readValue(request.getInputStream(), User.class);
				username = user.getUsername();
				password = user.getPassword();
				
				logger.error("Username (raw) is " + username);
				logger.error("Password (raw) is: " + password);
				
			}catch(IOException | JsonParseException e) {
				logger.error(e.getMessage());
			}
		}
		
		username = username.trim();
		UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
		
		return authenticationManager.authenticate(authRequest);
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		String token = jwtService.createToken(authResult);
		response.addHeader("Authorization", "Bearer " + token);
		
		Map<String, Object> body = new HashMap<String, Object>();
		body.put("token", token);
		body.put("user", authResult.getName());
		body.put("message", "You have logged in succesfully!");
		
		response.getWriter().write(new ObjectMapper().writeValueAsString(body));
		response.setStatus(200);
		response.setContentType("application/json");
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException, ServletException {
		Map<String, Object> body = new HashMap<String, Object>();
		body.put("error", failed.getMessage());
		body.put("message", "Username or password incorrect");
		
		response.getWriter().write(new ObjectMapper().writeValueAsString(body));
		response.setStatus(401);
		response.setContentType("application/json");
	}

	
}
