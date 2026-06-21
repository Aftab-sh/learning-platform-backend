package com.learningplatform.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest 
{
	@NotBlank(message="Email is required")//inn annotaion ke liye spring-boot-starter-validation dependenct add krna hogi 
	@Email(message="Email should be valid")
private String email;
	
	@NotBlank(message="Password is required")
	private String password;
}
