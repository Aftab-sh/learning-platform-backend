package com.learningplatform.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.learningplatform.dto.CourseRequest;
import com.learningplatform.entity.CourseEntity;
import com.learningplatform.service.CourseService;



@Controller
@RequestMapping("/teacher/course")
public class CourseViewController {
	

    
    private CourseService courseService;
    
    @Autowired
    CourseViewController(CourseService courseService)
    {
    	this.courseService=courseService;
    	
    	
    }


    // 🔥 page open (teacherId receive karega)
    @GetMapping("/create")
    public String createPage(@RequestParam Long teacherId, Model model) {

        // 🔥 teacherId HTML ko bhejna
        model.addAttribute("teacherId", teacherId);

        return "Course";
    }

    // 🔥 form submit
    @PostMapping("/create")
    public String createCourse(
            @ModelAttribute CourseRequest request,
            @RequestParam Long teacherId,
            Model model) {

        try {

            courseService.createCourse(request, teacherId);

            // success message
            model.addAttribute("success", "Course created successfully");

        } catch (Exception e) {

            // error message
            model.addAttribute("error", e.getMessage());
        }

        // 🔥 IMPORTANT: dobara teacherId bhejna padega
        model.addAttribute("teacherId", teacherId);

        return "Course";
    }
    
    @GetMapping("/list")
    public String courseList(@RequestParam Long teacherId, Model model) {

        List<CourseEntity> courses = courseService.getCourseByTeacher(teacherId);

        model.addAttribute("courses", courses);
        model.addAttribute("teacherId", teacherId); // future navigation ke liye

        return "Course";
    }
    
    
    @GetMapping("/delete")
    public String deleteCourse(@RequestParam Long courseId,
                               @RequestParam Long teacherId) {

        courseService.deleteCourse(courseId);

        return "redirect:/teacher/course/list?teacherId=" + teacherId;
    }
    
    
    @GetMapping("/edit")
    public String editPage(@RequestParam Long courseId,
                           @RequestParam Long teacherId,
                           Model model) {

        CourseEntity course = courseService.getCourseById(courseId);

        model.addAttribute("course", course);
        model.addAttribute("teacherId", teacherId);

        return "edit-course";
    }
    
    
    @PostMapping("/update")
    public String updateCourse(@ModelAttribute CourseRequest request,
                               @RequestParam Long courseId,
                               @RequestParam Long teacherId) {

        courseService.updateCourse(courseId, request);

        return "redirect:/teacher/course/list?teacherId=" + teacherId;
    }
    
}