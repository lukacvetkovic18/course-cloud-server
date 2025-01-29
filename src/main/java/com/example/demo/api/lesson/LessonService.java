package com.example.demo.api.lesson;

import com.example.demo.api.course.Course;
import com.example.demo.api.course.CourseRepository;
import com.example.demo.api.file.FileRepository;
import com.example.demo.api.lesson.lessonModels.CreateLessonRequest;
import com.example.demo.api.lesson.lessonModels.UpdateLessonRequest;
import com.example.demo.api.quiz.Quiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public Lesson createLesson(CreateLessonRequest lessonRequest) {
        Course course = courseRepository.findById(lessonRequest.getCourseId()).orElseThrow();

        Lesson lesson = Lesson.builder()
                .course(course)
                .title(lessonRequest.getTitle())
                .build();
        return lessonRepository.save(lesson);
    }

    public List<Lesson> getAllLessons() {
        return lessonRepository.findAll();
    }

    public Optional<Lesson> getLessonById(Long id) {
        return lessonRepository.findById(id);
    }

    public Lesson updateLesson(UpdateLessonRequest lessonRequest) {
        Lesson lesson = lessonRepository.findById(lessonRequest.getId()).orElseThrow();

        if(lessonRequest.getCourseId() != null) {
            Course course = courseRepository.findById(lessonRequest.getCourseId().get()).orElseThrow();
            lesson.setCourse(course);
        }
        if(lessonRequest.getTitle() != null) lesson.setTitle(lessonRequest.getTitle().get());

        return lessonRepository.save(lesson);
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

    public List<Lesson> getLessonsByCourseId(Long id) {
        return lessonRepository.findLessonsByCourseId(id);
    }
}
