package com.learningplatform.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizQuestionResponse 
{
	
	
	
	private String questionText;
	 
    @NotBlank(message = "Please Select a Option")
	 private String options;
	 
	
	 
	 private int marks;

}

