package com.developer.app.ws.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.developer.app.ws.shared.dto.UserDto;

public interface UserService extends UserDetailsService {
	UserDto createUser(UserDto user);
	UserDto getUser(String email);
	UserDto getUserById(String id);
	UserDto updateUser(String userId, UserDto user);
}
