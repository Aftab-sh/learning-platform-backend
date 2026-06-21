package com.learningplatform.dto;


import lombok.Data;



@Data

public class UserResponse 
{
	 private String email;
	 private Long id;

    private String name;
    
    //admin can pass the role 
    
    private String role;
	public UserResponse(Long id,String name,String email,String role)
	{
		this.id=id;
		this.name=name;
		this.email=email;
		this.role=role;
	}

   
}
