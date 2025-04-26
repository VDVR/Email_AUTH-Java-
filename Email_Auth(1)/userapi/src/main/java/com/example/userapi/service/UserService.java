package com.example.userapi.service;

import com.example.userapi.dto.CreateUserRequest;
import com.example.userapi.exception.*;
import com.example.userapi.model.User;
import com.example.userapi.repository.UserRepository;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class UserService implements UserServiceImpl {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Autowired
    public UserService(UserRepository userRepository, 
                     PasswordEncoder passwordEncoder,
                     EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserByEmpId(String empId) throws ResourceNotFoundException {
        return userRepository.findByEmpId(empId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with empId: " + empId));
    }

    @Override
    @Transactional
    public User createUser(User user) throws UserAlreadyExistsException {
        validateUserCreation(user);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        try {
			emailService.sendAccountCreatedEmail(savedUser);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return savedUser;
    }

    @Transactional
    public User createUserWithEmail(@Valid CreateUserRequest request) throws UserAlreadyExistsException {
        validateUserCreation(request);
        
        User user = new User();
        user.setEmpId(request.getEmpId());
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setMobileNumber(request.getMobileNumber());
        user.setMobilePass(request.isMobilePass());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        
        User savedUser = userRepository.save(user);
        try {
			emailService.sendAccountCreatedEmail(savedUser);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return savedUser;
    }

    private void validateUserCreation(User user) throws UserAlreadyExistsException {
        if (userRepository.existsByEmpId(user.getEmpId())) {
            throw new UserAlreadyExistsException("Employee ID already exists");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists");
        }
        if (userRepository.existsByMobileNumber(user.getMobileNumber())) {
            throw new UserAlreadyExistsException("Mobile number already exists");
        }
    }

    private void validateUserCreation(CreateUserRequest request) throws UserAlreadyExistsException {
        if (userRepository.existsByEmpId(request.getEmpId())) {
            throw new UserAlreadyExistsException("Employee ID already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists");
        }
        if (userRepository.existsByMobileNumber(request.getMobileNumber())) {
            throw new UserAlreadyExistsException("Mobile number already exists");
        }
    }

    @Override
    @Transactional
    public User updateUserPartial(String empId, Map<String, Object> updates) throws ResourceNotFoundException {
        User user = getUserByEmpId(empId);
        
        updates.forEach((key, value) -> {
            switch (key) {
                case "name": user.setName((String) value); break;
                case "email": 
                    if (!user.getEmail().equals(value) && userRepository.existsByEmail((String) value)) {
                        throw new UserAlreadyExistsException("Email already exists");
                    }
                    user.setEmail((String) value); 
                    break;
                case "password": 
                    user.setPassword(passwordEncoder.encode((String) value)); 
                    break;
                case "mobileNumber":
                    if (!user.getMobileNumber().equals(value) && 
                        userRepository.existsByMobileNumber((String) value)) {
                        throw new UserAlreadyExistsException("Mobile number already exists");
                    }
                    user.setMobileNumber((String) value);
                    break;
                case "mobilePass":
                    user.setMobilePass(Boolean.parseBoolean(value.toString()));
                    break;
            }
        });
        
        User updatedUser = userRepository.save(user);
        try {
			emailService.sendAccountUpdatedEmail(updatedUser);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return updatedUser;
    }
    

    @Override
    @Transactional
    public void deleteUser(String empId) throws ResourceNotFoundException {
        User user = getUserByEmpId(empId);
        String email = user.getEmail();
        String name = user.getName();
        userRepository.delete(user);
        try {
			emailService.sendAccountDeletedEmail(email, empId, name);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public void sendUserReportEmail(String email) {
        List<User> users = getAllUsers();
        try {
			emailService.sendAllUsersReportEmail(email, users);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public void sendSingleUserReport(String email, String empId) throws ResourceNotFoundException {
        User user = getUserByEmpId(empId);
        try {
			emailService.sendUserReportEmail(email, user);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}

