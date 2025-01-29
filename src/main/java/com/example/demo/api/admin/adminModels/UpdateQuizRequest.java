package com.example.demo.api.admin.adminModels;

import lombok.*;

import java.util.List;
import java.util.Optional;

@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateQuizRequest {
    private Optional<String> title;
    private Optional<Long> courseId;
    private Optional<List<QuestionRequest>> questions;
}
