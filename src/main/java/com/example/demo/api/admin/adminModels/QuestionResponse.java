package com.example.demo.api.admin.adminModels;

import com.example.demo.api.answer.Answer;
import com.example.demo.api.questionType.QuestionType;
import lombok.*;

import java.util.List;

@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResponse {
    private Long id;
    private String title;
    private QuestionType questionType;
    private List<Answer> answers;
}
