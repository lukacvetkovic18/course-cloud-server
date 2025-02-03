package com.example.demo.api.enrollment;

import com.example.demo.api.enrollment.enrollmentModels.CreateEnrollmentRequest;
import com.example.demo.api.enrollment.enrollmentModels.UpdateEnrollmentRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/api/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @Autowired
    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping
    public Enrollment createEnrollment(@RequestBody CreateEnrollmentRequest enrollment) {
        return enrollmentService.createEnrollment(enrollment);
    }

    @GetMapping
    public List<Enrollment> getAllEnrollments() { return enrollmentService.getAllEnrollments(); }

    @GetMapping(path = "/{id}")
    public Optional<Enrollment> getEnrollmentById(@PathVariable("id") long id) {
        return enrollmentService.getEnrollmentById(id);
    }

    @PutMapping
    public Enrollment updateEnrollment(@RequestBody UpdateEnrollmentRequest enrollment) {
        return enrollmentService.updateEnrollment(enrollment);
    }

    @DeleteMapping
    public void deleteAllEnrollments() {
        enrollmentService.deleteAllEnrollments();
    }

    @DeleteMapping(path = "/{id}")
    public void deleteEnrollment(@PathVariable("id") long id) {
        enrollmentService.deleteEnrollment(id);
    }

    @PostMapping(path = "/course/{courseId}")
    public Enrollment enrollToCourse(@PathVariable("courseId") long courseId) throws Exception {
        return enrollmentService.enrollToCourse(courseId);
    }

    @DeleteMapping(path = "/course/{courseId}")
    public void removeEnrollmentToCourse(@PathVariable("courseId") long courseId) throws Exception {
        enrollmentService.removeEnrollmentToCourse(courseId);
    }
}