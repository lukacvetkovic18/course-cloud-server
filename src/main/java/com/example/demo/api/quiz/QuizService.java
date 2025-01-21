package com.example.demo.api.quiz;

import com.example.demo.api.course.Course;
import com.example.demo.api.course.CourseRepository;
import com.example.demo.api.question.Question;
import com.example.demo.api.quiz.quizModels.CreateQuizRequest;
import com.example.demo.api.quiz.quizModels.UpdateQuizRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuizService {
    private final QuizRepository quizRepository;
    private final CourseRepository courseRepository;

    @Autowired
    public QuizService(
            QuizRepository quizRepository,
            CourseRepository courseRepository
    ) {
        this.quizRepository = quizRepository;
        this.courseRepository = courseRepository;
    }

    public Quiz createQuiz(CreateQuizRequest quizRequest) {
        Course course = courseRepository.findById(quizRequest.getCourseId()).orElseThrow();

        Quiz quiz = Quiz.builder()
                .title(quizRequest.getTitle())
                .course(course)
                .build();
        return quizRepository.save(quiz);
    }

    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll();
    }

    public Optional<Quiz> getQuizById(Long id) {
        return quizRepository.findById(id);
    }

    public Quiz updateQuiz(UpdateQuizRequest quizRequest) {
        Quiz quiz = quizRepository.findById(quizRequest.getId()).orElseThrow();

        if(quizRequest.getCourseId() != null) {
            Course course = courseRepository.findById(quizRequest.getCourseId().get()).orElseThrow();
            quiz.setCourse(course);
        }
        if(quizRequest.getTitle() != null) quiz.setTitle(quizRequest.getTitle().get());

        return quizRepository.save(quiz);
    }

    public void deleteAllQuizzes() {
        quizRepository.deleteAll();
    }

    public void deleteQuiz(Long id) {
        quizRepository.deleteById(id);
    }

    public Optional<Quiz> getQuizByCourseId(Long id) {
        return quizRepository.findQuizByCourseId(id);
    }

    public void deleteQuizByCourseId(Long id) {
        quizRepository.deleteQuizByCourseId(id);
    }
    
}
