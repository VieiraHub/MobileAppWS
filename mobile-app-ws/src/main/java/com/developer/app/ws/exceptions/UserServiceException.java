package com.developer.app.ws.exceptions;

public class UserServiceException extends RuntimeException {

	private static final long serialVersionUID = -4952023227914156040L;

	public UserServiceException(String message) {
		super(message);
	}
}
