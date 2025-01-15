package com.example.demo.api.answer;

import com.example.demo.api.answer.answerModels.CreateAnswerRequest;
import com.example.demo.api.answer.answerModels.UpdateAnswerRequest;
import com.example.demo.api.file.File;
import com.example.demo.api.question.Question;
import com.example.demo.api.question.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AnswerService {
    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;

    @Autowired
    public AnswerService(
            AnswerRepository answerRepository,
            QuestionRepository questionRepository
    ) {
        this.answerRepository = answerRepository;
        this.questionRepository = questionRepository;
    }

    public Answer createAnswer(CreateAnswerRequest answerRequest) {
        Question question = questionRepository.findById(answerRequest.getQuestionId()).orElseThrow();

        Answer answer = Answer.builder()
                .title(answerRequest.getTitle())
                .isCorrect(answerRequest.getIsCorrect())
                .question(question)
                .build();

        return answerRepository.save(answer);
    }

    public List<Answer> getAllAnswers() {
        return answerRepository.findAll();
    }

    public Optional<Answer> getAnswerById(Long id) {
        return answerRepository.findById(id);
    }

    public Answer updateAnswer(UpdateAnswerRequest answerRequest) {
        Answer answer = answerRepository.findById(answerRequest.getId()).orElseThrow();

        if(answerRequest.getTitle() != null) answer.setTitle(answerRequest.getTitle().get());
        if(answerRequest.getIsCorrect() != null) answer.setIsCorrect(answerRequest.getIsCorrect().get());
        if(answerRequest.getQuestionId() != null) {
            Question question = questionRepository.findById(answerRequest.getQuestionId().get()).orElseThrow();
            answer.setQuestion(question);
        }

        return answerRepository.save(answer);
    }

    public void deleteAllAnswers() {
        answerRepository.deleteAll();
    }

    public void deleteAnswer(Long id) {
        answerRepository.deleteById(id);
    }

    public List<Answer> getAnswersByQuestionId(Long id) {
        return answerRepository.findAnswersByQuestionId(id);
    }

    public void deleteAnswersByQuestionId(Long id) {
        answerRepository.deleteAnswersByQuestionId(id);
    }

    public List<Answer> getAnswersByQuestionIds(List<Long> questionIds) {
        return answerRepository.findAnswersByQuestionIds(questionIds);
    }
}
