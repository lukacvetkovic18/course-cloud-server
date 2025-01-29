package com.example.demo.api.admin;

import com.example.demo.api.admin.adminModels.*;
import com.example.demo.api.course.Course;
import com.example.demo.api.course.CourseRepository;
import com.example.demo.api.enrollment.Enrollment;
import com.example.demo.api.enrollment.EnrollmentRepository;
import com.example.demo.api.file.File;
import com.example.demo.api.file.FileRepository;
import com.example.demo.api.lesson.Lesson;
import com.example.demo.api.lesson.LessonRepository;
import com.example.demo.api.quiz.Quiz;
import com.example.demo.api.quiz.QuizRepository;
import com.example.demo.api.user.User;
import com.example.demo.api.user.UserRepository;
import com.example.demo.api.userRole.UserRole;
import com.example.demo.api.userRole.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AdminService {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final QuizRepository quizRepository;
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
            LessonRepository lessonRepository,
            FileRepository fileRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.quizRepository = quizRepository;
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

        Course course = courseRepository.save(Course.builder()
                .title(courseRequest.getTitle())
                .shortDescription(courseRequest.getShortDescription())
                .description(courseRequest.getDescription())
                .isActive(courseRequest.getIsActive())
                .image(courseRequest.getImage().orElse(null))
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

            courseRequest.getTitle().ifPresent(existingCourse::setTitle);
            courseRequest.getShortDescription().ifPresent(existingCourse::setShortDescription);
            courseRequest.getDescription().ifPresent(existingCourse::setDescription);
            courseRequest.getIsActive().ifPresent(existingCourse::setIsActive);
            courseRequest.getImage().ifPresent(existingCourse::setImage);

            courseRequest.getOwnerId().ifPresent(ownerId -> {
                User newOwner = userRepository.findById(ownerId).orElseThrow();
                Enrollment currentOwnerEnrollment = enrollmentRepository.findOwnerEnrollmentByCourseId(existingCourse.getId());
                currentOwnerEnrollment.setUser(newOwner);
                enrollmentRepository.save(currentOwnerEnrollment);
            });

            courseRepository.save(existingCourse);

            return CourseResponse.builder()
                    .id(existingCourse.getId())
                    .title(existingCourse.getTitle())
                    .shortDescription(existingCourse.getShortDescription())
                    .description(existingCourse.getDescription())
                    .isActive(existingCourse.getIsActive())
                    .image(existingCourse.getImage())
                    .owner(enrollmentRepository.findOwnerEnrollmentByCourseId(existingCourse.getId()).getUser())
                    .createdAt(existingCourse.getCreatedAt())
                    .build();
        }

        return null;
    }

    public void deleteAllCourses() {
        courseRepository.deleteAll();
    }

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
                        .files(fileRepository.findFilesByLessonId(lesson.getId()))
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

    public void deleteLesson(Long id) {
        // Find the lesson by ID

        // Remove the lesson reference from the files
        fileRepository.deleteFilesByLessonId(id);

        // Delete the lesson
        lessonRepository.deleteById(id);
    }



}
