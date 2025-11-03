package com.cookmate.backend.repository;

import com.cookmate.backend.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    
    Optional<PasswordResetToken> findByToken(String token);
    
    void deleteByUser_Id(Long userId);
    
    void deleteByExpiryDateBefore(LocalDateTime now);
}