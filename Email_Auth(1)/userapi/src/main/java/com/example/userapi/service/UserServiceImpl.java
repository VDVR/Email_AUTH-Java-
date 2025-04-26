package com.example.userapi.service;

import com.example.userapi.exception.ResourceNotFoundException;
import com.example.userapi.exception.UserAlreadyExistsException;
import com.example.userapi.model.User;

import java.util.List;
import java.util.Map;

public interface UserServiceImpl {
    List<User> getAllUsers();
    User getUserByEmpId(String empId) throws ResourceNotFoundException;
    User createUser(User user) throws UserAlreadyExistsException;
    User updateUserPartial(String empId, Map<String, Object> updates) throws ResourceNotFoundException;
    void deleteUser(String empId) throws ResourceNotFoundException;
}