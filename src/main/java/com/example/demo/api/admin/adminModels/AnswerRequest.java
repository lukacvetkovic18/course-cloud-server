package com.example.demo.api.admin.adminModels;

import com.example.demo.api.question.Question;
import lombok.*;

@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerRequest {
    private String title;
    private Boolean isCorrect;
}