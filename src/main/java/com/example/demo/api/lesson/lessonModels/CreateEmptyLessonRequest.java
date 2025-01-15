package com.example.demo.api.lesson.lessonModels;

import lombok.*;

@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateEmptyLessonRequest {
    private Long courseId;
}