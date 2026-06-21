package com.learningplatform.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.learningplatform.dto.ModuleRequest;
import com.learningplatform.entity.ModuleEntity;
import com.learningplatform.service.ModuleService;


@RestController
@RequestMapping("/api/module")
public class ModuleController
{

    private final ModuleService moduleService;
    
    @Autowired
    ModuleController(ModuleService moduleService)
    {
    	this.moduleService=moduleService;
    	
    }

    //CREATE MODULE
    @PostMapping("/create/{courseId}")
    public ResponseEntity<ModuleEntity> createModule(
            @RequestBody ModuleRequest request,
            @PathVariable Long courseId) 
    {

    	ModuleEntity module =
                moduleService.createModule(request, courseId);
    	if (request.getPassingPercentage() <= 0) {
    	    request.setPassingPercentage(50);
    	}

        return ResponseEntity.ok(module);
    }

    //GET ALL MODULES
    @GetMapping("/list/{courseId}")
    public ResponseEntity<List<ModuleEntity>> getModulesByCourse(
            @PathVariable Long courseId) {

        List<ModuleEntity> modules =
                moduleService.getModulesByCourse(courseId);

        return ResponseEntity.ok(modules);
    }

    //GET SINGLE MODULE
    @GetMapping("/{moduleId}")
    public ResponseEntity<ModuleEntity> getModuleById(
            @PathVariable Long moduleId) {

    	ModuleEntity module =
                moduleService.getModuleById(moduleId);

        return ResponseEntity.ok(module);
    }

    //UPDATE MODULE 
    @PutMapping("/update/{moduleId}")
    public ResponseEntity<ModuleEntity> updateModule(
            @PathVariable Long moduleId,
            @RequestBody ModuleRequest request) {

    	ModuleEntity updatedModule =
                moduleService.updateModule(moduleId, request);

        return ResponseEntity.ok(updatedModule);
    }

    //  DELETE MODULE 
    @DeleteMapping("/delete/{moduleId}")
    public ResponseEntity<String> deleteModule(
            @PathVariable Long moduleId) {

        moduleService.deleteModule(moduleId);

        return ResponseEntity.ok(
                "Module deleted successfully");
    }
}
