package com.jwt.service;

import java.io.IOException;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JWTServiceImpl {
	
	//@Value("${jwt.token.secret}")
	private String secret="mysecretkeyfortokensmysecretkeyfortokensmysecretkeyfortokens";

	private Key getKey() {
	  byte[] keyBytes = Decoders.BASE64.decode(this.secret);
	  return Keys.hmacShaKeyFor(keyBytes);
	}
	
	

	//Crear
	
	public String createToken(Authentication auth) throws JsonProcessingException {
		String username = auth.getName();
		Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
		Claims claims = Jwts.claims();
		claims.put("authorities", new ObjectMapper().writeValueAsString(authorities));
		String token = Jwts.builder().setSubject(username).setClaims(claims)
				.signWith(getKey())
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() * 1000 * 60 * 60 * 10))
				.compact();
		
		return token;	
	}
		
	//Validar
	public boolean validate(String token) {
		try {
			this.getClaims(token);
			return true;
		}catch(JwtException | IllegalArgumentException  e) {
			System.out.println(e.getMessage() + " " + e.getCause());
			return false;
		}
	}

	//Obtener Claims
	public Claims getClaims(String token){
		Claims claims = Jwts.parserBuilder().setSigningKey(this.getKey()).build().parseClaimsJws(this.resolve(token)).getBody();
		return claims;
	}

	public String resolve(String token) {
		if (token != null && token.startsWith("Bearer ")) {
			return token.replace("Bearer ", "");
		}
		return null;
	}

	
	public String getUsername(String token) {
		return this.getClaims(token).getSubject();
	}

	public Collection<? extends GrantedAuthority> getAuthorities(String token) 
			throws JsonParseException, JsonMappingException, IOException {
		Object roles = this.getClaims(token).get("authorities");
		
		Collection<? extends GrantedAuthority> authorities = Arrays
				.asList(new ObjectMapper().addMixIn(SimpleGrantedAuthority.class, com.jwt.filter.SimpleGrantedAuthorityMixin.class)
			    .readValue(roles.toString().getBytes(),SimpleGrantedAuthority[].class));
		
		return authorities;
	}
	
}
