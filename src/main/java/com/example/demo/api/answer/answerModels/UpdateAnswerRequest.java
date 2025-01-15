package com.example.demo.api.answer.answerModels;

import lombok.*;

import java.util.Optional;

@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAnswerRequest {
    private Long id;
    private Optional<String> title;
    private Optional<Boolean> isCorrect;
    private Optional<Long> questionId;
}
