package com.developer.app.ws.shared;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UtilsTest {
	
	@Autowired
	Utils utils;

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	final void testGenerateUserId() {
		String userId = utils.generateUserId(30);
		String userId2 = utils.generateUserId(30);
		assertNotNull(userId);
		assertNotNull(userId2);
		assertTrue(userId.length() == 30);
		assertTrue(!userId.equalsIgnoreCase(userId2));
	}

	@Test
	final void testHasTokenNotExpired() {
		String token = utils.generateEmailVerificationToken("UserId");
		assertNotNull(token);
		
		boolean hasTokenExpired = Utils.hasTokenExpired(token);
		assertFalse(hasTokenExpired);
	}

	@Test
	final void testHasTokenExpired() {
		// Para o teste resultar tenho que esperar que o token expire são 10 dias a + de 26/12/2020
		String expiredToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIzTEVTdVBVWFRacldVdzR6S0ZEbXBqU29MYWlaSnUiLCJleHAiOjE2MDk4ODQ2Mzh9.KCJbE5OEozr3dKIIP6yAn4WCVnFuCfa8bxk1rK55Af4c9JvD3OCm9FEJ_4Q_3HawsZg-1lPr8hWKLCqukEKgAQ";
		
		boolean hasTokenExpired = Utils.hasTokenExpired(expiredToken);
		assertTrue(hasTokenExpired);
	}
}
