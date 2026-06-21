package com.learningplatform.dto;

import lombok.Data;

@Data
public class CodeSubmitRequest {
    private Long problemId;
    private String sourceCode;
    private Integer languageId;
    // Judge0 Language IDs:
    // 71 = Python
    // 62 = Java
    // 50 = C
    // 54 = C++
}