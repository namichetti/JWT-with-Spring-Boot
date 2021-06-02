package com.jwt.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jwt.model.User;

public interface IUserRepository extends JpaRepository<User, Long>{

	User findByUsername(String username);
}
