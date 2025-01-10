package com.example.demo.api.course;

import com.example.demo.api.course.courseModels.CreateCourseRequest;
import com.example.demo.api.course.courseModels.UpdateCourseRequest;
import com.example.demo.api.user.User;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping
    public Course createCourse(@RequestBody CreateCourseRequest course) {
        return courseService.createCourse(course);
    }

    @GetMapping
    public List<Course> getAllCourses() { return courseService.getAllCourses(); }

    @GetMapping(path = "/{id}")
    public Optional<Course> getCourseById(@PathVariable("id") long id) {
        return courseService.getCourseById(id);
    }

    @PutMapping
    public Course updateCourse(@RequestBody UpdateCourseRequest course) {
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
    public List<Course> getInstructorCourses(@PathVariable("id") long id) {
        return courseService.getInstructorCourses(id);
    }

    @GetMapping(path = "/instructor/logged-in")
    public List<Course> getMyCourses() {
        return courseService.getMyCourses();
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

}
