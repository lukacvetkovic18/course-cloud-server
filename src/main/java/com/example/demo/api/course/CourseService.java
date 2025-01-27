package com.example.demo.api.course;

import com.example.demo.api.answer.Answer;
import com.example.demo.api.answer.AnswerRepository;
import com.example.demo.api.course.courseModels.CreateCourseRequest;
import com.example.demo.api.course.courseModels.UpdateCourseRequest;
import com.example.demo.api.enrollment.Enrollment;
import com.example.demo.api.enrollment.EnrollmentRepository;
import com.example.demo.api.file.FileRepository;
import com.example.demo.api.lesson.LessonRepository;
import com.example.demo.api.lesson.LessonService;
import com.example.demo.api.question.QuestionRepository;
import com.example.demo.api.quiz.QuizRepository;
import com.example.demo.api.quiz.QuizService;
import com.example.demo.api.user.User;
import com.example.demo.api.user.UserRepository;
import com.example.demo.api.user.UserService;
import com.example.demo.api.user.userModels.UserResponse;
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
    private final EnrollmentRepository enrollmentRepository;
    private final LessonRepository lessonRepository;
    private final QuizRepository quizRepository;
    private final FileRepository fileRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final JwtService jwtService;

    @Autowired
    public CourseService(CourseRepository courseRepository,
                         UserRepository userRepository,
                         EnrollmentRepository enrollmentRepository,
                         LessonRepository lessonRepository,
                         QuizRepository quizRepository,
                         FileRepository fileRepository,
                         QuestionRepository questionRepository,
                         AnswerRepository answerRepository,
                         JwtService jwtService) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.lessonRepository = lessonRepository;
        this.quizRepository = quizRepository;
        this.fileRepository = fileRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.jwtService = jwtService;
    }

    public Course createEmptyCourse() {
        String token = getBearerTokenHeader();
        String email = jwtService.extractUsername(token);
        User user = userRepository.findByEmail(email).orElseThrow();
        Course course = courseRepository.save(Course.builder()
                .isActive(false)
                .build()
        );
        Enrollment enrollment = Enrollment.builder()
                .course(course)
                .user(user)
                .isInstructor(true)
                .build();
        enrollmentRepository.save(enrollment);
        return course;
    }

    public Course createCourse(CreateCourseRequest courseRequest) {
        Course course = Course.builder()
                .title(courseRequest.getTitle())
                .shortDescription(courseRequest.getShortDescription())
                .description(courseRequest.getDescription())
                .isActive(courseRequest.getIsActive())
                .image(courseRequest.getImage())
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
        if(courseRequest.getShortDescription() != null) course.setShortDescription(courseRequest.getShortDescription().get());
        if(courseRequest.getDescription() != null) course.setDescription(courseRequest.getDescription().get());
        if(courseRequest.getIsActive() != null) course.setIsActive(courseRequest.getIsActive().get());
        if(courseRequest.getImage() != null) course.setImage(courseRequest.getImage().get());

        return courseRepository.save(course);
    }

    public void deleteAllCourses() {
        courseRepository.deleteAll();
    }

    public void deleteCourse(Long id) {
        enrollmentRepository.deleteEnrollmentsByCourseId(id);

        fileRepository.deleteFilesByCourseId(id);
        lessonRepository.deleteLessonsByCourseId(id);

        answerRepository.deleteAnswersByCourseId(id);
        questionRepository.deleteQuestionsByCourseId(id);
        quizRepository.deleteQuizByCourseId(id);

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

    public List<Course> getCourseSearchResults(String query) {
        return courseRepository.findCoursesBySearchQuery(query);
    }
}
