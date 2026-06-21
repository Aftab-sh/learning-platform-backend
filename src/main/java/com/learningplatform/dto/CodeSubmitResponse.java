package com.learningplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CodeSubmitResponse {
    private boolean passed;
    private String status;
    private String actualOutput;
    private String expectedOutput;
    private String message;
    private Integer marks;
}