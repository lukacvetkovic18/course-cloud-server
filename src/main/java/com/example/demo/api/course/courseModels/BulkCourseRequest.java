package com.example.demo.api.course.courseModels;

import lombok.*;

import java.util.List;

@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkCourseRequest {
    private String title;
    private String shortDescription;
    private String description;
    private Boolean isActive;
    private String image;
    private List<LessonRequest> lessons;
    private QuizRequest quiz;
}
