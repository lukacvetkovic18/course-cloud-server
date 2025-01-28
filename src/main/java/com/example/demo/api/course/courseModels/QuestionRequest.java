package com.example.demo.api.course.courseModels;

import lombok.*;

import java.util.List;

@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionRequest {
    private String title;
    private Long questionTypeId;
    private List<AnswerRequest> answers;
}
