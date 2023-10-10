package com.sandu.roomrental.repository;

import com.sandu.roomrental.model.ConfirmationToken;
import com.sandu.roomrental.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConfirmationTokenRepository extends JpaRepository <ConfirmationToken, Long> {
    Optional<ConfirmationToken> findByConfirmationTokenAndUser(Integer confirmationToken, User user);
    Optional<ConfirmationToken> findByUser(User user);
}
