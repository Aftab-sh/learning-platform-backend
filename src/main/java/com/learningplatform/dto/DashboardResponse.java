package com.learningplatform.dto;


import lombok.Data;

@Data
public class DashboardResponse {
	
	private long totalCourses;
	private long totalStudents;
	private long activeTests;
	private long totalSubmissions;

}