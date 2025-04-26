package com.example.userapi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthenticationRequest {
//    private String empId;
//    private String password;
	  @NotBlank(message = "Employee ID is required")
	    private String empId;
	    
	    @NotBlank(message = "Password is required")
	    private String password;

    // Default constructor for JSON parsing
    public AuthenticationRequest() {
    }

    public AuthenticationRequest(String empId, String password) {
        this.empId = empId;
        this.password = password;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}