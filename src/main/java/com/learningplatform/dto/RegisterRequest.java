package com.learningplatform.dto;

import com.learningplatform.entity.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest 
{
	
	//iss ki ye charo field fill krna mendetory hi he 
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 15, message = "Name must be between 2 and 15 characters")
    private String name;
    
    //admin can pass the role 
    
    private Role role;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 15, message = "Password must be between 6 and 15 characters")
    private String password;

    
    private String profileImageUrl; 
    
    

}
