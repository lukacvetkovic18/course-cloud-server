package com.example.demo.api.quiz;

import com.example.demo.api.question.Question;
import com.example.demo.api.quiz.quizModels.CreateQuizRequest;
import com.example.demo.api.quiz.quizModels.QuizResponse;
import com.example.demo.api.quiz.quizModels.UpdateQuizRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<QuizResponse> createQuiz(@RequestBody CreateQuizRequest quiz) {
        QuizResponse quizResponse = quizService.createQuiz(quiz);
        return ResponseEntity.ok(quizResponse);
    }

    @GetMapping
    public ResponseEntity<List<QuizResponse>> getAllQuizzes() {
        List<QuizResponse> quizzes = quizService.getAllQuizzes();
        return ResponseEntity.ok(quizzes);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<QuizResponse> getQuizById(@PathVariable("id") long id) {
        QuizResponse quizResponse =  quizService.getQuizById(id);
        return ResponseEntity.ok(quizResponse);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<QuizResponse> updateQuiz(@RequestBody UpdateQuizRequest quiz, @PathVariable Long id) {
        QuizResponse quizResponse = quizService.updateQuiz(quiz, id);
        return ResponseEntity.ok(quizResponse);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllQuizzes() {
        quizService.deleteAllQuizzes();
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteQuiz(@PathVariable("id") long id) {
        quizService.deleteQuiz(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "/course/{id}")
    public ResponseEntity<QuizResponse> getQuizByCourseId(@PathVariable("id") long id) {
        QuizResponse quizResponse = quizService.getQuizByCourseId(id);
        return ResponseEntity.ok(quizResponse);
    }

    @DeleteMapping(path = "/course/{id}")
    public ResponseEntity<Void> deleteQuizByCourseId(@PathVariable("id") long id) {
        quizService.deleteQuizByCourseId(id);
        return ResponseEntity.noContent().build();
    }
}
