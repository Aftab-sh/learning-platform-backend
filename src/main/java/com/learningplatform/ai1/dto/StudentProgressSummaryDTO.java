package com.learningplatform.ai1.dto;


import com.learningplatform.dto.ModuleProgressDTO;
import lombok.Data;

import java.util.List;

import com.learningplatform.ai1.dto.StudentProgressSummaryDTO;


@Data
public class StudentProgressSummaryDTO {

    private Long courseId;

    private int totalModules;

    private int completedModules;

    private List<ModuleProgressDTO> moduleProgress;

    private List<String> solvedCodingProblems;
}