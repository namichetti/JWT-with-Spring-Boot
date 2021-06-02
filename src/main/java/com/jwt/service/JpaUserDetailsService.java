package com.jwt.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.jwt.model.Role;
import com.jwt.model.User;
import com.jwt.repository.IUserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class JpaUserDetailsService implements UserDetailsService{

	@Autowired
	private IUserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = null;
		List<GrantedAuthority> authorities = new ArrayList<>();
		
		try {
			user = this.userRepository.findByUsername(username);
		}catch(UsernameNotFoundException e) {
			log.error(e.getMessage());
			throw new UsernameNotFoundException("Username not found!");
		}

		
		for(Role role: user.getRoles()) {
			authorities.add(new SimpleGrantedAuthority(role.getRole()));
		}	
		
		
		if(authorities.isEmpty()) {
			log.error("User have no roles");
			throw new  AuthorizationServiceException("Username cannot sing in!");
		}
		
		return new org.springframework.security.core.userdetails
				.User(username, user.getPassword(), user.getEnabled(), true, true, true, authorities);
	}

}
