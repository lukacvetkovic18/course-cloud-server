package com.example.demo.api.question.questionModels;

import lombok.*;

import java.util.Optional;

@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateQuestionRequest {
    private Long id;
    private Optional<String> title;
    private Optional<Long> questionTypeId;
    private Optional<Long> quizId;
}
