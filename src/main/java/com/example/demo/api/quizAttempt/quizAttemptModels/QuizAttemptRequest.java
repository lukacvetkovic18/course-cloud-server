package com.example.demo.api.quizAttempt.quizAttemptModels;

import jakarta.persistence.Entity;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class QuizAttemptRequest {
    private Map<Long, Long> selectedAnswerIds;
    private Map<Long, String> textAnswers;
}
