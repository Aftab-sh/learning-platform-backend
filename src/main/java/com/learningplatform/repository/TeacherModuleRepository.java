package com.learningplatform.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import  com.learningplatform.entity.Teacher;

public interface TeacherModuleRepository extends JpaRepository<Teacher,Long> {

	    Optional<Teacher> findByEmail(String email);
	
}