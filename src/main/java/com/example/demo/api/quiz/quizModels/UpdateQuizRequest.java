package com.example.demo.api.quiz.quizModels;

import lombok.*;

import java.util.Optional;

@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateQuizRequest {
    private Long id;
    private Optional<String> title;
    private Optional<Long> courseId;
}
