package com.example.demo.api.quiz.quizModels;

import lombok.*;

@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateQuizRequest {
    private String title;
    private Long courseId;
}
