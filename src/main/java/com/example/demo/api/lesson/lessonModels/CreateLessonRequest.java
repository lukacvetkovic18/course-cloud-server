package com.example.demo.api.lesson.lessonModels;

import jakarta.persistence.Column;
import lombok.*;

@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateLessonRequest {
    private Long courseId;
    private String title;
    private String content;
    private Integer lessonOrder;
}
