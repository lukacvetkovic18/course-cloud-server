package com.example.demo.api.lesson.lessonModels;

import com.example.demo.api.course.Course;
import com.example.demo.api.file.File;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonResponse {
    private Long id;
    private Course course;
    private String title;
    private Timestamp createdAt;
    private List<File> files;
}