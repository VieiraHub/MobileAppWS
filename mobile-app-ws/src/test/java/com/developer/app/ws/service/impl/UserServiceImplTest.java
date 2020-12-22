package com.developer.app.ws.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyString;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.developer.app.ws.io.entity.UserEntity;
import com.developer.app.ws.io.repositories.UserRepository;
import com.developer.app.ws.shared.dto.UserDto;

class UserServiceImplTest {

	@InjectMocks
	UserServiceImpl userService;
	
	@Mock
	UserRepository userRepository;
	
	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	final void testGetUser() {
		UserEntity userEntity = new UserEntity();
		userEntity.setId(1L);
		userEntity.setFirstName("Bruno");
		userEntity.setUserId("awfawf");
		userEntity.setEncryptedPassword("faawfa2324");
		
		when(userRepository.findByEmail(anyString())).thenReturn(userEntity);
		
		UserDto userDto = userService.getUser("test@test.com");
		
		assertNotNull(userDto);
		assertEquals("Bruno", userDto.getFirstName());
		
	}
	
	@Test
	final void testGetUser_UsernameNotFoundException() {
		when(userRepository.findByEmail(anyString())).thenReturn(null);
		
		
		assertThrows(UsernameNotFoundException.class, () -> {
			userService.getUser("test@test.com");	
		});
		
	}

}
