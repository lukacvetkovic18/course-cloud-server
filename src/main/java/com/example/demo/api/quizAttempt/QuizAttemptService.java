package com.example.demo.api.quizAttempt;

import com.example.demo.api.answer.Answer;
import com.example.demo.api.answer.AnswerRepository;
import com.example.demo.api.question.Question;
import com.example.demo.api.question.QuestionRepository;
import com.example.demo.api.quiz.Quiz;
import com.example.demo.api.quiz.QuizRepository;
import com.example.demo.api.quizAttemptAnswer.QuizAttemptAnswer;
import com.example.demo.api.quizAttemptAnswer.QuizAttemptAnswerRepository;
import com.example.demo.api.user.User;
import com.example.demo.api.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class QuizAttemptService {
    private final QuizAttemptRepository quizAttemptRepository;
    private final QuizAttemptAnswerRepository quizAttemptAnswerRepository;
    private final QuizRepository quizRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    @Autowired
    public QuizAttemptService(
        QuizAttemptRepository quizAttemptRepository,
        QuizAttemptAnswerRepository quizAttemptAnswerRepository,
        QuizRepository quizRepository,
        UserRepository userRepository,
        QuestionRepository questionRepository,
        AnswerRepository answerRepository
    ) {
        this.quizAttemptRepository = quizAttemptRepository;
        this.quizAttemptAnswerRepository = quizAttemptAnswerRepository;
        this.quizRepository = quizRepository;
        this.userRepository = userRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
    }


    @Transactional
    public QuizAttempt createQuizAttempt(Long userId, Long quizId, List<Long> selectedAnswerIds, Map<Long, String> textAnswers) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new RuntimeException("Quiz not found"));

        List<Question> questions = questionRepository.findQuestionsByQuizId(quizId);
        int totalQuestions = questions.size();
        int correctAnswers = 0;

        QuizAttempt quizAttempt = new QuizAttempt();
        quizAttempt.setUser(user);
        quizAttempt.setQuiz(quiz);
        quizAttempt.setTotalQuestions(totalQuestions);

        quizAttempt = quizAttemptRepository.save(quizAttempt);

        for (Question question : questions) {
            QuizAttemptAnswer attemptAnswer = new QuizAttemptAnswer();
            attemptAnswer.setQuizAttempt(quizAttempt);
            attemptAnswer.setQuestion(question);

            if (question.getQuestionType().getId() == 1) { // Text question
                String userAnswer = textAnswers.get(question.getId());
                attemptAnswer.setTextAnswer(userAnswer);
                boolean isCorrect = checkTextAnswer(question, userAnswer);
                attemptAnswer.setIsCorrect(isCorrect);
                if (isCorrect) correctAnswers++;
            } else { // Single or multiple choice
                Answer selectedAnswer = answerRepository.findById(selectedAnswerIds.get(Math.toIntExact(question.getId())))
                        .orElseThrow(() -> new RuntimeException("Answer not found"));
                attemptAnswer.setSelectedAnswer(selectedAnswer);
                attemptAnswer.setIsCorrect(selectedAnswer.getIsCorrect());
                if (selectedAnswer.getIsCorrect()) correctAnswers++;
            }

            quizAttemptAnswerRepository.save(attemptAnswer);
        }

        quizAttempt.setCorrectAnswers(correctAnswers);
        quizAttempt.setScore((correctAnswers * 100) / totalQuestions); // Score in percentage
        return quizAttemptRepository.save(quizAttempt);
    }

    private boolean checkTextAnswer(Question question, String userAnswer) {
        // Implement logic to check if text answer is correct
        return userAnswer.equalsIgnoreCase("expected answer"); // Replace with actual answer checking logic
    }

    public List<QuizAttempt> getUserAttempts(Long userId) {
        return quizAttemptRepository.findByUserId(userId);
    }
}
