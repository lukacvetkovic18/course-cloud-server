package com.example.demo.api.course.courseModels;

import lombok.*;

import java.util.Optional;

@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCourseRequest {
    private Long id;
    private Optional<String> title;
    private Optional<String> shortDescription;
    private Optional<String> description;
    private Optional<Boolean> isActive;
    private Optional<String> image;
}
