package com.example.demo.api.question.questionModels;

import com.example.demo.api.answer.answerModels.UpdateAnswerRequest;
import lombok.*;

import java.util.List;

@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateQuestionWithAnswersRequest {
    private Long id;
    private String title;
    private Long questionTypeId;
    private List<UpdateAnswerRequest> answers;
}