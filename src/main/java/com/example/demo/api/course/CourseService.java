package com.example.demo.api.course;

import com.example.demo.api.answer.Answer;
import com.example.demo.api.answer.AnswerRepository;
import com.example.demo.api.course.courseModels.*;
import com.example.demo.api.enrollment.Enrollment;
import com.example.demo.api.enrollment.EnrollmentRepository;
import com.example.demo.api.file.File;
import com.example.demo.api.file.FileRepository;
import com.example.demo.api.lesson.Lesson;
import com.example.demo.api.lesson.LessonRepository;
import com.example.demo.api.lesson.LessonService;
import com.example.demo.api.question.Question;
import com.example.demo.api.question.QuestionRepository;
import com.example.demo.api.questionType.QuestionTypeRepository;
import com.example.demo.api.quiz.Quiz;
import com.example.demo.api.quiz.QuizRepository;
import com.example.demo.api.quiz.QuizService;
import com.example.demo.api.user.User;
import com.example.demo.api.user.UserRepository;
import com.example.demo.api.user.UserService;
import com.example.demo.api.user.userModels.UserResponse;
import com.example.demo.config.JwtService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private final QuestionTypeRepository questionTypeRepository;
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
                         QuestionTypeRepository questionTypeRepository,
                         JwtService jwtService) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.lessonRepository = lessonRepository;
        this.quizRepository = quizRepository;
        this.fileRepository = fileRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.questionTypeRepository = questionTypeRepository;
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

    public CourseResponse createCourse(CreateCourseRequest courseRequest) {
        String token = getBearerTokenHeader();
        String email = jwtService.extractUsername(token);
        User user = userRepository.findByEmail(email).orElseThrow();

        String slug = courseRequest.getTitle().toLowerCase().replaceAll("\\s+", "-");

        int num = 1;
        while (courseRepository.findBySlug(slug).isPresent()) {
            slug = courseRequest.getTitle().toLowerCase().replaceAll("\\s+", "-") + "-" + num++;
        }

        Course course = courseRepository.save(Course.builder()
                .title(courseRequest.getTitle())
                .shortDescription(courseRequest.getShortDescription())
                .description(courseRequest.getDescription())
                .isActive(courseRequest.getIsActive())
                .image(courseRequest.getImage().orElse(null))
                .slug(slug)
                .build());

        Enrollment enrollment = Enrollment.builder()
                .course(course)
                .user(user)
                .isInstructor(true)
                .build();
        enrollmentRepository.save(enrollment);

        return CourseResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .shortDescription(course.getShortDescription())
                .description(course.getDescription())
                .isActive(course.getIsActive())
                .image(course.getImage())
                .owner(user)
                .createdAt(course.getCreatedAt())
                .slug(course.getSlug())
                .build();
    }

    public List<CourseResponse> getAllCourses() {
        List<Course> courses = courseRepository.findAll();
        return courses.stream()
                .map(course -> CourseResponse.builder()
                        .id(course.getId())
                        .title(course.getTitle())
                        .shortDescription(course.getShortDescription())
                        .description(course.getDescription())
                        .isActive(course.getIsActive())
                        .image(course.getImage())
                        .owner(courseRepository.findOwnerOfCourse(course.getId()))
                        .createdAt(course.getCreatedAt())
                        .slug(course.getSlug())
                        .build())
                .collect(Collectors.toList());
    }

    public CourseResponse getCourseById(Long id) {
        Course course = courseRepository.findById(id).orElseThrow();
        return CourseResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .shortDescription(course.getShortDescription())
                .description(course.getDescription())
                .isActive(course.getIsActive())
                .image(course.getImage())
                .owner(courseRepository.findOwnerOfCourse(course.getId()))
                .createdAt(course.getCreatedAt())
                .slug(course.getSlug())
                .build();
    }

    public CourseResponse updateCourse(UpdateCourseRequest courseRequest) {
        Optional<Course> courseOptional = courseRepository.findById(courseRequest.getId());

        if (courseOptional.isPresent()) {
            Course existingCourse = courseOptional.get();

            courseRequest.getTitle().ifPresent(newTitle -> {
                if (!newTitle.equals(existingCourse.getTitle())) {
                    String newSlug = newTitle.toLowerCase().replaceAll("\\s+", "-");
                    int num = 1;
                    while (courseRepository.findBySlug(newSlug).isPresent()) {
                        newSlug = newTitle.toLowerCase().replaceAll("\\s+", "-") + "-" + num++;
                    }
                    existingCourse.setSlug(newSlug);
                }
                existingCourse.setTitle(newTitle);
            });
            courseRequest.getShortDescription().ifPresent(existingCourse::setShortDescription);
            courseRequest.getDescription().ifPresent(existingCourse::setDescription);
            courseRequest.getIsActive().ifPresent(existingCourse::setIsActive);
            courseRequest.getImage().ifPresent(existingCourse::setImage);

            courseRepository.save(existingCourse);

            return CourseResponse.builder()
                    .id(existingCourse.getId())
                    .title(existingCourse.getTitle())
                    .shortDescription(existingCourse.getShortDescription())
                    .description(existingCourse.getDescription())
                    .isActive(existingCourse.getIsActive())
                    .image(existingCourse.getImage())
                    .owner(enrollmentRepository.findOwnerEnrollmentByCourseId(existingCourse.getId()).get().getUser())
                    .createdAt(existingCourse.getCreatedAt())
                    .slug(existingCourse.getSlug())
                    .build();
        }

        return null;
    }

    public void deleteAllCourses() {
        courseRepository.deleteAll();
    }

    @Transactional
    public void deleteCourse(Long id) {
        enrollmentRepository.deleteEnrollmentsByCourseId(id);

        List<Lesson> lessons = lessonRepository.findLessonsByCourseId(id);
        for (Lesson lesson : lessons) {
            lesson.setCourse(null);
            lessonRepository.save(lesson);
        }

        Optional<Quiz> quizOptional = quizRepository.findQuizByCourseId(id);
        if (quizOptional.isPresent()) {
            Quiz quiz = quizOptional.get();
            quiz.setCourse(null);
            quizRepository.save(quiz);
        }
        courseRepository.deleteById(id);
    }

    public List<CourseResponse> getInstructorCourses(Long userId) {
        List<Course> courses = courseRepository.findCoursesOfInstructor(userId);
        return courses.stream()
                .map(course -> CourseResponse.builder()
                        .id(course.getId())
                        .title(course.getTitle())
                        .shortDescription(course.getShortDescription())
                        .description(course.getDescription())
                        .isActive(course.getIsActive())
                        .image(course.getImage())
                        .owner(courseRepository.findOwnerOfCourse(course.getId()))
                        .createdAt(course.getCreatedAt())
                        .slug(course.getSlug())
                        .build())
                .collect(Collectors.toList());
    }

    public List<CourseResponse> getMyCourses() {
        String token = getBearerTokenHeader();
        String email = jwtService.extractUsername(token);
        User user = userRepository.findByEmail(email).orElseThrow();
        List<Course> courses = courseRepository.findCoursesOfInstructor(user.getId());
        return courses.stream()
                .map(course -> CourseResponse.builder()
                        .id(course.getId())
                        .title(course.getTitle())
                        .shortDescription(course.getShortDescription())
                        .description(course.getDescription())
                        .isActive(course.getIsActive())
                        .image(course.getImage())
                        .owner(courseRepository.findOwnerOfCourse(course.getId()))
                        .createdAt(course.getCreatedAt())
                        .slug(course.getSlug())
                        .build())
                .collect(Collectors.toList());
    }

    public List<CourseResponse> getMyEnrolledCourses() {
        String token = getBearerTokenHeader();
        String email = jwtService.extractUsername(token);
        User user = userRepository.findByEmail(email).orElseThrow();
        List<Course> courses = courseRepository.findEnrolledCoursesOfUser(user.getId());
        return courses.stream()
                .map(course -> CourseResponse.builder()
                        .id(course.getId())
                        .title(course.getTitle())
                        .shortDescription(course.getShortDescription())
                        .description(course.getDescription())
                        .isActive(course.getIsActive())
                        .image(course.getImage())
                        .owner(courseRepository.findOwnerOfCourse(course.getId()))
                        .createdAt(course.getCreatedAt())
                        .slug(course.getSlug())
                        .build())
                .collect(Collectors.toList());
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

    public List<User> getStudentsInCourse(Long courseId) {
        return courseRepository.findStudentsInCourse(courseId);
    }

    public List<CourseResponse> getCourseSearchResults(String query) {
        List<Course> courses = courseRepository.findCoursesBySearchQuery(query);
        return courses.stream()
                .map(course -> CourseResponse.builder()
                        .id(course.getId())
                        .title(course.getTitle())
                        .shortDescription(course.getShortDescription())
                        .description(course.getDescription())
                        .isActive(course.getIsActive())
                        .image(course.getImage())
                        .owner(courseRepository.findOwnerOfCourse(course.getId()))
                        .createdAt(course.getCreatedAt())
                        .slug(course.getSlug())
                        .build())
                .collect(Collectors.toList());
    }

    public List<CourseResponse> getRandomCourses() {
        Pageable pageable = PageRequest.of(0, 3);
        List<Course> courses = courseRepository.findRandomCourses(pageable);
        return courses.stream()
                .map(course -> CourseResponse.builder()
                        .id(course.getId())
                        .title(course.getTitle())
                        .shortDescription(course.getShortDescription())
                        .description(course.getDescription())
                        .isActive(course.getIsActive())
                        .image(course.getImage())
                        .owner(courseRepository.findOwnerOfCourse(course.getId()))
                        .createdAt(course.getCreatedAt())
                        .slug(course.getSlug())
                        .build())
                .collect(Collectors.toList());
    }
    public List<CourseResponse> getCoursesWithoutQuiz() {
        List<Course> courses = courseRepository.findCoursesWithoutQuiz();
        return courses.stream()
                .map(course -> CourseResponse.builder()
                        .id(course.getId())
                        .title(course.getTitle())
                        .shortDescription(course.getShortDescription())
                        .description(course.getDescription())
                        .isActive(course.getIsActive())
                        .image(course.getImage())
                        .owner(courseRepository.findOwnerOfCourse(course.getId()))
                        .createdAt(course.getCreatedAt())
                        .slug(course.getSlug())
                        .build())
                .collect(Collectors.toList());
    }
    public CourseResponse getCourseBySlug(String slug) {
        Course course = courseRepository.findBySlug(slug).orElseThrow();
        return CourseResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .shortDescription(course.getShortDescription())
                .description(course.getDescription())
                .isActive(course.getIsActive())
                .image(course.getImage())
                .owner(courseRepository.findOwnerOfCourse(course.getId()))
                .createdAt(course.getCreatedAt())
                .slug(course.getSlug())
                .build();
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Transactional
    public Course createCourseWithLessonsAndQuiz(BulkCourseRequest request) {
        // Fetch user from token
        String token = getBearerTokenHeader();
        String email = jwtService.extractUsername(token);
        User user = userRepository.findByEmail(email).orElseThrow();

        // Create course entity
        Course course = courseRepository.save(
                Course.builder()
                        .title(request.getTitle())
                        .shortDescription(request.getShortDescription())
                        .description(request.getDescription())
                        .isActive(request.getIsActive())
                        .image(request.getImage())
                        .build()
        );

        // Enroll user as instructor
        enrollmentRepository.save(
                Enrollment.builder()
                        .course(course)
                        .user(user)
                        .isInstructor(true)
                        .build()
        );

        // Process lessons
        List<Lesson> lessons = new ArrayList<>();
        for (LessonRequest lessonRequest : request.getLessons()) {
            Lesson lesson = Lesson.builder()
                    .title(lessonRequest.getTitle())
                    .course(course)
                    .build();
            lessons.add(lesson);
        }
        lessonRepository.saveAll(lessons);

        // Process lesson files
        for (int i = 0; i < request.getLessons().size(); i++) {
            Lesson lesson = lessons.get(i);
            LessonRequest lessonRequest = request.getLessons().get(i);

            for (FileRequest fileRequest : lessonRequest.getFiles()) {
                File file = File.builder()
                        .name(fileRequest.getName())
                        .type(fileRequest.getType())
                        .data(Base64.getDecoder().decode(fileRequest.getData()))
                        .lesson(lesson)
                        .build();
                fileRepository.save(file);
            }
        }

        // Process quiz
        Quiz quiz = Quiz.builder()
                .title(request.getQuiz().getTitle())
                .course(course)
                .build();
        quiz = quizRepository.save(quiz);

        // Process questions
        List<Question> questions = new ArrayList<>();
        for (QuestionRequest questionRequest : request.getQuiz().getQuestions()) {
            Question question = Question.builder()
                    .title(questionRequest.getTitle())
                    .questionType(questionTypeRepository.findById(questionRequest.getQuestionTypeId()).orElseThrow())
                    .quiz(quiz)
                    .build();
            questions.add(question);
        }
        questionRepository.saveAll(questions);

        // Process answers
        for (int i = 0; i < request.getQuiz().getQuestions().size(); i++) {
            Question question = questions.get(i);
            QuestionRequest questionRequest = request.getQuiz().getQuestions().get(i);

            List<Answer> answers = new ArrayList<>();
            for (AnswerRequest answerRequest : questionRequest.getAnswers()) {
                Answer answer = Answer.builder()
                        .title(answerRequest.getTitle())
                        .isCorrect(answerRequest.getIsCorrect())
                        .question(question)
                        .build();
                answers.add(answer);
            }
            answerRepository.saveAll(answers);
        }

        return course;
    }

}
