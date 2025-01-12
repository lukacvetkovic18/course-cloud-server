package com.example.demo.api.file;

import com.example.demo.api.lesson.Lesson;
import com.example.demo.api.lesson.LessonService;
import com.example.demo.api.lesson.lessonModels.CreateLessonRequest;
import com.example.demo.api.lesson.lessonModels.UpdateLessonRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/api/files")
public class FileController {

    private final FileService fileService;

    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping(path = "/upload")
    public ResponseEntity<File> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("lessonId") Long lessonId
    ) throws IOException {
        File savedFile = fileService.saveFile(file, lessonId);
        return ResponseEntity.ok(savedFile);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<byte[]> getFile(@PathVariable Long id) throws IOException {
        File file = fileService.getFile(id).orElseThrow();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .header(HttpHeaders.CONTENT_TYPE, file.getType())
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(file.getData().length))
                .body(file.getData());
    }
}
