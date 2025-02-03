package com.example.demo.api.lesson;

import com.example.demo.api.course.Course;
import com.example.demo.api.course.CourseRepository;
import com.example.demo.api.file.File;
import com.example.demo.api.file.FileRepository;
import com.example.demo.api.lesson.lessonModels.CreateLessonRequest;
import com.example.demo.api.lesson.lessonModels.LessonResponse;
import com.example.demo.api.lesson.lessonModels.UpdateLessonRequest;
import com.example.demo.api.quiz.Quiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LessonService {
    private final LessonRepository lessonRepository;
    private final CourseRepository courseRepository;
    private final FileRepository fileRepository;

    @Autowired
    public LessonService(
            LessonRepository lessonRepository,
            CourseRepository courseRepository,
            FileRepository fileRepository
    ) {
        this.lessonRepository = lessonRepository;
        this.courseRepository = courseRepository;
        this.fileRepository = fileRepository;
    }

    public Lesson createEmptyLesson(Long courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow();

        Lesson lesson = Lesson.builder()
                .course(course)
                .build();
        return lessonRepository.save(lesson);
    }

    public LessonResponse createLesson(CreateLessonRequest lessonRequest) {
        Course course = courseRepository.findById(lessonRequest.getCourseId()).orElseThrow();

        Lesson lesson = Lesson.builder()
                .course(course)
                .title(lessonRequest.getTitle())
                .build();

        Lesson savedLesson =  lessonRepository.save(lesson);

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

    public LessonResponse updateLesson(UpdateLessonRequest lessonRequest) {
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
                        .toList();
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
        fileRepository.deleteFilesByLessonId(id);
        lessonRepository.deleteById(id);
    }
    public void deleteLessonsByCourseId(Long id) {
        fileRepository.deleteFilesByCourseId(id);
        lessonRepository.deleteLessonsByCourseId(id);
    }

    public List<LessonResponse> getLessonsByCourseId(Long id) {
        List<Lesson> lessons = lessonRepository.findLessonsByCourseId(id);
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
}
