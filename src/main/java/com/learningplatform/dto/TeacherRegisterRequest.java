package com.learningplatform.dto;


import lombok.Data;

@Data
public class TeacherRegisterRequest {

	private String name;
	
    private String email;
    
    private String password;
    
    private String role;
	
}
