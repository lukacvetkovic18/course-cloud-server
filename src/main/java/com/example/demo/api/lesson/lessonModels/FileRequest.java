package com.example.demo.api.lesson.lessonModels;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileRequest {
    private MultipartFile file;
}
