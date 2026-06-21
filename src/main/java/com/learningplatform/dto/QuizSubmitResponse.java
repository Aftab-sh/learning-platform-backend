

//result bhejne ke liye
package com.learningplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QuizSubmitResponse {
    private double score;      // percentage
    private boolean passed;
    private String message;
}