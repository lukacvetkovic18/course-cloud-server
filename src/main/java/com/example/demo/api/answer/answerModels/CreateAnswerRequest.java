package com.example.demo.api.answer.answerModels;

import lombok.*;

@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAnswerRequest {
    private String title;
    private Boolean isCorrect;
    private Long questionId;
}
