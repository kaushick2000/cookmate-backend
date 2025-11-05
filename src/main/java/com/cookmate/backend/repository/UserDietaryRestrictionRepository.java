package com.cookmate.backend.repository;

import com.cookmate.backend.entity.User;
import com.cookmate.backend.entity.UserDietaryRestriction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDietaryRestrictionRepository extends JpaRepository<UserDietaryRestriction, Long> {
    List<UserDietaryRestriction> findByUser(User user);
    void deleteByUser(User user);
}
