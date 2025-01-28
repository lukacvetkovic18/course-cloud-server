package com.example.demo.api.course.courseModels;

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
