package com.developer.app.ws.io.repository;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.developer.app.ws.io.entity.AddressEntity;
import com.developer.app.ws.io.entity.UserEntity;
import com.developer.app.ws.io.repositories.UserRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserRepositoryTest {

	@Autowired
	UserRepository userRepository;
	
	static boolean recordsCreated = false;
	private String userId = "userId1234";
	
	@BeforeEach
	void setUp() throws Exception {
		if(!recordsCreated) createRecords();	
	}

	@Test
	final void testGetVerifiedUsers() {
		Pageable pageableRequest = PageRequest.of(0, 2);
		Page<UserEntity> pages = userRepository.findAllUsersWithConfirmedEmailAddress(pageableRequest);
		assertNotNull(pages);
		
		List<UserEntity> userEntities = pages.getContent();
		assertNotNull(userEntities);
		assertTrue(userEntities.size() == 1);
	}

	@Test
	final void testFindUserByFirstName() {
		String firstName = "Bruno";
		List<UserEntity> users = userRepository.findUserByFirstName(firstName);
		assertNotNull(users);
		assertTrue(users.size() == 2);
		
		UserEntity user = users.get(0);
		assertTrue(user.getFirstName().equals(firstName));
	}
	
	@Test
	final void testFindUserBylastName() {
		String lastName = "Vieira";
		List<UserEntity> users = userRepository.findUserByLastName(lastName);
		assertNotNull(users);
		assertTrue(users.size() == 2);
		
		UserEntity user = users.get(0);
		assertTrue(user.getLastName().equals(lastName));
	}
	
	@Test
	final void testFindUserByKeyword() {
		String keyword = "Vie";
		List<UserEntity> users = userRepository.findUserByKeyword(keyword);
		assertNotNull(users);
		assertTrue(users.size() == 2);
		
		UserEntity user = users.get(0);
		assertTrue(user.getLastName().contains(keyword) || user.getFirstName().contains(keyword));
	}
	
	@Test
	final void testFindUserFirstNameAndLastNameByKeyword() {
		String keyword = "Vie";
		List<Object[]> users = userRepository.findUserFirstNameAndLastNameByKeyword(keyword);
		assertNotNull(users);
		assertTrue(users.size() == 2);
		
		Object[] user = users.get(0);
		assertTrue(user.length == 2);
		
		String userFirstName = String.valueOf(user[0]);
		String userLastName = String.valueOf(user[1]);
		
		assertNotNull(userFirstName);
		assertNotNull(userLastName);
		
		System.out.println("First name = " + userFirstName);
		System.out.println("Last name = " + userLastName);
	}
	
	@Test
	final void testUpdateUserEmailVerificationStatus() {
		boolean newEmailVerificationStatus = true;
		userRepository.updateUserEmailVerificationStatus(newEmailVerificationStatus, userId);
		
		UserEntity storedUserDetails = userRepository.findByUserId(userId);
		
		boolean storedEmailVerificationStatus = storedUserDetails.getEmailVerificationStatus();
		assertTrue(storedEmailVerificationStatus == newEmailVerificationStatus);
	}
	
	@Test
	final void testFindUserEntityByUserId() {
		
		UserEntity userEntity = userRepository.findUserEntityByUserId(userId);
		
		assertNotNull(userEntity);
		assertTrue(userEntity.getUserId().equals(userId));
		
	}
	
	@Test
	final void testGetUserEntityFullNameById() {
		
		List<Object[]> records = userRepository.getUserEntityFullNameById(userId);
		
		assertNotNull(records);
		assertTrue(records.size() == 1);
		
		Object[] userDetails = records.get(0);
		
		String firstName = String.valueOf(userDetails[0]);
		String lastName = String.valueOf(userDetails[1]);
		assertNotNull(firstName);
		assertNotNull(lastName);
	}
	
	@Test
	final void testUpdateUserEntityEmailVerificationStatus() {
		boolean newEmailVerificationStatus = false;
		userRepository.updateUserEntityEmailVerificationStatus(newEmailVerificationStatus, userId);
		
		UserEntity storedUserDetails = userRepository.findByUserId(userId);
		
		boolean storedEmailVerificationStatus = storedUserDetails.getEmailVerificationStatus();
		assertTrue(storedEmailVerificationStatus == newEmailVerificationStatus);
	}
	
	private void createRecords() {
		UserEntity userEntity = new UserEntity();
		userEntity.setFirstName("Bruno");
		userEntity.setLastName("Vieira");
		userEntity.setUserId(userId);
		userEntity.setEncryptedPassword("encryptedxxx");
		userEntity.setEmail("test@test.com");
		userEntity.setEmailVerificationStatus(true);
		
		AddressEntity addressEntity = new AddressEntity();
		addressEntity.setType("shipping");
		addressEntity.setAddressId("addressId1234");
		addressEntity.setCity("Lisbon");
		addressEntity.setCountry("Portugal");
		addressEntity.setPostalCode("2740-105");
		addressEntity.setStreetName("Street Name Number door");
		
		List<AddressEntity> addresses = new ArrayList<>();
		addresses.add(addressEntity);
		userEntity.setAddresses(addresses);
		
		userRepository.save(userEntity);
		
		UserEntity userEntity2 = new UserEntity();
		userEntity2.setFirstName("Bruno");
		userEntity2.setLastName("Vieira");
		userEntity2.setUserId("4321");
		userEntity2.setEncryptedPassword("encryptedxxx");
		userEntity2.setEmail("test1@test.com");
		userEntity2.setEmailVerificationStatus(true);
		
		AddressEntity addressEntity2 = new AddressEntity();
		addressEntity2.setType("shipping");
		addressEntity2.setAddressId("43215");
		addressEntity2.setCity("Lisbon");
		addressEntity2.setCountry("Portugal");
		addressEntity2.setPostalCode("2740-105");
		addressEntity2.setStreetName("Street Name Number door");
		
		List<AddressEntity> addresses2 = new ArrayList<>();
		addresses2.add(addressEntity2);
		userEntity2.setAddresses(addresses2);
		
		userRepository.save(userEntity2);
		
		recordsCreated = true;
	}
}
