package com.learningplatform.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.learningplatform.dto.CourseRequest;
import com.learningplatform.entity.CourseEntity;
import com.learningplatform.entity.Role;
import com.learningplatform.entity.User;
import com.learningplatform.repository.CourseRepository;
import com.learningplatform.repository.UserRepository;
import com.learningplatform.Exception.BadRequestException;
import com.learningplatform.Exception.ResourceNotFoundException;

@Service
public class CourseServiceImpl implements CourseService {

     private final CourseRepository courseRepo;
     private final UserRepository userRepo;
    
     @Autowired
    CourseServiceImpl(UserRepository userRepo,CourseRepository courseRepo)
    {
    	this.userRepo=userRepo;
    	this.courseRepo=courseRepo;
    }
    


    @Override
    public CourseEntity createCourse(CourseRequest request, Long teacherUserId) {
        User teacher = userRepo.findById(teacherUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", teacherUserId));
        if (teacher.getRole() != Role.TEACHER)
            throw new BadRequestException("Only teachers can create courses");
        
        CourseEntity course = new CourseEntity();
        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setTeacher(teacher);   // ← bas yahi change hai
        return courseRepo.save(course);
    }

    @Override
    public List<CourseEntity> getCourseByTeacher(Long teacherUserId)
    {
        // optional: verify teacher exists
        userRepo.findById(teacherUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", teacherUserId));
        return courseRepo.findByTeacherId(teacherUserId);
    }

    
    @Override
    public CourseEntity getCourseById(Long courseId) {
        return courseRepo.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId));
    }

    @Override
    public CourseEntity updateCourse(Long courseId, CourseRequest request) {
        CourseEntity course = getCourseById(courseId);
        if (request.getTitle() != null) course.setTitle(request.getTitle());
        if (request.getDescription() != null) course.setDescription(request.getDescription());
        return courseRepo.save(course);
    }

    
    @Override
    public void deleteCourse(Long courseId) {
        CourseEntity course = getCourseById(courseId);
        courseRepo.delete(course);
    }
}