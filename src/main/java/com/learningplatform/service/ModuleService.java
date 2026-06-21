package com.learningplatform.service;


import java.util.List;

import com.learningplatform.dto.ModuleRequest;
import com.learningplatform.entity.ModuleEntity;



public interface ModuleService {

	ModuleEntity createModule(ModuleRequest request, Long courseId);

    List<ModuleEntity> getModulesByCourse(Long courseId);

    ModuleEntity getModuleById(Long moduleId);

    ModuleEntity updateModule(Long moduleId, ModuleRequest request);

    void deleteModule(Long moduleId);
	
}