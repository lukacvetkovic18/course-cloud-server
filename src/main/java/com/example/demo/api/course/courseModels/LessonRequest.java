package com.example.demo.api.course.courseModels;

import lombok.*;

import java.util.List;

@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonRequest {
    private String title;
    private String content;
    private Integer lessonOrder;
    private List<FileRequest> files;
}
