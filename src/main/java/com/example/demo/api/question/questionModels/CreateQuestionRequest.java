package com.example.demo.api.question.questionModels;

import lombok.*;

@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateQuestionRequest {
    private String title;
    private Long questionTypeId;
    private Long quizId;
}
