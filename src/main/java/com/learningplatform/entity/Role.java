package com.learningplatform.entity;

import java.util.Set;

public enum Role
{
	TEACHER(Set.of(Permissions.TEACHER_CREATE_COURSE
			,Permissions.TEACHER_UPDATE_COURSE
			,Permissions.TEACHER_DELETE_COURSE
			,Permissions.TEACHER_CREATE_MODULE
			,Permissions.TEACHER_UPDATE_MODULE
			,Permissions.TEACHER_CREATE_QUIZ
			,Permissions.TEACHER_VIEW_STUDENTS
			,Permissions.TEACHER_CREATE_CODING_PROBLEM)),
	


	
	
	
	STUDENT (Set.of(Permissions.STUDENT_READ_ENROLLED_COURSES
			,Permissions.STUDENT_WRITE_ENROLL_COURSES_BYID

			,Permissions.STUDENT_READ_MODULES_BY_COURSES
			,Permissions.STUDENT_READ_QUIZ_QUESTIONS
			,Permissions.STUDENT_WRITE_SUBMIT_QUIZE
			,Permissions.STUDENT_READ_PROGRESS
			,Permissions.STUDENT_READ_ALLCOURSES
			,Permissions.STUDENT_READ_CODINGPROBLEMS
			,Permissions.STUDENT_WRITE_SUBMIT_CODE
			

			));

private final Set<Permissions> permissions;

Role(Set<Permissions> permissions)
{
	this.permissions=permissions;
	// TODO Auto-generated constructor stub
}
public Set<Permissions> getPermissions()
{
	return permissions;
}

}