package com.learningplatform.dto;


import lombok.Data;

@Data
public class ModuleRequest {

	  private String title;
	    private String content;
	    private int orderIndex;
	    private int passingPercentage=50;

	    
		public Long getCourseId() {
			// TODO Auto-generated method stub
			return null;
		}
}