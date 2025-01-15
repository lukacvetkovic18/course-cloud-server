package com.example.demo.api.question;

import com.example.demo.api.question.questionModels.CreateQuestionRequest;
import com.example.demo.api.question.questionModels.UpdateQuestionRequest;
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

    @Autowired
    public QuestionService(
            QuestionRepository questionRepository,
            QuestionTypeRepository questionTypeRepository,
            QuizRepository quizRepository
    ) {
        this.questionRepository = questionRepository;
        this.questionTypeRepository = questionTypeRepository;
        this.quizRepository = quizRepository;
    }

    public Question createQuestion(CreateQuestionRequest questionRequest) {
        QuestionType questionType = questionTypeRepository.findById(questionRequest.getQuestionTypeId()).orElseThrow();
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
}
