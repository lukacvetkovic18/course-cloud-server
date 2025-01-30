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

    public List<Course> getMyEnrolledCourses() {
        String token = getBearerTokenHeader();
        String email = jwtService.extractUsername(token);
        User user = userRepository.findByEmail(email).orElseThrow();
        return courseRepository.findEnrolledCoursesOfUser(user.getId());
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

    public List<Course> getCourseSearchResults(String query) {
        return courseRepository.findCoursesBySearchQuery(query);
    }

    public List<Course> getRandomCourses() {
        // Create a Pageable object to limit the result to 3 courses
        Pageable pageable = PageRequest.of(0, 3); // 0 for the first page, 3 for the number of courses to fetch
        return courseRepository.findRandomCourses(pageable);
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
