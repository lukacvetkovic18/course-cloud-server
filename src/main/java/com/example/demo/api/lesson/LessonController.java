package com.example.demo.api.lesson;

import com.example.demo.api.course.Course;
import com.example.demo.api.lesson.lessonModels.CreateEmptyLessonRequest;
import com.example.demo.api.lesson.lessonModels.CreateLessonRequest;
import com.example.demo.api.lesson.lessonModels.UpdateLessonRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

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
    public Lesson createLesson(@RequestBody CreateLessonRequest lesson) {
        return lessonService.createLesson(lesson);
    }

    @GetMapping
    public List<Lesson> getAllLessons() { return lessonService.getAllLessons(); }

    @GetMapping(path = "/{id}")
    public Optional<Lesson> getLessonById(@PathVariable("id") long id) {
        return lessonService.getLessonById(id);
    }

    @PutMapping
    public Lesson updateLesson(@RequestBody UpdateLessonRequest lesson) {
        return lessonService.updateLesson(lesson);
    }

    @DeleteMapping
    public void deleteAllLessons() {
        lessonService.deleteAllLessons();
    }

    @DeleteMapping(path = "/{id}")
    public void deleteLesson(@PathVariable("id") long id) {
        lessonService.deleteLesson(id);
    }

    @GetMapping(path = "/course/{id}")
    public List<Lesson> getLessonsByCourseId(@PathVariable("id") long id) {
        return lessonService.getLessonsByCourseId(id);
    }
}
