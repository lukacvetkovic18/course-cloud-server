package com.example.demo.api.quizAttempt.quizAttemptModels;

import jakarta.persistence.Entity;
import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuizAttemptRequest {
    private Map<Long, List<Long>> selectedAnswerIds;
    private Map<Long, String> textAnswers;
}
