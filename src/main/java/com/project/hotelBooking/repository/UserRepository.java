package com.project.hotelBooking.repository;

import com.project.hotelBooking.repository.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u from User u")
    List<User> findAllUsers(Pageable path);
    Optional<User> findByUsername(String username);
    Optional<User> findTopByEmail(String email);
    Optional<User> findByHash(String email);
}
