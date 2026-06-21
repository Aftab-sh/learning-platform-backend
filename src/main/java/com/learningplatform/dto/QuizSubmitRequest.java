
// student jo answers bhejega
package com.learningplatform.dto;

import lombok.Data;
import java.util.Map;

@Data
public class QuizSubmitRequest {
    private Long moduleId;
    private Map<Long, Integer> answers;   // key = questionId, value = selected option index (0-3)
}