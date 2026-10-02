package com.project.agriculture.repository;

import com.project.agriculture.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Used during login to fetch a user by their email
    Optional<User> findByEmail(String email);

    // Used during registration to check for duplicate emails
    boolean existsByEmail(String email);
}
