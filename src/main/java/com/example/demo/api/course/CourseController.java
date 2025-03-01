package com.example.demo.api.course;

import com.example.demo.api.course.courseModels.BulkCourseRequest;
import com.example.demo.api.course.courseModels.CourseResponse;
import com.example.demo.api.course.courseModels.CreateCourseRequest;
import com.example.demo.api.course.courseModels.UpdateCourseRequest;
import com.example.demo.api.user.User;
import com.example.demo.api.user.userModels.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/api/courses")
public class CourseController {

    private final CourseService courseService;

    @Autowired
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping(path = "/empty")
    public Course createEmptyCourse() {
        return courseService.createEmptyCourse();
    }

    @PostMapping
    public CourseResponse createCourse(@RequestBody CreateCourseRequest course) {
        return courseService.createCourse(course);
    }

    @GetMapping
    public List<CourseResponse> getAllCourses() { return courseService.getAllCourses(); }

    @GetMapping(path = "/{id}")
    public CourseResponse getCourseById(@PathVariable("id") long id) {
        return courseService.getCourseById(id);
    }

    @PutMapping
    public CourseResponse updateCourse(@RequestBody UpdateCourseRequest course) {
        return courseService.updateCourse(course);
    }

    @DeleteMapping
    public void deleteAllCourses() {
        courseService.deleteAllCourses();
    }

    @DeleteMapping(path = "/{id}")
    public void deleteCourse(@PathVariable("id") long id) {
        courseService.deleteCourse(id);
    }

    @GetMapping(path = "/instructor/{id}")
    public List<CourseResponse> getInstructorCourses(@PathVariable("id") long id) {
        return courseService.getInstructorCourses(id);
    }

    @GetMapping(path = "/instructor/logged-in")
    public List<CourseResponse> getMyCourses() {
        return courseService.getMyCourses();
    }

    @GetMapping(path = "/student/logged-in")
    public List<CourseResponse> getMyEnrolledCourses() {
        return courseService.getMyEnrolledCourses();
    }

    @GetMapping(path = "/is-enrolled/{id}")
    public boolean isUserEnrolledInCourse(@PathVariable("id") long id) {
        return courseService.isUserEnrolledInCourse(id);
    }

    @GetMapping(path = "/is-instructor/{id}")
    public boolean isUserOwnerOfCourse(@PathVariable("id") long id) {
        return courseService.isUserOwnerOfCourse(id);
    }

    @GetMapping(path = "/owner/{id}")
    public User getOwnerOfCourse(@PathVariable("id") long id) {
        return courseService.getOwnerOfCourse(id);
    }

    @GetMapping(path = "/students/{id}")
    public List<User> getStudentsInCourse(@PathVariable("id") long id) {
        return courseService.getStudentsInCourse(id);
    }

    @GetMapping(path = "/search")
    public List<CourseResponse> getCourseSearchResults(@RequestParam("query") String query) {
        return courseService.getCourseSearchResults(query);
    }

    @GetMapping("/random")
    public List<CourseResponse> getRandomCourses() {
        return courseService.getRandomCourses();
    }

    @GetMapping("/without-quiz")
    public ResponseEntity<List<CourseResponse>> getCoursesWithoutQuiz() {
        List<CourseResponse> courses = courseService.getCoursesWithoutQuiz();
        return ResponseEntity.ok(courses);
    }

    @GetMapping(path = "/slug/{slug}")
    public CourseResponse getCourseBySlug(@PathVariable("slug") String slug) {
        return courseService.getCourseBySlug(slug);
    }

    @PostMapping("/bulk-create")
    public ResponseEntity<Course> createCourseWithLessonsAndQuiz(@RequestBody BulkCourseRequest request) {
        return ResponseEntity.ok(courseService.createCourseWithLessonsAndQuiz(request));
    }
}
