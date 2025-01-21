package com.example.demo.api.question;

import com.example.demo.api.answer.Answer;
import com.example.demo.api.answer.AnswerRepository;
import com.example.demo.api.answer.answerModels.UpdateAnswerRequest;
import com.example.demo.api.question.questionModels.CreateQuestionRequest;
import com.example.demo.api.question.questionModels.UpdateQuestionRequest;
import com.example.demo.api.question.questionModels.UpdateQuestionWithAnswersRequest;
import com.example.demo.api.questionType.QuestionType;
import com.example.demo.api.questionType.QuestionTypeRepository;
import com.example.demo.api.quiz.Quiz;
import com.example.demo.api.quiz.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final QuestionTypeRepository questionTypeRepository;
    private final QuizRepository quizRepository;
    private final AnswerRepository answerRepository;

    @Autowired
    public QuestionService(
            QuestionRepository questionRepository,
            QuestionTypeRepository questionTypeRepository,
            QuizRepository quizRepository,
            AnswerRepository answerRepository
    ) {
        this.questionRepository = questionRepository;
        this.questionTypeRepository = questionTypeRepository;
        this.quizRepository = quizRepository;
        this.answerRepository = answerRepository;
    }

    public Question createQuestion(CreateQuestionRequest questionRequest) {
        QuestionType questionType;
        if(questionRequest.getQuestionTypeId() != null) {
            questionType = questionTypeRepository.findById(questionRequest.getQuestionTypeId()).orElseThrow();
        } else {
            questionType = questionTypeRepository.findById((long)1).orElseThrow();
        }
        Quiz quiz = quizRepository.findById(questionRequest.getQuizId()).orElseThrow();

        Question question = Question.builder()
                .title(questionRequest.getTitle())
                .questionType(questionType)
                .quiz(quiz)
                .build();
        return questionRepository.save(question);
    }

    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    public Optional<Question> getQuestionById(Long id) {
        return questionRepository.findById(id);
    }

    public Question updateQuestion(UpdateQuestionRequest questionRequest) {
        Question question = questionRepository.findById(questionRequest.getId()).orElseThrow();;

        if(questionRequest.getTitle() != null) question.setTitle(questionRequest.getTitle().get());
        if(questionRequest.getQuestionTypeId() != null) {
            QuestionType questionType = questionTypeRepository.findById(questionRequest.getQuestionTypeId().get()).orElseThrow();
            question.setQuestionType(questionType);
        }
        if(questionRequest.getQuizId() != null) {
            Quiz quiz = quizRepository.findById(questionRequest.getQuizId().get()).orElseThrow();
            question.setQuiz(quiz);
        }
        return questionRepository.save(question);
    }

    public void deleteAllQuestions() {
        questionRepository.deleteAll();
    }

    public void deleteQuestion(Long id) {
        questionRepository.deleteById(id);
    }

    public List<Question> getQuestionsByQuizId(Long id) {
        return questionRepository.findQuestionsByQuizId(id);
    }

    public void deleteQuestionsByQuizId(Long id) {
        questionRepository.deleteQuestionsByQuizId(id);
    }

    public List<QuestionType> getAllQuestionTypes() {
        return questionTypeRepository.findAll();
    }

    public Question updateQuestionWithAnswers(UpdateQuestionWithAnswersRequest questionRequest) {
        Question question = questionRepository.findById(questionRequest.getId()).orElseThrow(() -> new RuntimeException("Question not found"));
        question.setTitle(questionRequest.getTitle());

        QuestionType questionType = questionTypeRepository.findById(questionRequest.getQuestionTypeId()).orElseThrow(() -> new RuntimeException("Question Type not found"));
        question.setQuestionType(questionType);

        questionRequest.getAnswers().forEach(answerRequest -> {
            Answer answer = answerRepository.findById(answerRequest.getId()).orElseThrow(() -> new RuntimeException("Answer not found"));

            answerRequest.getTitle().ifPresent(answer::setTitle);
            answerRequest.getIsCorrect().ifPresent(answer::setIsCorrect);

            answerRepository.save(answer);
        });

        return questionRepository.save(question);
    }
}
