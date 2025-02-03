package com.example.demo.api.lesson;

import com.example.demo.api.course.Course;
import com.example.demo.api.lesson.lessonModels.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api/lessons")
public class LessonController {

    private final LessonService lessonService;

    @Autowired
    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    @PostMapping("/empty")
    public Lesson createEmptyLesson(@RequestBody CreateEmptyLessonRequest lessonRequest) {
        return lessonService.createEmptyLesson(lessonRequest.getCourseId());
    }

    @PostMapping
    public ResponseEntity<LessonResponse> createLesson(
            @RequestParam("courseId") Long courseId,
            @RequestParam("title") String title,
            @RequestParam("files") List<MultipartFile> files) throws IOException {
        CreateLessonRequest lessonRequest = CreateLessonRequest.builder()
                .courseId(courseId)
                .title(title)
                .files(files.stream().map(file -> {
                    FileRequest fileRequest = new FileRequest();
                    fileRequest.setFile(file);
                    return fileRequest;
                }).collect(Collectors.toList()))
                .build();

        LessonResponse lessonResponse = lessonService.createLesson(lessonRequest);
        return ResponseEntity.ok(lessonResponse);
    }

    @GetMapping
    public ResponseEntity<List<LessonResponse>> getAllLessons() {
        List<LessonResponse> lessons = lessonService.getAllLessons();
        return ResponseEntity.ok(lessons);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<LessonResponse> getLessonById(@PathVariable("id") long id) {
        LessonResponse lesson = lessonService.getLessonById(id);
        return ResponseEntity.ok(lesson);
    }

    @PutMapping
    public ResponseEntity<LessonResponse> updateLesson(
            @RequestParam("id") Long id,
            @RequestParam("courseId") Long courseId,
            @RequestParam("title") String title,
            @RequestParam("files") List<MultipartFile> files) throws IOException {
        UpdateLessonRequest lessonRequest = UpdateLessonRequest.builder()
                .id(id)
                .courseId(Optional.of(courseId))
                .title(Optional.of(title))
                .files(Optional.of(files.stream().map(file -> {
                    FileRequest fileRequest = new FileRequest();
                    fileRequest.setFile(file);
                    return fileRequest;
                }).collect(Collectors.toList())))
                .build();

        LessonResponse lessonResponse = lessonService.updateLesson(lessonRequest);
        return ResponseEntity.ok(lessonResponse);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllLessons() {
        lessonService.deleteAllLessons();
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteLesson(@PathVariable Long id) {
        lessonService.deleteLesson(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "/course/{id}")
    public ResponseEntity<List<LessonResponse>> getLessonsByCourseId(@PathVariable("id") long id) {
        List<LessonResponse> lessons = lessonService.getLessonsByCourseId(id);
        return ResponseEntity.ok(lessons);
    }
}
