//package com.learningplatform.controller;
//
//import java.util.List;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
//import org.springframework.web.bind.annotation.*;
//import com.learningplatform.dto.CourseRequest;
//import com.learningplatform.entity.CourseEntity;
//import com.learningplatform.entity.Role;
//import com.learningplatform.entity.User;
//import com.learningplatform.service.CourseService;
//import com.learningplatform.service.UserService;
//
//@RestController
//@RequestMapping("/api/teacher/course")
//public class CourseController {
//
//    @Autowired 
//    private CourseService courseService;
//
//    @Autowired
//    private UserService userService;   // ✅ Inject UserService
//
//    // ✅ Get courses of currently logged-in teacher
//    @GetMapping("/courses")
//    public ResponseEntity<List<CourseEntity>> getMyCourses(Authentication auth) {
//        String email = auth.getName();
//        User teacher = userService.findByEmail(email);
//        if (teacher.getRole() != Role.TEACHER) {
//            return ResponseEntity.status(403).build();
//        }
//        List<CourseEntity> courses = courseService.getCourseByTeacher(teacher.getId());
//        return ResponseEntity.ok(courses);
//    }
//
//    // ✅ Create a new course (teacherId is user id of teacher)
//    @PostMapping("/create/{teacherId}")
//    public ResponseEntity<?> createCourse(@RequestBody CourseRequest request,
//                                          @PathVariable Long teacherId) {
//        // Optional: you can add role check here too
//        return ResponseEntity.ok(courseService.createCourse(request, teacherId));
//    }
//
//    // ✅ Get all courses of a specific teacher by ID (can be used by admin or teacher himself)
//    @GetMapping("/list/{teacherId}")
//    public List<CourseEntity> listCourses(@PathVariable Long teacherId) {
//        return courseService.getCourseByTeacher(teacherId);
//    }
//}


package com.learningplatform.controller;

import com.learningplatform.dto.CourseRequest;
import com.learningplatform.entity.CourseEntity;
import com.learningplatform.entity.Role;
import com.learningplatform.entity.User;
import com.learningplatform.repository.EnrollmentRepository;
import com.learningplatform.service.CourseService;
import com.learningplatform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/teacher/course")
public class CourseController {

    
    private final CourseService courseService;

    
    private final UserService userService;

    
    private final EnrollmentRepository enrollmentRepository;  // for student count
    
    @Autowired
    CourseController(CourseService courseService,
    		UserService userService,
    		EnrollmentRepository enrollmentRepository)
    {
    	this.courseService=courseService;
    	this.userService=userService;
    	this.enrollmentRepository=enrollmentRepository;
    }

    // Get courses of logged-in teacher (with enrollment count)
    @GetMapping("/courses")
    public ResponseEntity<List<Map<String, Object>>> getMyCourses(Authentication auth) {
        String email = auth.getName();
        User teacher = userService.findByEmail(email);
        if (teacher.getRole() != Role.TEACHER) {
            return ResponseEntity.status(403).build();
        }
        List<CourseEntity> courses = courseService.getCourseByTeacher(teacher.getId());
        List<Map<String, Object>> result = courses.stream().map(c -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getId());
            map.put("title", c.getTitle());
            map.put("description", c.getDescription());
            map.put("language", c.getLanguage());
            // count enrolled students for this course
            long studentCount = enrollmentRepository.countByCourseId(c.getId());
            map.put("enrolledStudents", studentCount);
            return map;
        }).toList();
        return ResponseEntity.ok(result);
    }

    // Create a new course
    @PostMapping("/create/{teacherId}")
    public ResponseEntity<?> createCourse(@RequestBody CourseRequest request,
                                          @PathVariable Long teacherId) {
        return ResponseEntity.ok(courseService.createCourse(request, teacherId));
    }

    // Get all courses of a specific teacher (simple list)
    @GetMapping("/list/{teacherId}")
    public List<CourseEntity> listCourses(@PathVariable Long teacherId) {
        return courseService.getCourseByTeacher(teacherId);
    }

    // ✅ DELETE course
    @DeleteMapping("/delete/{courseId}")
    public ResponseEntity<String> deleteCourse(@PathVariable Long courseId) {
        courseService.deleteCourse(courseId);
        return ResponseEntity.ok("Course deleted");
    }

    // ✅ UPDATE course
    @PutMapping("/update/{courseId}")
    public ResponseEntity<String> updateCourse(@PathVariable Long courseId,
                                               @RequestParam String title,
                                               @RequestParam String description) {
        CourseRequest request = new CourseRequest();
        request.setTitle(title);
        request.setDescription(description);
        courseService.updateCourse(courseId, request);
        return ResponseEntity.ok("Course updated");
    }
}