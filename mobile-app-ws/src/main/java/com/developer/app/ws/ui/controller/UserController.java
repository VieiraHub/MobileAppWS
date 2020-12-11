package com.developer.app.ws.ui.controller;


import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.developer.app.ws.exceptions.UserServiceException;
import com.developer.app.ws.service.AddressService;
import com.developer.app.ws.service.UserService;
import com.developer.app.ws.shared.dto.AddressDto;
import com.developer.app.ws.shared.dto.UserDto;
import com.developer.app.ws.ui.model.request.UserDetailsRequestModel;
import com.developer.app.ws.ui.model.response.AddressesRest;
import com.developer.app.ws.ui.model.response.ErrorMessages;
import com.developer.app.ws.ui.model.response.OperationStatusModel;
import com.developer.app.ws.ui.model.response.RequestOperationName;
import com.developer.app.ws.ui.model.response.RequestOperationStatus;
import com.developer.app.ws.ui.model.response.UserRest;

@RestController
@RequestMapping("users") // http://localhost:8080/users
public class UserController {

	@Autowired
	UserService userService;

	@Autowired
	AddressService addressService;
	

	@GetMapping(path = "/{id}", 
			produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE },
			consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public UserRest getUser(@PathVariable String id) {
		UserDto userDto = userService.getUserById(id);
		UserRest returnValue = new ModelMapper().map(userDto, UserRest.class);
		return returnValue;
	}
	

	@PostMapping(
			produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE },
			consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) throws Exception {
		// TEST
		if (userDetails.getFirstName().isEmpty())
			throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());

		UserDto userDto = new ModelMapper().map(userDetails, UserDto.class);
		UserDto createdUser = userService.createUser(userDto);
		UserRest returnValue = new ModelMapper().map(createdUser, UserRest.class);
		return returnValue;
	}

	@PutMapping(path = "/{id}", 
			produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE },
			consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public UserRest updateUser(@PathVariable String id, @RequestBody UserDetailsRequestModel userDetails) {
		UserDto userDto = new ModelMapper().map(userDetails, UserDto.class);
		UserDto updatedUser = userService.updateUser(id, userDto);
		UserRest returnValue = new ModelMapper().map(updatedUser, UserRest.class);
		return returnValue;
	}

	@DeleteMapping(path = "/{id}",
			produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public OperationStatusModel deleteUser(@PathVariable String id) {
		OperationStatusModel returnValue = new OperationStatusModel();
		returnValue.setOperationName(RequestOperationName.DELETE.name());
		userService.deleteUser(id);
		returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
		return returnValue;
	}

	@GetMapping(produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public List<UserRest> getUsers(@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "limit", defaultValue = "10") int limit) {
		List<UserRest> returnValue = new ArrayList<>();

		List<UserDto> users = userService.getUsers(page, limit);

		java.lang.reflect.Type listType = new TypeToken<List<UserRest>>(){}.getType();
		returnValue = new ModelMapper().map(users, listType);

//		
//		for (UserDto userDto : users) {
//			UserRest userModel = new UserRest();
//			BeanUtils.copyProperties(userDto, userModel);
//			returnValue.add(userModel);
//		}
		return returnValue;
	}

	// http://localhost:8080/mobile-app-ws/users/{id}/addresses
	@GetMapping(path = "/{id}/addresses", 
			produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public List<AddressesRest> getUserAddresses(@PathVariable String id) {
		List<AddressesRest> returnValue = new ArrayList<>();
		List<AddressDto> addressesDto = addressService.getAddresses(id);

		ModelMapper modelMapper = new ModelMapper();
		
		if (addressesDto != null && !addressesDto.isEmpty()) {
			java.lang.reflect.Type listType = new TypeToken<List<AddressesRest>>(){}.getType();
			returnValue = modelMapper.map(addressesDto, listType);
		}
		return returnValue;
	}
	
	@GetMapping(path = "/{id}/addresses/{addressId}", 
			produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public AddressesRest getUserAddress(@PathVariable String addressId) {
		AddressDto addressesDto = addressService.getAddress(addressId);
		ModelMapper modelMapper = new ModelMapper();
		AddressesRest returnValue = modelMapper.map(addressesDto, AddressesRest.class);
		return returnValue;
	}

}
