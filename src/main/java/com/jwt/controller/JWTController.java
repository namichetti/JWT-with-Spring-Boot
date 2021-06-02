package com.jwt.controller;

import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JWTController {

	@Secured("ROLE_USER")
	@GetMapping("/")
	public String getHello() {
		return "Hello user";
	}
	
	@Secured("ROLE_ADMIN")
	@GetMapping("/hello")
	public String getChau() {
		return "Hello admin";
	}
}
