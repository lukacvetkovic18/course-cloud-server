package com.example.demo.api.quiz.quizModels;

import lombok.*;

import java.util.List;
import java.util.Optional;

@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateQuizRequest {
    private String title;
    private Optional<Long> courseId;
    private List<QuestionRequest> questions;
}
