package com.example.demo.api.course.courseModels;

import lombok.*;

import java.util.List;

@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizRequest {
    private String title;
    private List<QuestionRequest> questions;
}
