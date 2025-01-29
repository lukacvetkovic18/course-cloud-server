package com.example.demo.api.admin.adminModels;

import lombok.*;

import java.util.List;

@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateLessonRequest {
    private Long courseId;
    private String title;
    private List<FileRequest> files;
}