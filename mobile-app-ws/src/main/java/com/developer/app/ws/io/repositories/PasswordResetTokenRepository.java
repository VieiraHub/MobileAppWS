package com.developer.app.ws.io.repositories;

import org.springframework.data.repository.CrudRepository;

import com.developer.app.ws.io.entity.PasswordResetTokenEntity;

public interface PasswordResetTokenRepository extends CrudRepository<PasswordResetTokenEntity, Long> {

}
