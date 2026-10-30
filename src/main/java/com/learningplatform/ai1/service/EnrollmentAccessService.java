
package com.learningplatform.ai1.service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.learningplatform.entity.User;
import com.learningplatform.repository.UserRepository;
import com.learningplatform.repository.EnrollmentRepository;

@Service
public class EnrollmentAccessService {

    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;

    public EnrollmentAccessService(
            UserRepository userRepository,
            EnrollmentRepository enrollmentRepository) {

        this.userRepository = userRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    public void validateCourseAccess(Long courseId) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("User is not authenticated");
        }

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new AccessDeniedException("User not found"));

        boolean enrolled = enrollmentRepository
                .existsByStudent_IdAndCourse_Id(user.getId(), courseId);

        if (!enrolled) {
            throw new AccessDeniedException(
                    "You are not enrolled in this course");
        }
    }
}
