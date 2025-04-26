package com.example.userapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonFilter;

import java.util.Date;

@Entity
@Table(name = "users")
//@JsonFilter("userFilter")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "emp_id", unique = true, nullable = false, length = 6, updatable = false)
    @Pattern(regexp = "\\d{6}", message = "Employee ID must be exactly 6 digits")
    private String empId;
    
    @NotBlank(message = "Password is mandatory")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Column(name = "password", nullable = false)
    private String password;

    @NotBlank(message = "Name is mandatory")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Column(name = "name", nullable = false)
    private String name;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email must be valid")
    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @NotBlank(message = "Mobile number is mandatory")
    @Pattern(regexp = "\\d{10}", message = "Mobile number must be 10 digits")
    @Column(name = "mobile_number", unique = true, nullable = false, length = 10)
    private String mobileNumber;

    @NotNull(message = "Mobile pass is mandatory")
    @Column(name = "mobile_pass", nullable = false)
    private Boolean mobilePass;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    
    
    private Date updatedAt;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmpId() { return empId; }
    public void setEmpId(String empId) { this.empId = empId; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }
    public Boolean getMobilePass() { return mobilePass; }
    public void setMobilePass(Boolean mobilePass) { this.mobilePass = mobilePass; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}