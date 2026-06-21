package com.learningplatform.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.learningplatform.entity.Role;
import com.learningplatform.entity.User;

public interface UserRepository extends JpaRepository<User,Long>
{

	   Optional<User>  findByEmail(String email);
	    Boolean existsByEmail(String email);
	    List<User> findByRole(Role role);
	    Optional<User> findByVerificationToken(String token);
	    Optional<User> findByResetToken(String resetToken);
	    
	
}

