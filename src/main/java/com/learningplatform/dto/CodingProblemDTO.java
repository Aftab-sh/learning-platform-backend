package com.learningplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CodingProblemDTO {
    private Long id;
    private Long moduleId;
    private String title;
    private String description;
    private String constraintsText;
    private String sampleInput;
    private String sampleOutput;
    private int orderIndex;
    private String difficulty;
    private Integer marks;
    // hiddenTestInput/Output nahi bhejenge student ko!
}