package com.example.demo.api.quizAttempt;

import com.example.demo.api.lesson.LessonService;
import com.example.demo.api.quizAttempt.quizAttemptModels.QuizAttemptRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/quiz-attempts")
public class QuizAttemptController {
    private final QuizAttemptService quizAttemptService;

    @Autowired
    public QuizAttemptController(QuizAttemptService quizAttemptService) {
        this.quizAttemptService = quizAttemptService;
    }

    @PostMapping("/{userId}/{quizId}")
    public ResponseEntity<QuizAttempt> submitQuizAttempt(
            @PathVariable Long userId,
            @PathVariable Long quizId,
            @RequestBody QuizAttemptRequest request) {
        QuizAttempt quizAttempt = quizAttemptService.createQuizAttempt(userId, quizId, request.getSelectedAnswerIds(), request.getTextAnswers());
        return ResponseEntity.ok(quizAttempt);
    }
//    @GetMapping("/{userId}/attempts")
//    public ResponseEntity<List<QuizAttempt>> getUserAttempts(@PathVariable Long userId) {
//        return ResponseEntity.ok(quizAttemptService.getUserAttempts(userId));
//    }
}
