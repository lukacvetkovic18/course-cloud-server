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
import java.util.stream.Collectors;

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
    public QuizAttempt createQuizAttempt(Long userId, Long quizId, Map<Long, List<Long>> selectedAnswerIds, Map<Long, String> textAnswers) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new RuntimeException("Quiz not found"));

        List<Question> questions = questionRepository.findQuestionsByQuizId(quizId);
        int totalQuestions = questions.size();
        double correctAnswers = 0;

        QuizAttempt quizAttempt = new QuizAttempt();
        quizAttempt.setUser(user);
        quizAttempt.setQuiz(quiz);
        quizAttempt.setTotalQuestions(totalQuestions);

        quizAttempt = quizAttemptRepository.save(quizAttempt);

        for (Question question : questions) {
            if (question.getQuestionType().getId() == 1) {
                //text question
                Answer correctAnswer = answerRepository.findAnswersByQuestionId(question.getId()).getFirst();
                String userAnswer = textAnswers.get(question.getId());
                QuizAttemptAnswer attemptAnswer = new QuizAttemptAnswer();
                attemptAnswer.setQuizAttempt(quizAttempt);
                attemptAnswer.setQuestion(question);
                attemptAnswer.setTextAnswer(userAnswer);
                boolean isCorrect = userAnswer.equalsIgnoreCase(correctAnswer.getTitle());
                attemptAnswer.setIsCorrect(isCorrect);
                if (isCorrect) correctAnswers++;
                quizAttemptAnswerRepository.save(attemptAnswer);
            } else if (question.getQuestionType().getId() == 2) {
                //single choice
                Answer selectedAnswer = answerRepository.findById(selectedAnswerIds.get(question.getId()).getFirst())
                        .orElseThrow(() -> new RuntimeException("Answer not found"));
                QuizAttemptAnswer attemptAnswer = new QuizAttemptAnswer();
                attemptAnswer.setQuizAttempt(quizAttempt);
                attemptAnswer.setQuestion(question);
                attemptAnswer.setSelectedAnswer(selectedAnswer);
                attemptAnswer.setIsCorrect(selectedAnswer.getIsCorrect());
                if (selectedAnswer.getIsCorrect()) correctAnswers++;
                quizAttemptAnswerRepository.save(attemptAnswer);
            } else if (question.getQuestionType().getId() == 3) {
                //multiple choice
                List<Answer> correctAnswersList = answerRepository.findCorrectAnswersByQuestionId(question.getId());
                List<Long> correctAnswerIds = correctAnswersList.stream().map(Answer::getId).toList();

                List<Long> userSelectedAnswerIds = selectedAnswerIds.get(question.getId());

                long correctSelectedCount = userSelectedAnswerIds.stream().filter(correctAnswerIds::contains).count();
                long incorrectSelectedCount = userSelectedAnswerIds.size() - correctSelectedCount;

                if (correctSelectedCount == correctAnswerIds.size() && incorrectSelectedCount == 0) {
                    correctAnswers++;
                } else if (correctSelectedCount > incorrectSelectedCount) {
                    double percentageOfSelectedAnswers = (double) 1 / userSelectedAnswerIds.size();
                    correctAnswers += (correctSelectedCount - incorrectSelectedCount) * percentageOfSelectedAnswers;
                }

                for (Long answerId : userSelectedAnswerIds) {
                    Answer selectedAnswer = answerRepository.findById(answerId)
                            .orElseThrow(() -> new RuntimeException("Answer not found"));
                    QuizAttemptAnswer attemptAnswer = new QuizAttemptAnswer();
                    attemptAnswer.setQuizAttempt(quizAttempt);
                    attemptAnswer.setQuestion(question);
                    attemptAnswer.setSelectedAnswer(selectedAnswer);
                    attemptAnswer.setIsCorrect(correctAnswerIds.contains(answerId));
                    quizAttemptAnswerRepository.save(attemptAnswer);
                }
            }
        }

        quizAttempt.setCorrectAnswers(correctAnswers);
        quizAttempt.setScore((correctAnswers * 100) / totalQuestions);
        return quizAttemptRepository.save(quizAttempt);
    }


    public QuizAttempt getQuizAttemptByQuizAndUser(Long quizId, Long userId) {
        return quizAttemptRepository.findByQuizIdAndUserId(quizId, userId)
                .orElseThrow(() -> new RuntimeException("Quiz attempt not found"));
    }

    public List<QuizAttemptAnswer> getQuizAttemptAnswersByQuizAttempt(Long quizAttemptId) {
        return quizAttemptAnswerRepository.findByQuizAttemptId(quizAttemptId);
    }

    public List<QuizAttempt> getUserAttempts(Long userId) {
        return quizAttemptRepository.findByUserId(userId);
    }

    public List<QuizAttempt> getQuizAttemptsByCourseId(Long courseId) {
        return quizAttemptRepository.findByCourseId(courseId);
    }
}
