package com.example.demo.api.course.courseModels;

import lombok.*;

import java.util.Optional;

@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCourseRequest {
    private String title;
    private String shortDescription;
    private String description;
    private Boolean isActive;
    private Optional<String> image;
}
