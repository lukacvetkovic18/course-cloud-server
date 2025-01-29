package com.example.demo.api.admin.adminModels;

import lombok.*;

import java.util.List;
import java.util.Optional;

@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateQuizRequest {
    private String title;
    private Optional<Long> courseId;
    private List<QuestionRequest> questions;
}
