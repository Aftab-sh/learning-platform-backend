package com.learningplatform.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import  com.learningplatform.entity.Teacher;

public interface TeacherRepository extends JpaRepository<Teacher,Long> {

	    Optional<Teacher> findByEmail(String email);
	
}