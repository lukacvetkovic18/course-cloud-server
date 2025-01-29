package com.example.demo.api.admin.adminModels;

import com.example.demo.api.course.Course;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizResponse {
    private Long id;
    private String title;
    private Course course;
    private Timestamp createdAt;
    private List<QuestionResponse> questions;
}
