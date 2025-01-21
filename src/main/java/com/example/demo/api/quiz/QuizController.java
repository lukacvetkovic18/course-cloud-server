package com.example.demo.api.quiz;

import com.example.demo.api.question.Question;
import com.example.demo.api.quiz.quizModels.CreateQuizRequest;
import com.example.demo.api.quiz.quizModels.UpdateQuizRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/api/quizzes")
public class QuizController {

    private final QuizService quizService;

    @Autowired
    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @PostMapping
    public Quiz createQuiz(@RequestBody CreateQuizRequest quiz) {
        return quizService.createQuiz(quiz);
    }

    @GetMapping
    public List<Quiz> getAllQuizzes() { return quizService.getAllQuizzes(); }

    @GetMapping(path = "/{id}")
    public Optional<Quiz> getQuizById(@PathVariable("id") long id) {
        return quizService.getQuizById(id);
    }

    @PutMapping
    public Quiz updateQuiz(@RequestBody UpdateQuizRequest quiz) {
        return quizService.updateQuiz(quiz);
    }

    @DeleteMapping
    public void deleteAllQuizzes() {
        quizService.deleteAllQuizzes();
    }

    @DeleteMapping(path = "/{id}")
    public void deleteQuiz(@PathVariable("id") long id) {
        quizService.deleteQuiz(id);
    }

    @GetMapping(path = "/course/{id}")
    public Optional<Quiz> getQuizByCourseId(@PathVariable("id") long id) {
        return quizService.getQuizByCourseId(id);
    }

    @DeleteMapping(path = "/course/{id}")
    public void deleteQuizByCourseId(@PathVariable("id") long id) {
        quizService.deleteQuizByCourseId(id);
    }
}
