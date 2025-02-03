package com.example.demo.api.admin;

import com.example.demo.api.admin.adminModels.*;
import com.example.demo.api.answer.Answer;
import com.example.demo.api.answer.AnswerRepository;
import com.example.demo.api.course.Course;
import com.example.demo.api.course.CourseRepository;
import com.example.demo.api.enrollment.Enrollment;
import com.example.demo.api.enrollment.EnrollmentRepository;
import com.example.demo.api.file.File;
import com.example.demo.api.file.FileRepository;
import com.example.demo.api.lesson.Lesson;
import com.example.demo.api.lesson.LessonRepository;
import com.example.demo.api.question.Question;
import com.example.demo.api.question.QuestionRepository;
import com.example.demo.api.questionType.QuestionType;
import com.example.demo.api.questionType.QuestionTypeRepository;
import com.example.demo.api.quiz.Quiz;
import com.example.demo.api.quiz.QuizRepository;
import com.example.demo.api.user.User;
import com.example.demo.api.user.UserRepository;
import com.example.demo.api.userRole.UserRole;
import com.example.demo.api.userRole.UserRoleRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminService {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final QuestionTypeRepository questionTypeRepository;
    private final AnswerRepository answerRepository;
    private final LessonRepository lessonRepository;
    private final FileRepository fileRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminService(
            UserRepository userRepository,
            UserRoleRepository userRoleRepository,
            CourseRepository courseRepository,
            EnrollmentRepository enrollmentRepository,
            QuizRepository quizRepository,
            QuestionRepository questionRepository,
            QuestionTypeRepository questionTypeRepository,
            AnswerRepository answerRepository,
            LessonRepository lessonRepository,
            FileRepository fileRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.questionTypeRepository = questionTypeRepository;
        this.answerRepository = answerRepository;
        this.lessonRepository = lessonRepository;
        this.fileRepository = fileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // USERS

    public UserResponse createUser(CreateUserRequest userRequest) throws Exception {
        if (userRepository.findByEmail(userRequest.getEmail()).isPresent()) {
            throw new Exception("User with email: " + userRequest.getEmail() + " already exists");
        }

        String slug = userRequest.getSlug()
                .orElseGet(() -> userRequest.getFirstName().toLowerCase() + "-" + userRequest.getLastName().toLowerCase());

        int num = 1;
        while (userRepository.findBySlug(slug).isPresent()) {
            slug = userRequest.getFirstName().toLowerCase() + "-" + userRequest.getLastName().toLowerCase() + "-" + num++;
        }

        List<UserRole> userRoles = userRoleRepository.findAllById(userRequest.getUserRoleIds());
        User user = User.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .email(userRequest.getEmail())
                .slug(slug)
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .dateOfBirth(userRequest.getDateOfBirth())
                .gender(userRequest.getGender())
                .address(userRequest.getAddress().orElse(null))
                .isActive(userRequest.getIsActive())
                .profilePicture(userRequest.getProfilePicture().orElse(null))
                .phoneNumber(userRequest.getPhoneNumber().orElse(null))
                .instagram(userRequest.getInstagram().orElse(null))
                .linkedIn(userRequest.getLinkedIn().orElse(null))
                .userRoles(userRoles)
                .build();
        userRepository.save(user);
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .slug(user.getSlug())
                .dateOfBirth(user.getDateOfBirth())
                .gender(user.getGender())
                .address(user.getAddress())
                .isActive(user.getIsActive())
                .profilePicture(user.getProfilePicture())
                .phoneNumber(user.getPhoneNumber())
                .instagram(user.getInstagram())
                .linkedIn(user.getLinkedIn())
                .userRoles(user.getUserRoles())
                .createdAt(user.getCreatedAt())
                .build();
    }

    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> UserResponse.builder()
                        .id(user.getId())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .email(user.getEmail())
                        .slug(user.getSlug())
                        .dateOfBirth(user.getDateOfBirth())
                        .gender(user.getGender())
                        .address(user.getAddress())
                        .isActive(user.getIsActive())
                        .profilePicture(user.getProfilePicture())
                        .phoneNumber(user.getPhoneNumber())
                        .instagram(user.getInstagram())
                        .linkedIn(user.getLinkedIn())
                        .userRoles(user.getUserRoles())
                        .createdAt(user.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    // Get user by ID
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow();
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .slug(user.getSlug())
                .dateOfBirth(user.getDateOfBirth())
                .gender(user.getGender())
                .address(user.getAddress())
                .isActive(user.getIsActive())
                .profilePicture(user.getProfilePicture())
                .phoneNumber(user.getPhoneNumber())
                .instagram(user.getInstagram())
                .linkedIn(user.getLinkedIn())
                .userRoles(user.getUserRoles())
                .createdAt(user.getCreatedAt())
                .build();
    }

    public UserResponse updateUser(UpdateUserRequest updateUserInput) {
        Optional<User> userOptional = userRepository.findById(updateUserInput.getId());

        if (userOptional.isPresent()) {
            User existingUser = userOptional.get();

            updateUserInput.getFirstName().ifPresent(existingUser::setFirstName);
            updateUserInput.getLastName().ifPresent(existingUser::setLastName);
            updateUserInput.getEmail().ifPresent(existingUser::setEmail);
            updateUserInput.getDateOfBirth().ifPresent(existingUser::setDateOfBirth);
            updateUserInput.getGender().ifPresent(existingUser::setGender);
            updateUserInput.getAddress().ifPresent(existingUser::setAddress);
            updateUserInput.getIsActive().ifPresent(existingUser::setIsActive);
            updateUserInput.getProfilePicture().ifPresent(existingUser::setProfilePicture);
            updateUserInput.getPhoneNumber().ifPresent(existingUser::setPhoneNumber);
            updateUserInput.getInstagram().ifPresent(existingUser::setInstagram);
            updateUserInput.getLinkedIn().ifPresent(existingUser::setLinkedIn);

            updateUserInput.getPassword().ifPresent(password -> existingUser.setPassword(passwordEncoder.encode(password)));

            updateUserInput.getSlug().ifPresent(slugInput -> {
                String slug = slugInput;
                int num = 1;
                while (userRepository.findBySlug(slug).isPresent()) {
                    slug = slugInput + "-" + num++;
                }
                existingUser.setSlug(slug);
            });

            if (updateUserInput.getUserRoleIds().isPresent()) {
                List<UserRole> userRoles = userRoleRepository.findAllById(updateUserInput.getUserRoleIds().get());
                if (!userRoles.isEmpty()) {
                    existingUser.setUserRoles(userRoles);
                }
            }

            userRepository.save(existingUser);

            return UserResponse.builder()
                    .id(existingUser.getId())
                    .firstName(existingUser.getFirstName())
                    .lastName(existingUser.getLastName())
                    .email(existingUser.getEmail())
                    .slug(existingUser.getSlug())
                    .dateOfBirth(existingUser.getDateOfBirth())
                    .gender(existingUser.getGender())
                    .address(existingUser.getAddress())
                    .isActive(existingUser.getIsActive())
                    .profilePicture(existingUser.getProfilePicture())
                    .phoneNumber(existingUser.getPhoneNumber())
                    .instagram(existingUser.getInstagram())
                    .linkedIn(existingUser.getLinkedIn())
                    .userRoles(existingUser.getUserRoles())
                    .createdAt(existingUser.getCreatedAt())
                    .build();
        }

        return null;
    }

    public void deleteAllUsers() {
        userRepository.deleteAll();
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // COURSES

    public CourseResponse createCourse(CreateCourseRequest courseRequest) throws Exception {
        User user = userRepository.findById(courseRequest.getOwnerId()).orElseThrow();

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

            courseRequest.getOwnerId().ifPresent(ownerId -> {
                User newOwner = userRepository.findById(ownerId).orElseThrow();
                Optional<Enrollment> currentOwnerEnrollment = enrollmentRepository.findOwnerEnrollmentByCourseId(existingCourse.getId());
                if(currentOwnerEnrollment.isEmpty()) {
                    enrollmentRepository.save(
                            Enrollment.builder()
                                    .course(existingCourse)
                                    .user(newOwner)
                                    .isInstructor(true)
                                    .build()
                    );
                } else {
                    currentOwnerEnrollment.get().setUser(newOwner);
                    enrollmentRepository.save(currentOwnerEnrollment.get());
                }
            });

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

    //LESSONS

    public LessonResponse createLesson(CreateLessonRequest lessonRequest) throws IOException {
        Course course = courseRepository.findById(lessonRequest.getCourseId()).orElseThrow();

        Lesson lesson = Lesson.builder()
                .title(lessonRequest.getTitle())
                .course(course)
                .build();

        Lesson savedLesson = lessonRepository.save(lesson);

        List<File> files = lessonRequest.getFiles().stream()
                .map(fileRequest -> {
                    try {
                        return saveFile(fileRequest.getFile(), savedLesson.getId());
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to save file", e);
                    }
                })
                .collect(Collectors.toList());

        return LessonResponse.builder()
                .id(savedLesson.getId())
                .title(savedLesson.getTitle())
                .course(savedLesson.getCourse())
                .createdAt(savedLesson.getCreatedAt())
                .files(files)
                .build();
    }

    private File saveFile(MultipartFile fileRequest, Long lessonId) throws IOException {
        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow();

        File file = File.builder()
                .name(fileRequest.getOriginalFilename())
                .type(fileRequest.getContentType())
                .data(fileRequest.getBytes())
                .lesson(lesson)
                .build();

        return fileRepository.save(file);
    }

    public List<LessonResponse> getAllLessons() {
        List<Lesson> lessons = lessonRepository.findAll();
        return lessons.stream()
                .map(lesson -> LessonResponse.builder()
                        .id(lesson.getId())
                        .title(lesson.getTitle())
                        .course(lesson.getCourse())
                        .createdAt(lesson.getCreatedAt())
//                        .files(fileRepository.findFilesByLessonId(lesson.getId()))
                        .build())
                .collect(Collectors.toList());
    }

    public LessonResponse getLessonById(Long id) {
        Lesson lesson = lessonRepository.findById(id).orElseThrow();
        List<File> files = fileRepository.findFilesByLessonId(lesson.getId());
        return LessonResponse.builder()
                .id(lesson.getId())
                .title(lesson.getTitle())
                .course(lesson.getCourse())
                .createdAt(lesson.getCreatedAt())
                .files(files)
                .build();
    }

    public LessonResponse updateLesson(UpdateLessonRequest lessonRequest) throws IOException {
        Optional<Lesson> lessonOptional = lessonRepository.findById(lessonRequest.getId());

        if (lessonOptional.isPresent()) {
            Lesson existingLesson = lessonOptional.get();

            lessonRequest.getCourseId().ifPresent(courseId -> {
                Course course = courseRepository.findById(courseId).orElseThrow();
                existingLesson.setCourse(course);
            });
            lessonRequest.getTitle().ifPresent(existingLesson::setTitle);

            List<File> files = fileRepository.findFilesByLessonId(existingLesson.getId());

            if (lessonRequest.getFiles().isPresent()) {
//                files = fileRepository.findFilesByLessonId(existingLesson.getId());
                for (File file : files) {
                    fileRepository.delete(file);
                }
                files = lessonRequest.getFiles().get().stream()
                        .map(fileRequest -> {
                            try {
                                return saveFile(fileRequest.getFile(), existingLesson.getId());
                            } catch (IOException e) {
                                throw new RuntimeException("Failed to save file", e);
                            }
                        })
                        .collect(Collectors.toList());
            }

            Lesson savedLesson = lessonRepository.save(existingLesson);

            return LessonResponse.builder()
                    .id(savedLesson.getId())
                    .title(savedLesson.getTitle())
                    .course(savedLesson.getCourse())
                    .createdAt(savedLesson.getCreatedAt())
                    .files(files)
                    .build();
        }

        return null;
    }

    public void deleteAllLessons() {
        lessonRepository.deleteAll();
    }

    public void deleteLesson(Long id) {
        // Find the lesson by ID

        // Remove the lesson reference from the files
        fileRepository.deleteFilesByLessonId(id);

        // Delete the lesson
        lessonRepository.deleteById(id);
    }

    // QUIZZES

    @Transactional
    public QuizResponse createQuiz(CreateQuizRequest quizRequest) {
        Course course = courseRepository.findById(quizRequest.getCourseId().orElseThrow())
                .orElseThrow(() -> new RuntimeException("Course not found"));

        Quiz quiz = Quiz.builder()
                .title(quizRequest.getTitle())
                .course(course)
                .build();
        Quiz savedQuiz = quizRepository.save(quiz);

        List<Question> questions = quizRequest.getQuestions().stream()
                .map(questionRequest -> createQuestion(questionRequest, savedQuiz))
                .toList();

        return QuizResponse.builder()
                .id(savedQuiz.getId())
                .title(savedQuiz.getTitle())
                .course(savedQuiz.getCourse())
                .createdAt(savedQuiz.getCreatedAt())
                .questions(questions.stream()
                        .map(this::mapToQuestionResponse)
                        .collect(Collectors.toList()))
                .build();
    }

    private Question createQuestion(QuestionRequest questionRequest, Quiz quiz) {
        QuestionType questionType = questionTypeRepository.findById(questionRequest.getQuestionTypeId())
                .orElseThrow(() -> new RuntimeException("Question type not found"));

        Question question = Question.builder()
                .title(questionRequest.getTitle())
                .questionType(questionType)
                .quiz(quiz)
                .build();
        Question savedQuestion = questionRepository.save(question);

        List<Answer> answers = questionRequest.getAnswers().stream()
                .map(answerRequest -> createAnswer(answerRequest, savedQuestion))
                .toList();

        return savedQuestion;
    }

    private Answer createAnswer(AnswerRequest answerRequest, Question question) {
        Answer answer = Answer.builder()
                .title(answerRequest.getTitle())
                .isCorrect(answerRequest.getIsCorrect())
                .question(question)
                .build();
        return answerRepository.save(answer);
    }

    private QuestionResponse mapToQuestionResponse(Question question) {
        return QuestionResponse.builder()
                .id(question.getId())
                .title(question.getTitle())
                .questionType(question.getQuestionType())
                .answers(answerRepository.findAnswersByQuestionId(question.getId()))
                .build();
    }

    public QuizResponse getQuizById(Long id) {
        Quiz quiz = quizRepository.findById(id).orElseThrow(() -> new RuntimeException("Quiz not found"));
        return mapToQuizResponse(quiz);
    }

    public List<QuizResponse> getAllQuizzes() {
        List<Quiz> quizzes = quizRepository.findAll();
        return quizzes.stream()
                .map(this::mapToQuizResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public QuizResponse updateQuiz(UpdateQuizRequest quizRequest, Long quizId) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new RuntimeException("Quiz not found"));

        quizRequest.getTitle().ifPresent(quiz::setTitle);
        quizRequest.getCourseId().ifPresent(courseId -> {
            Course course = courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("Course not found"));
            quiz.setCourse(course);
        });

        if (quizRequest.getQuestions().isPresent()) {
            List<QuestionRequest> newQuestions = quizRequest.getQuestions().get();
            List<Question> existingQuestions = questionRepository.findQuestionsByQuizId(quiz.getId());

            Map<String, Question> existingQuestionsMap = existingQuestions.stream()
                    .collect(Collectors.toMap(Question::getTitle, question -> question));

            Set<Long> questionsToDelete = new HashSet<>(existingQuestions.stream().map(Question::getId).collect(Collectors.toSet()));

            for (QuestionRequest questionRequest : newQuestions) {
                Question existingQuestion = existingQuestionsMap.get(questionRequest.getTitle());
                if (existingQuestion != null) {
                    updateQuestion(existingQuestion, questionRequest);
                    questionsToDelete.remove(existingQuestion.getId());
                } else {
                    createQuestion(questionRequest, quiz);
                }
            }

            for (Long questionId : questionsToDelete) {
                questionRepository.deleteById(questionId);
            }
        }

        Quiz savedQuiz = quizRepository.save(quiz);

        List<QuestionResponse> questionResponses = questionRepository.findQuestionsByQuizId(savedQuiz.getId()).stream()
                .map(this::mapToQuestionResponse)
                .collect(Collectors.toList());

        return QuizResponse.builder()
                .id(savedQuiz.getId())
                .title(savedQuiz.getTitle())
                .course(savedQuiz.getCourse())
                .createdAt(savedQuiz.getCreatedAt())
                .questions(questionResponses)
                .build();
    }

    private void updateQuestion(Question existingQuestion, QuestionRequest questionRequest) {
        existingQuestion.setTitle(questionRequest.getTitle());
        QuestionType questionType = questionTypeRepository.findById(questionRequest.getQuestionTypeId())
                .orElseThrow(() -> new RuntimeException("Question type not found"));
        existingQuestion.setQuestionType(questionType);

        List<AnswerRequest> newAnswers = questionRequest.getAnswers();
        List<Answer> existingAnswers = answerRepository.findAnswersByQuestionId(existingQuestion.getId());

        // Map of existing answers by title for fast lookup
        Map<String, Answer> existingAnswersMap = existingAnswers.stream()
                .collect(Collectors.toMap(Answer::getTitle, answer -> answer));

        // Keep track of answers to delete
        Set<Long> answersToDelete = new HashSet<>(existingAnswers.stream().map(Answer::getId).collect(Collectors.toSet()));

        for (AnswerRequest answerRequest : newAnswers) {
            Answer existingAnswer = existingAnswersMap.get(answerRequest.getTitle());
            if (existingAnswer != null) {
                existingAnswer.setIsCorrect(answerRequest.getIsCorrect());
                answersToDelete.remove(existingAnswer.getId());
            } else {
                createAnswer(answerRequest, existingQuestion);
            }
        }

        // Delete answers that are not in the new list
        for (Long answerId : answersToDelete) {
            answerRepository.deleteById(answerId);
        }

        questionRepository.save(existingQuestion);
    }

    @Transactional
    public void deleteQuiz(Long id) {
        Quiz quiz = quizRepository.findById(id).orElseThrow(() -> new RuntimeException("Quiz not found"));
        // Delete associated questions and answers
        List<Question> questions = questionRepository.findQuestionsByQuizId(quiz.getId());
        for (Question question : questions) {
            answerRepository.deleteAnswersByQuestionId(question.getId());
        }
        questionRepository.deleteQuestionsByQuizId(quiz.getId());
        quizRepository.deleteQuizByCourseId(quiz.getCourse().getId());
    }

    @Transactional
    public void deleteAllQuizzes() {
        // Delete all quizzes and their associated questions and answers
        List<Quiz> quizzes = quizRepository.findAll();
        for (Quiz quiz : quizzes) {
            List<Question> questions = questionRepository.findQuestionsByQuizId(quiz.getId());
            for (Question question : questions) {
                answerRepository.deleteAnswersByQuestionId(question.getId());
            }
            questionRepository.deleteById(quiz.getId());
        }
        quizRepository.deleteAll();
    }

    private QuizResponse mapToQuizResponse(Quiz quiz) {
        List<QuestionResponse> questionResponses = questionRepository.findQuestionsByQuizId(quiz.getId()).stream()
                .map(this::mapToQuestionResponse)
                .collect(Collectors.toList());

        return QuizResponse.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .course(quiz.getCourse())
                .createdAt(quiz.getCreatedAt())
                .questions(questionResponses)
                .build();
    }



}
