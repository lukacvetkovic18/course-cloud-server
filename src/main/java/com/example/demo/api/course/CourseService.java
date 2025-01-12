package com.example.demo.api.course;

import com.example.demo.api.course.courseModels.CreateCourseRequest;
import com.example.demo.api.course.courseModels.UpdateCourseRequest;
import com.example.demo.api.user.User;
import com.example.demo.api.user.UserRepository;
import com.example.demo.config.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.example.demo.api.user.UserService.getBearerTokenHeader;

@Service
public class CourseService {
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Autowired
    public CourseService(CourseRepository courseRepository, UserRepository userRepository, JwtService jwtService) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    public Course createEmptyCourse() {
        Course course = Course.builder()
                .isActive(false)
                .build();
        return courseRepository.save(course);
    }

    public Course createCourse(CreateCourseRequest courseRequest) {
        Course course = Course.builder()
                .title(courseRequest.getTitle())
                .description(courseRequest.getDescription())
                .isActive(courseRequest.getIsActive())
                .duration(courseRequest.getDuration())
                .build();
        return courseRepository.save(course);
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Optional<Course> getCourseById(Long id) {
        return courseRepository.findById(id);
    }

    public Course updateCourse(UpdateCourseRequest courseRequest) {
        Course course = courseRepository.findById(courseRequest.getId()).orElseThrow();

        if(courseRequest.getTitle() != null) course.setTitle(courseRequest.getTitle().get());
        if(courseRequest.getDescription() != null) course.setDescription(courseRequest.getDescription().get());
        if(courseRequest.getIsActive() != null) course.setIsActive(courseRequest.getIsActive().get());
        if(courseRequest.getDuration() != null) course.setDuration(courseRequest.getDuration().get());

        return courseRepository.save(course);
    }

    public void deleteAllCourses() {
        courseRepository.deleteAll();
    }

    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }

    public List<Course> getInstructorCourses(Long userId) {
        return courseRepository.findCoursesOfInstructor(userId);
    }

    public List<Course> getMyCourses() {
        String token = getBearerTokenHeader();
        String email = jwtService.extractUsername(token);
        User user = userRepository.findByEmail(email).orElseThrow();
        return courseRepository.findCoursesOfInstructor(user.getId());
    }

    public boolean isUserEnrolledInCourse(Long courseId) {
        String token = getBearerTokenHeader();
        String email = jwtService.extractUsername(token);
        User user = userRepository.findByEmail(email).orElseThrow();
        return courseRepository.isUserEnrolledInCourse(user.getId(), courseId);
    }

    public boolean isUserOwnerOfCourse(Long courseId) {
        String token = getBearerTokenHeader();
        String email = jwtService.extractUsername(token);
        User user = userRepository.findByEmail(email).orElseThrow();
        return courseRepository.isUserOwnerOfCourse(user.getId(), courseId);
    }

    public User getOwnerOfCourse(Long courseId) {
        return courseRepository.findOwnerOfCourse(courseId);
    }
}
