package com.example.userapi.controller;

import com.example.userapi.dto.CreateUserRequest;
import com.example.userapi.dto.ErrorResponse;
import com.example.userapi.exception.*;
import com.example.userapi.model.User;
import com.example.userapi.service.*;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final ExcelExportService excelExportService;

    public UserController(UserService userService, ExcelExportService excelExportService) {
        this.userService = userService;
        this.excelExportService = excelExportService;
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
           
            SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept(
                "empId", "name", "email", "mobileNumber", "mobilePass", "createdAt"
            );
            
            FilterProvider filters = new SimpleFilterProvider()
                .addFilter("userFilter", filter);
            
            MappingJacksonValue mapping = new MappingJacksonValue(users);
            mapping.setFilters(filters);
            
            return ResponseEntity.ok(mapping);
//            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch users");
        }
    }

    @GetMapping("/get")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> getUserByEmpId(@RequestParam String empId) {
        try {
            User user = userService.getUserByEmpId(empId);
            return ResponseEntity.ok(user);
        } catch (ResourceNotFoundException e) {
            return errorResponse(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            return errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch user");
        }
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createUser(
//            @RequestParam String empId,
            @Valid @RequestBody User user,
            BindingResult bindingResult) {
        
//        if (!empId.equals(user.getEmpId())) {
//            return errorResponse(HttpStatus.BAD_REQUEST, 
//                  "empId mismatch between URL and body");
//        }

        if (bindingResult.hasErrors()) {
            return validationErrorResponse(bindingResult);
        }

        try {
            User createdUser = userService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (UserAlreadyExistsException e) {
            return errorResponse(HttpStatus.CONFLICT, e.getMessage());
        } catch (Exception e) {
            return errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, 
                  "Failed to create user");
        }
    }
    
    @PostMapping("/emailOnUserCreation")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createUserWithEmail(
//            @RequestParam(name = "empId") String empId,  
            @Valid @RequestBody CreateUserRequest request,
            BindingResult bindingResult) {

//        logger.debug("Received empId: {}, Request: {}", empId, request);

        if (bindingResult.hasErrors()) {
            logger.error("Validation errors: {}", bindingResult.getAllErrors());
            return validationErrorResponse(bindingResult);
        }
//        if (!empId.equals(request.getEmpId())) {
//            logger.error("empId mismatch - URL: {}, Body: {}", empId, request.getEmpId());
//            return ResponseEntity.badRequest()
//                   .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), 
//                         "empId mismatch between URL and body"));
//        }

        try {
            User createdUser = userService.createUserWithEmail(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (UserAlreadyExistsException e) {
            logger.error("User exists: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                   .body(new ErrorResponse(HttpStatus.CONFLICT.value(), e.getMessage()));
        } catch (Exception e) {
            logger.error("Creation failed", e);
            return ResponseEntity.internalServerError()
                   .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                         "Failed to create user: " + e.getMessage()));
        }
    }
//
//    @PutMapping("/update")
//    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
//    public ResponseEntity<?> updateUser(
//            @RequestParam String empId,
//            @RequestBody Map<String, Object> updates) {
//        try {
//            User updatedUser = userService.updateUserPartial(empId, updates);
//            return ResponseEntity.ok(updatedUser);
//        } catch (ResourceNotFoundException e) {
//            return errorResponse(HttpStatus.NOT_FOUND, e.getMessage());
//        } catch (UserAlreadyExistsException e) {
//            return errorResponse(HttpStatus.CONFLICT, e.getMessage());
//        } catch (Exception e) {
//            return errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, 
//                  "Failed to update user");
//        }
//    }
    
    
    @PutMapping("/update")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> updateUser(
            @RequestParam String empId,
            @RequestBody Map<String, Object> updates) throws MessagingException {
        
        // Validate allowed fields
        Set<String> allowedFields = Set.of("name", "email", "mobileNumber", "mobilePass");
        Set<String> receivedFields = updates.keySet();
        
        if (!allowedFields.containsAll(receivedFields)) {
            Set<String> invalidFields = new HashSet<>(receivedFields);
            invalidFields.removeAll(allowedFields);
            return ResponseEntity.badRequest().body(
                Map.of(
                    "status", HttpStatus.BAD_REQUEST.value(),
                    "error", "Invalid fields",
                    "message", "Only name, email, mobileNumber, and mobilePass can be updated",
                    "invalidFields", invalidFields
                )
            );
        }

        try {
            User updatedUser = userService.updateUserPartial(empId, updates);
            return ResponseEntity.ok(updatedUser);
        } catch (ResourceNotFoundException e) {
            return errorResponse(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UserAlreadyExistsException e) {
            return errorResponse(HttpStatus.CONFLICT, e.getMessage());
        } catch (Exception e) {
            return errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, 
                  "Failed to update user: " + e.getMessage());
        }
    }
    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@RequestParam String empId) {
        try {
            userService.deleteUser(empId);
            return successResponse("User deleted successfully");
        } catch (ResourceNotFoundException e) {
            return errorResponse(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            return errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, 
                  "Failed to delete user");
        }
    }

    @GetMapping("/emailReport")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> sendUserReportEmail(@RequestParam String email) {
        try {
            userService.sendUserReportEmail(email);
            return successResponse("Report sent to " + email);
        } catch (Exception e) {
            return errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, 
                  "Failed to send email report");
        }
    }

    @GetMapping("/email-user-report")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> sendSingleUserReport(
            @RequestParam String email,
            @RequestParam String empId) {
        try {
            userService.sendSingleUserReport(email, empId);
            return successResponse("User report sent to " + email);
        } catch (ResourceNotFoundException e) {
            return errorResponse(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            return errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, 
                  "Failed to send user report");
        }
    }

    @GetMapping("/DownloadExcel")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Resource> downloadExcelReport() {
        try {
            byte[] excelData = excelExportService.exportUsersToExcel(userService.getAllUsers());
            ByteArrayResource resource = new ByteArrayResource(excelData);
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                           "attachment; filename=users_report.xlsx")
                    .contentType(MediaType.parseMediaType(
                           "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(resource);
        } catch (Exception e) {
            throw new RuntimeException("Excel generation failed", e);
        }
    }

    // Helper methods
    private ResponseEntity<?> errorResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(
            Map.of(
                "status", status.value(),
                "error", status.getReasonPhrase(),
                "message", message,
                "timestamp", Instant.now()
            )
        );
    }

    private ResponseEntity<?> validationErrorResponse(BindingResult bindingResult) {
        List<String> errors = bindingResult.getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.toList());
        
        return ResponseEntity.badRequest().body(
            Map.of(
                "status", HttpStatus.BAD_REQUEST.value(),
                "error", "Validation failed",
                "message", "Invalid request data",
                "errors", errors,
                "timestamp", Instant.now()
            )
        );
    }

    private ResponseEntity<?> successResponse(String message) {
        return ResponseEntity.ok(
            Map.of(
                "status", HttpStatus.OK.value(),
                "message", message,
                "timestamp", Instant.now()
            )
        );
    }
}

