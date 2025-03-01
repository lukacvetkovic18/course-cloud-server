package com.example.demo.api.enrollment;

import com.example.demo.api.course.Course;
import com.example.demo.api.course.CourseRepository;
import com.example.demo.api.enrollment.enrollmentModels.CreateEnrollmentRequest;
import com.example.demo.api.enrollment.enrollmentModels.UpdateEnrollmentRequest;
import com.example.demo.api.user.User;
import com.example.demo.api.user.UserRepository;
import com.example.demo.config.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.example.demo.api.user.UserService.getBearerTokenHeader;

@Service
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final JwtService jwtService;

    @Autowired
    public EnrollmentService(
            EnrollmentRepository enrollmentRepository,
            UserRepository userRepository,
            CourseRepository courseRepository,
            JwtService jwtService
    ) {
        this.enrollmentRepository = enrollmentRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.jwtService = jwtService;
    }

    public Enrollment createEnrollment(CreateEnrollmentRequest enrollmentRequest) {
        User user = userRepository.findById(enrollmentRequest.getUserId()).orElseThrow();
        Course course = courseRepository.findById(enrollmentRequest.getCourseId()).orElseThrow();

        Enrollment enrollment = Enrollment.builder()
                .user(user)
                .course(course)
                .isInstructor(enrollmentRequest.getIsInstructor())
                .build();
        return enrollmentRepository.save(enrollment);
    }

    public List<Enrollment> getAllEnrollments() {
        return enrollmentRepository.findAll();
    }

    public Optional<Enrollment> getEnrollmentById(Long id) {
        return enrollmentRepository.findById(id);
    }

    public Enrollment updateEnrollment(UpdateEnrollmentRequest enrollmentRequest) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentRequest.getId()).orElseThrow();

        if(enrollmentRequest.getUserId() != null) {
            User user = userRepository.findById(enrollmentRequest.getUserId().get()).orElseThrow();
            enrollment.setUser(user);
        }
        if(enrollmentRequest.getCourseId() != null) {
            Course course = courseRepository.findById(enrollmentRequest.getCourseId().get()).orElseThrow();
            enrollment.setCourse(course);
        }
        if(enrollmentRequest.getIsInstructor() != null) enrollment.setIsInstructor(enrollmentRequest.getIsInstructor().get());

        return enrollmentRepository.save(enrollment);
    }

    public void deleteAllEnrollments() {
        enrollmentRepository.deleteAll();
    }

    public void deleteEnrollment(Long id) {
        enrollmentRepository.deleteById(id);
    }

    public Enrollment enrollToCourse(Long courseId) throws Exception {
        String token = getBearerTokenHeader();
        String email = jwtService.extractUsername(token);
        User user = userRepository.findByEmail(email).orElseThrow();
        Course course = courseRepository.findById(courseId).orElseThrow();

        if(enrollmentRepository.findEnrollmentByUserIdAndCourseId(user.getId(), courseId).isPresent()){
            throw new Exception("User is already enrolled to this course.");
        }

        Enrollment enrollment = Enrollment.builder()
                .user(user)
                .course(course)
                .isInstructor(false)
                .build();
        return enrollmentRepository.save(enrollment);
    }

    public void removeEnrollmentToCourse(Long courseId) throws Exception {
        String token = getBearerTokenHeader();
        String email = jwtService.extractUsername(token);
        User user = userRepository.findByEmail(email).orElseThrow();
        Course course = courseRepository.findById(courseId).orElseThrow();

        Optional<Enrollment> enrollment = enrollmentRepository.findEnrollmentByUserIdAndCourseId(user.getId(), courseId);

        if(enrollment.isEmpty()){
            throw new Exception("User is not enrolled to this course.");
        } else {
            enrollmentRepository.deleteById(enrollment.get().getId());
        }
    }

}
