package com.example.demo.api.course.courseModels;

import lombok.*;

@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileRequest {
    private String name;
    private String type;
    private String data;
}
