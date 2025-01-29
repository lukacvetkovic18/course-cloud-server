package com.example.demo.api.admin;

import com.example.demo.api.admin.adminModels.*;
import com.example.demo.api.answer.Answer;
import com.example.demo.api.course.Course;
import com.example.demo.api.enrollment.Enrollment;
import com.example.demo.api.file.File;
import com.example.demo.api.lesson.Lesson;
import com.example.demo.api.question.Question;
import com.example.demo.api.quiz.Quiz;
import com.example.demo.api.user.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // USERS

    @PostMapping("/users")
    public ResponseEntity<UserResponse> createUser(@RequestBody CreateUserRequest userRequest) throws Exception {
        UserResponse userResponse = adminService.createUser(userRequest);
        return ResponseEntity.ok(userResponse);
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = adminService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse user = adminService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/users")
    public ResponseEntity<UserResponse> updateUser(@RequestBody UpdateUserRequest updateUserRequest) {
        UserResponse userResponse = adminService.updateUser(updateUserRequest);
        return ResponseEntity.ok(userResponse);
    }

    @DeleteMapping("/users")
    public ResponseEntity<Void> deleteAllUsers() {
        adminService.deleteAllUsers();
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // COURSES

    @PostMapping("/courses")
    public ResponseEntity<CourseResponse> createCourse(@RequestBody CreateCourseRequest courseRequest) throws Exception {
        CourseResponse courseResponse = adminService.createCourse(courseRequest);
        return ResponseEntity.ok(courseResponse);
    }

    @GetMapping("/courses")
    public ResponseEntity<List<CourseResponse>> getAllCourses() {
        List<CourseResponse> courses = adminService.getAllCourses();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/courses/{id}")
    public ResponseEntity<CourseResponse> getCourseById(@PathVariable Long id) {
        CourseResponse course = adminService.getCourseById(id);
        return ResponseEntity.ok(course);
    }

    @PutMapping("/courses")
    public ResponseEntity<CourseResponse> updateCourse(@RequestBody UpdateCourseRequest courseRequest) {
        CourseResponse courseResponse = adminService.updateCourse(courseRequest);
        return ResponseEntity.ok(courseResponse);
    }

    @DeleteMapping("/courses")
    public ResponseEntity<Void> deleteAllCourses() {
        adminService.deleteAllCourses();
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/courses/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        adminService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }

    // LESSONS

    @PostMapping("/lessons")
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

        LessonResponse lessonResponse = adminService.createLesson(lessonRequest);
        return ResponseEntity.ok(lessonResponse);
    }

    @GetMapping("/lessons")
    public ResponseEntity<List<LessonResponse>> getAllLessons() {
        List<LessonResponse> lessons = adminService.getAllLessons();
        return ResponseEntity.ok(lessons);
    }

    @GetMapping("/lessons/{id}")
    public ResponseEntity<LessonResponse> getLessonById(@PathVariable Long id) {
        LessonResponse lesson = adminService.getLessonById(id);
        return ResponseEntity.ok(lesson);
    }

    @PutMapping("/lessons")
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

        LessonResponse lessonResponse = adminService.updateLesson(lessonRequest);
        return ResponseEntity.ok(lessonResponse);
    }

    @DeleteMapping("/lessons")
    public ResponseEntity<Void> deleteAllLessons() {
        adminService.deleteAllLessons();
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/lessons/{id}")
    public ResponseEntity<Void> deleteLesson(@PathVariable Long id) {
        adminService.deleteLesson(id);
        return ResponseEntity.noContent().build();
    }

    // QUIZZES

    @PostMapping("/quizzes")
    public ResponseEntity<QuizResponse> createQuiz(@RequestBody CreateQuizRequest quizRequest) {
        QuizResponse quizResponse = adminService.createQuiz(quizRequest);
        return ResponseEntity.ok(quizResponse);
    }

    @GetMapping("/quizzes")
    public ResponseEntity<List<QuizResponse>> getAllQuizzes() {
        List<QuizResponse> quizzes = adminService.getAllQuizzes();
        return ResponseEntity.ok(quizzes);
    }

    @GetMapping("/quizzes/{id}")
    public ResponseEntity<QuizResponse> getQuizById(@PathVariable Long id) {
        QuizResponse quiz = adminService.getQuizById(id);
        return ResponseEntity.ok(quiz);
    }

    @PutMapping("/quizzes/{id}")
    public ResponseEntity<QuizResponse> updateQuiz(@RequestBody UpdateQuizRequest quizRequest, @PathVariable Long id) {
        QuizResponse quizResponse = adminService.updateQuiz(quizRequest, id);
        return ResponseEntity.ok(quizResponse);
    }

    @DeleteMapping("/quizzes")
    public ResponseEntity<Void> deleteAllQuizzes() {
        adminService.deleteAllQuizzes();
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/quizzes/{id}")
    public ResponseEntity<Void> deleteQuiz(@PathVariable Long id) {
        adminService.deleteQuiz(id);
        return ResponseEntity.noContent().build();
    }
}