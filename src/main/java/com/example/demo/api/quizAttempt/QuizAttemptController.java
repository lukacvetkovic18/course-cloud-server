package com.example.demo.api.quizAttempt;

import com.example.demo.api.lesson.LessonService;
import com.example.demo.api.quizAttempt.quizAttemptModels.QuizAttemptRequest;
import com.example.demo.api.quizAttemptAnswer.QuizAttemptAnswer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/quiz-attempts")
public class QuizAttemptController {
    private final QuizAttemptService quizAttemptService;

    @Autowired
    public QuizAttemptController(QuizAttemptService quizAttemptService) {
        this.quizAttemptService = quizAttemptService;
    }

    @PostMapping()
    public ResponseEntity<QuizAttempt> submitQuizAttempt(
//            @PathVariable Long userId,
//            @PathVariable Long quizId,
            @RequestBody QuizAttemptRequest request) {
        QuizAttempt quizAttempt = quizAttemptService.createQuizAttempt(request.getUserId(), request.getQuizId(), request.getSelectedAnswerIds(), request.getTextAnswers());
        return ResponseEntity.ok(quizAttempt);
    }

    @GetMapping("/quiz/{quizId}/user/{userId}")
    public ResponseEntity<QuizAttempt> getQuizAttemptByQuizAndUser(
            @PathVariable Long quizId,
            @PathVariable Long userId) {
        QuizAttempt quizAttempt = quizAttemptService.getQuizAttemptByQuizAndUser(quizId, userId);
        return ResponseEntity.ok(quizAttempt);
    }

    @GetMapping("/{quizAttemptId}")
    public ResponseEntity<List<QuizAttemptAnswer>> getQuizAttemptAnswersByQuizAttempt(@PathVariable Long quizAttemptId) {
        List<QuizAttemptAnswer> quizAttemptAnswers = quizAttemptService.getQuizAttemptAnswersByQuizAttempt(quizAttemptId);
        return ResponseEntity.ok(quizAttemptAnswers);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<QuizAttempt>> getUserAttempts(@PathVariable Long userId) {
        return ResponseEntity.ok(quizAttemptService.getUserAttempts(userId));
    }
}
