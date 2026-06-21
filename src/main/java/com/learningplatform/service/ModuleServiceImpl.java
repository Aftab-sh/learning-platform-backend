package com.learningplatform.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.learningplatform.Exception.BadRequestException;
import com.learningplatform.Exception.ResourceNotFoundException;
import com.learningplatform.dto.ModuleRequest;
import com.learningplatform.entity.CourseEntity;
import com.learningplatform.entity.ModuleEntity;
import com.learningplatform.repository.CourseRepository;
import com.learningplatform.repository.ModuleRepository;

import java.util.List;

@Service
public class ModuleServiceImpl implements ModuleService {

    private final ModuleRepository moduleRepo;

    
    private final CourseRepository courseRepo;

    
    private final ModelMapper modelMapper;
    
    @Autowired
    ModuleServiceImpl(ModelMapper modelMapper,CourseRepository courseRepo,ModuleRepository moduleRepo)
    {
    	this.modelMapper=modelMapper;
    	this.courseRepo=courseRepo;
         this.moduleRepo=moduleRepo;
    }

    @Override
    public ModuleEntity createModule(ModuleRequest request, Long courseId) {
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new BadRequestException("Module title is required");
        }
        // verify course exists (optional but good)
//        courseRepo.findById(courseId)
//                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId));
//
//        ModuleEntity module = modelMapper.map(request, ModuleEntity.class);
//        module.setCourseId(courseId);
//        return moduleRepo.save(module);
     // ✅ FIX 5 — createModule() mein
        CourseEntity course = courseRepo.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId));

        ModuleEntity module = modelMapper.map(request, ModuleEntity.class);
        module.setCourse(course);    // ← setCourseId() → setCourse()
        return moduleRepo.save(module);
    }

    @Override
    public List<ModuleEntity> getModulesByCourse(Long courseId) {
        return moduleRepo.findByCourseIdOrderByOrderIndexAsc(courseId);
    }

    @Override
    public ModuleEntity getModuleById(Long moduleId) {
        return moduleRepo.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Module", "id", moduleId));
    }

    @Override
    public ModuleEntity updateModule(Long moduleId, ModuleRequest request) {
        ModuleEntity module = getModuleById(moduleId);
        module.setTitle(request.getTitle());
        module.setContent(request.getContent());
        if (request.getOrderIndex() != 0) module.setOrderIndex(request.getOrderIndex());
        // passingPercentage can also be updated if needed
        return moduleRepo.save(module);
    }

    @Override
    public void deleteModule(Long moduleId) {
        ModuleEntity module = getModuleById(moduleId);
        moduleRepo.delete(module);
    }
    
    
}