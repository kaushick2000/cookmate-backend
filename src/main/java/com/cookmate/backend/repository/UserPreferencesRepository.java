package com.cookmate.backend.repository;

import com.cookmate.backend.entity.User;
import com.cookmate.backend.entity.UserPreferences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPreferencesRepository extends JpaRepository<UserPreferences, Long> {
    Optional<UserPreferences> findByUser(User user);
    Optional<UserPreferences> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
    void deleteByUser(User user);
}
