package com.developer.app.ws.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.developer.app.ws.exceptions.UserServiceException;
import com.developer.app.ws.io.entity.PasswordResetTokenEntity;
import com.developer.app.ws.io.entity.UserEntity;
import com.developer.app.ws.io.repositories.PasswordResetTokenRepository;
import com.developer.app.ws.io.repositories.UserRepository;
import com.developer.app.ws.service.UserService;
import com.developer.app.ws.shared.AmazonSES;
import com.developer.app.ws.shared.Utils;
import com.developer.app.ws.shared.dto.AddressDto;
import com.developer.app.ws.shared.dto.UserDto;
import com.developer.app.ws.ui.model.response.ErrorMessages;

@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	UserRepository userRepository;

	@Autowired
	Utils utils;
	
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder; 
	
	@Autowired
	PasswordResetTokenRepository passwordResetTokenRepository;
	
	@Override
	public UserDto createUser(UserDto user) {
		if(userRepository.findByEmail(user.getEmail()) != null)
			throw new RuntimeException("Record already exists");
				
		for(int i = 0; i < user.getAddresses().size(); i++) {
			AddressDto address = user.getAddresses().get(i);
			address.setUserDetails(user);
			address.setAddressId(utils.generateAddressId(30));
			user.getAddresses().set(i, address);
		}
		
		
		ModelMapper modelMapper = new ModelMapper();
		UserEntity userEntity = modelMapper.map(user, UserEntity.class);
		
		String publicUserId = utils.generateUserId(30);
		userEntity.setUserId(publicUserId);
		userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		
		userEntity.setEmailVerificationToken(utils.generateEmailVerificationToken(publicUserId));
		userEntity.setEmailVerificationStatus(false);
		
		UserEntity storedUserDetails = userRepository.save(userEntity);	
		
		UserDto returnValue = modelMapper.map(storedUserDetails, UserDto.class);
//		Aqui o beans da erro porque tenho as addresses que tem userdetails etc
//		BeanUtils.copyProperties(storedUserDetails, returnValue);
		
		// Send an email message to user to verify their email address
		new AmazonSES().verifyEmail(returnValue);
				
		return returnValue;
	}
	
	@Override
	public UserDto getUser(String email) {
		UserEntity userEntity = userRepository.findByEmail(email);
		
		if(userEntity == null) throw new UsernameNotFoundException(email);
		
		UserDto returnValue = new UserDto();
		BeanUtils.copyProperties(userEntity, returnValue);
		return returnValue;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		UserEntity userEntity = userRepository.findByEmail(email);
		
		if(userEntity == null) throw new UsernameNotFoundException(email);
		
		return new User(userEntity.getEmail(), 
				userEntity.getEncryptedPassword(), 
				userEntity.getEmailVerificationStatus(), 
				true, true, true, new ArrayList<>());
		//return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new ArrayList<>());
	}

	@Override
	public UserDto getUserById(String userId) {
		UserDto returnValue = new UserDto();
		UserEntity userEntity = userRepository.findByUserId(userId);
		
		if(userEntity == null) throw new UsernameNotFoundException("User with ID: " + userId + " not found.");
		
		BeanUtils.copyProperties(userEntity, returnValue);	
		return returnValue;
	}

	@Override
	public UserDto updateUser(String userId, UserDto user) {
		UserEntity userEntity = userRepository.findByUserId(userId);
		if(userEntity == null)
			throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
		
		userEntity.setFirstName(user.getFirstName());
		userEntity.setLastName(user.getLastName());
		
		UserEntity updatedUserDetails = userRepository.save(userEntity);
//		UserDto returnValue = new ModelMapper().map(updatedUserDetails, UserDto.class);	
		UserDto returnValue = new UserDto();
		BeanUtils.copyProperties(updatedUserDetails, returnValue);
		return returnValue;
	}

	@Override
	public void deleteUser(String userId) {
		UserEntity userEntity = userRepository.findByUserId(userId);
		if(userEntity == null)
			throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
		userRepository.delete(userEntity);
	}

	@Override
	public List<UserDto> getUsers(int page, int limit) {
		List<UserDto> returnValues = new ArrayList<>();
		
		if(page > 0) page = page -1;
		
		Pageable pageableRequest = PageRequest.of(page, limit);
		
		Page<UserEntity> usersPage = userRepository.findAll(pageableRequest);
		List<UserEntity> users = usersPage.getContent();
		
		for(UserEntity userEntity : users) {
			UserDto userDto = new UserDto();
			BeanUtils.copyProperties(userEntity, userDto);
			returnValues.add(userDto);
		}
		return returnValues;
	}

	@Override
	public boolean verifyEmailToken(String token) {
		boolean returnValue = false;
		UserEntity userEntity = userRepository.findUserByEmailVerificationToken(token);
		
		if (userEntity != null) {
			boolean hasTokenExpired = Utils.hasTokenExpired(token);
			if(!hasTokenExpired) {
				userEntity.setEmailVerificationToken(null);
				userEntity.setEmailVerificationStatus(Boolean.TRUE);
				userRepository.save(userEntity);
				returnValue = true;
			}
		}
		return returnValue;
	}

	@Override
	public boolean requestPasswordReset(String email) {
		boolean returnValue = false;
		
		UserEntity userEntity = userRepository.findByEmail(email);
		
		if (userEntity == null) {
			return returnValue;
		}
		
		String token = new Utils().generatePasswordResetToken(userEntity.getUserId());
		
		PasswordResetTokenEntity passwordResetTokenEntity = new PasswordResetTokenEntity();
		passwordResetTokenEntity.setToken(token);
		passwordResetTokenEntity.setUserDetails(userEntity);
		passwordResetTokenRepository.save(passwordResetTokenEntity);
		
		returnValue = new AmazonSES().sendPasswordResetRequest(
				userEntity.getFirstName(), 
				userEntity.getEmail(), token);
		
		return returnValue;
	}

	@Override
	public boolean resetPassword(String token, String password) {
		boolean returnValue = false;
		
		if(Utils.hasTokenExpired(token)) {
			return returnValue;
		}
		
		PasswordResetTokenEntity passwordResetTokenEntity = passwordResetTokenRepository.findByToken(token);
		
		if(passwordResetTokenEntity == null) {
			return returnValue;
		}
		
		// Prepare new Password
		String encondedPassword = bCryptPasswordEncoder.encode(password);
		
		// Update User Password in db
		UserEntity userEntity = passwordResetTokenEntity.getUserDetails();
		userEntity.setEncryptedPassword(encondedPassword);
		UserEntity savedUSerEntity = userRepository.save(userEntity);
		
		// Verify if password was saved successfully
		if(savedUSerEntity != null && savedUSerEntity.getEncryptedPassword().equalsIgnoreCase(encondedPassword)) {
			returnValue = true;
		}
		
		// Remove Password reset token from db
		passwordResetTokenRepository.delete(passwordResetTokenEntity);
		
		return returnValue;
	}

}
