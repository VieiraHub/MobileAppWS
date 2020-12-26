package com.developer.app.ws.ui.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.developer.app.ws.service.impl.UserServiceImpl;
import com.developer.app.ws.shared.dto.AddressDto;
import com.developer.app.ws.shared.dto.UserDto;
import com.developer.app.ws.ui.model.response.UserRest;

class UserControllerTest {
	
	@InjectMocks
	UserController userController;
	
	@Mock
	UserServiceImpl userService;
	
	UserDto userDto;
	final String USER_ID = "UserId";
	

	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);
		
		userDto = new UserDto();
		userDto.setFirstName("FirstName");
		userDto.setLastName("LastName");
		userDto.setEmail("test@test.com");
		userDto.setEmailVerificationStatus(Boolean.FALSE);
		userDto.setEmailVerificationToken(null);
		userDto.setUserId(USER_ID);
		userDto.setAddresses(getAddressesDto());
		userDto.setEncryptedPassword("EncryptedPassword");
		userDto.setPassword("Password");		
	}

	@Test
	void testGetUser() {
		when(userService.getUserById(anyString())).thenReturn(userDto);
		UserRest userRest = userController.getUser(USER_ID);
		
		assertNotNull(userRest);
		assertEquals(USER_ID, userRest.getUserId());
		assertEquals(userDto.getFirstName(), userRest.getFirstName());
		assertEquals(userDto.getLastName(), userRest.getLastName());
		assertTrue(userDto.getAddresses().size() == userRest.getAddresses().size());		
	}

	private List<AddressDto> getAddressesDto() {
		AddressDto shippingAddressDto = new AddressDto();
		shippingAddressDto.setType("shipping");
		shippingAddressDto.setCity("City");
		shippingAddressDto.setCountry("Country");
		shippingAddressDto.setPostalCode("PostalCode");
		shippingAddressDto.setStreetName("Street Name");
		
		AddressDto billingAddressDto = new AddressDto();
		billingAddressDto.setType("billing");
		billingAddressDto.setCity("City");
		billingAddressDto.setCountry("Country");
		billingAddressDto.setPostalCode("PostalCode");
		billingAddressDto.setStreetName("Street Name");
		
		List<AddressDto> addresses = new ArrayList<>();
		addresses.add(shippingAddressDto);
		addresses.add(billingAddressDto);
		return addresses;
	}
}
