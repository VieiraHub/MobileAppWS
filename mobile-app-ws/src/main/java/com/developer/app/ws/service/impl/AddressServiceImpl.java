package com.developer.app.ws.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.developer.app.ws.io.entity.AddressEntity;
import com.developer.app.ws.io.entity.UserEntity;
import com.developer.app.ws.io.repositories.AddressRepository;
import com.developer.app.ws.io.repositories.UserRepository;
import com.developer.app.ws.service.AddressService;
import com.developer.app.ws.shared.dto.AddressDto;

@Service
public class AddressServiceImpl implements AddressService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	AddressRepository addressRepository;

	@Override
	public List<AddressDto> getAddresses(String userId) {
		
		List<AddressDto> returnValue = new ArrayList<>();
		UserEntity userEntity = userRepository.findByUserId(userId);
		if (userEntity == null)	return returnValue;
		
		Iterable<AddressEntity> addresses = addressRepository.findAllByUserDetails(userEntity);
		for(AddressEntity addEntity : addresses) {
			AddressDto addressDto = new AddressDto();
			BeanUtils.copyProperties(addEntity, addressDto);
			returnValue.add(addressDto);
		}
		return returnValue;
	}
}
