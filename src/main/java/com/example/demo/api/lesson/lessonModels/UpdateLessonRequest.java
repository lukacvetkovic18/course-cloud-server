package com.example.demo.api.lesson.lessonModels;

import lombok.*;

import java.util.List;
import java.util.Optional;

@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateLessonRequest {
    private Long id;
    private Optional<Long> courseId;
    private Optional<String> title;
    private Optional<List<FileRequest>> files;
}
