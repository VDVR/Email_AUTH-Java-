package com.example.userapi.repository;

import com.example.userapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmpId(String empId);
    Optional<User> findByEmail(String email);
    Optional<User> findByMobileNumber(String mobileNumber);
    boolean existsByEmpId(String empId);
    boolean existsByEmail(String email);
    boolean existsByMobileNumber(String mobileNumber);
    List<User> findAll();
}