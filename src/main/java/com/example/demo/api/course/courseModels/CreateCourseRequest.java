package com.example.demo.api.course.courseModels;

import lombok.*;

@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCourseRequest {
    private String title;
    private String description;
    private Boolean isActive;
    private Float duration;
}
