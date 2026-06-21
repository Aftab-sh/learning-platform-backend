package com.learningplatform.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.learningplatform.dto.ModuleRequest;
import com.learningplatform.entity.ModuleEntity;
import com.learningplatform.service.ModuleService;



@Controller
@RequestMapping("/teacher/module")
public class TeacherModuleViewController {

    
    private final ModuleService moduleService;
    
    @Autowired
    TeacherModuleViewController(ModuleService moduleService)
    {
    	this.moduleService=moduleService;
    	
    }
    

    // ================= MODULE LIST =================
    @GetMapping("/list")
    public String moduleList(@RequestParam Long courseId,
                             @RequestParam Long teacherId,
                             Model model) {

        List<ModuleEntity> modules =
                moduleService.getModulesByCourse(courseId);

        model.addAttribute("modules", modules);
        model.addAttribute("courseId", courseId);
        model.addAttribute("teacherId", teacherId);

        return "module-list";
    }

    // ================= CREATE PAGE =================
    @GetMapping("/create")
    public String createPage(@RequestParam Long courseId,
                             @RequestParam Long teacherId,
                             Model model) {

        model.addAttribute("courseId", courseId);
        model.addAttribute("teacherId", teacherId);

        return "create-module";
    }

    // ================= CREATE MODULE =================
    @PostMapping("/create")
    public String createModule(@ModelAttribute ModuleRequest request,
                               @RequestParam Long courseId,
                               @RequestParam Long teacherId) {

        moduleService.createModule(request, courseId);

        return "redirect:/teacher/module/list?courseId="
                + courseId + "&teacherId=" + teacherId;
    }
    
 // ================= VIEW MODULE =================
    @GetMapping("/view")
    public String viewModule(@RequestParam Long moduleId,
                             @RequestParam Long teacherId,
                             Model model) {

        ModuleEntity module =
                moduleService.getModuleById(moduleId);

        model.addAttribute("module", module);
        model.addAttribute("teacherId", teacherId);

        return "view-module";
    }
    
 // ================= EDIT PAGE =================
    @GetMapping("/edit")
    public String editPage(@RequestParam Long moduleId,
                           @RequestParam Long teacherId,
                           Model model) {

        ModuleEntity module =
                moduleService.getModuleById(moduleId);

        model.addAttribute("module", module);
        model.addAttribute("teacherId", teacherId);

        return "edit-module";
    }
    
 // ================= UPDATE MODULE =================
    @PostMapping("/update")
    public String updateModule(@ModelAttribute ModuleRequest request,
                               @RequestParam Long moduleId,
                               @RequestParam Long teacherId) {

        moduleService.updateModule(moduleId, request);

        return "redirect:/teacher/module/list?courseId="
                + request.getCourseId()
                + "&teacherId=" + teacherId;
    }
    
 // ================= DELETE MODULE =================
    @GetMapping("/delete")
    public String deleteModule(@RequestParam Long moduleId,
                               @RequestParam Long courseId,
                               @RequestParam Long teacherId) {

        moduleService.deleteModule(moduleId);

        return "redirect:/teacher/module/list?courseId="
                + courseId + "&teacherId=" + teacherId;
    }
}