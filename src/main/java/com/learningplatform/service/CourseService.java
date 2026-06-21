package com.learningplatform.service;

import java.util.List;

import com.learningplatform.dto.CourseRequest;
import com.learningplatform.entity.CourseEntity;


public interface CourseService {

	CourseEntity createCourse(CourseRequest request,Long teacherId);
	
	List<CourseEntity> getCourseByTeacher(Long teacherId);
	
	
	 CourseEntity getCourseById(Long courseId);

	 CourseEntity updateCourse(Long courseId, CourseRequest request);

	    void deleteCourse(Long courseId);
}