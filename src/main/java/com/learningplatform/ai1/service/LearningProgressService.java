package com.learningplatform.ai1.service;



import com.learningplatform.ai1.dto.StudentProgressSummaryDTO;
import com.learningplatform.dto.ModuleProgressDTO;
import com.learningplatform.entity.SolvedCodingProblem;
import com.learningplatform.repository.SolvedCodingProblemRepository;
import com.learningplatform.service.StudentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import com.learningplatform.repository.UserRepository;

@Service
public class LearningProgressService {

    private final StudentService studentService;
    private final SolvedCodingProblemRepository solvedCodingProblemRepository;
    private final UserRepository userRepository;

    public LearningProgressService(
    		StudentService studentService,
    		SolvedCodingProblemRepository solvedCodingProblemRepository,
    		UserRepository userRepository)
    {

        this.studentService = studentService;
        this.solvedCodingProblemRepository = solvedCodingProblemRepository;
        this.userRepository = userRepository;
    }
    
    
    
    public Long getStudentIdByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Student not found"))
                .getId();
    }

    public List<ModuleProgressDTO> getModuleProgress(String email, Long courseId) 
    {

        return studentService.getProgressForCourse(email, courseId);
    }

    public List<SolvedCodingProblem> getSolvedProblems(Long studentId)
    {

        return solvedCodingProblemRepository.findByStudentId(studentId);
    }

    public StudentProgressSummaryDTO createProgressSummary(
            String email,
            Long studentId,
            Long courseId) {

        List<ModuleProgressDTO> modules =
                studentService.getProgressForCourse(email, courseId);

        List<SolvedCodingProblem> solvedProblems =
                solvedCodingProblemRepository.findByStudentId(studentId);

        List<String> solvedProblemTitles = solvedProblems.stream()
                .map(solved -> solved.getProblem().getTitle()
                        + " (" + solved.getProblem().getDifficulty() + ")")
                .collect(Collectors.toList());

        StudentProgressSummaryDTO summary =new StudentProgressSummaryDTO();

        summary.setCourseId(courseId);
        summary.setTotalModules(modules.size());

        int completedCount = (int) modules.stream()
                .filter(ModuleProgressDTO::isCompleted)
                .count();

        summary.setCompletedModules(completedCount);
        summary.setModuleProgress(modules);
        summary.setSolvedCodingProblems(solvedProblemTitles);

        return summary;
    }
    
    
    
}
