package com.example.demo.api.question;

import com.example.demo.api.question.questionModels.CreateQuestionRequest;
import com.example.demo.api.question.questionModels.UpdateQuestionRequest;
import com.example.demo.api.question.questionModels.UpdateQuestionWithAnswersRequest;
import com.example.demo.api.questionType.QuestionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/api/questions")
public class QuestionController {
    private final QuestionService questionService;

    @Autowired
    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @PostMapping
    public Question createQuestion(@RequestBody CreateQuestionRequest question) {
        return questionService.createQuestion(question);
    }

    @GetMapping
    public List<Question> getAllQuestions() { return questionService.getAllQuestions(); }

    @GetMapping(path = "/{id}")
    public Optional<Question> getQuestionById(@PathVariable("id") long id) {
        return questionService.getQuestionById(id);
    }

    @PutMapping
    public Question updateQuestion(@RequestBody UpdateQuestionRequest question) {
        return questionService.updateQuestion(question);
    }

    @DeleteMapping
    public void deleteAllQuestions() {
        questionService.deleteAllQuestions();
    }

    @DeleteMapping(path = "/{id}")
    public void deleteQuestion(@PathVariable("id") long id) {
        questionService.deleteQuestion(id);
    }

    @GetMapping(path = "/quiz/{id}")
    public List<Question> getQuestionsByQuizId(@PathVariable("id") long id) {
        return questionService.getQuestionsByQuizId(id);
    }

    @DeleteMapping(path = "/quiz/{id}")
    public void deleteQuestionsByQuizId(@PathVariable("id") long id) {
        questionService.deleteQuestionsByQuizId(id);
    }

    @GetMapping(path = "/types")
    public List<QuestionType> getAllQuestionTypes() { return questionService.getAllQuestionTypes(); }

    @PutMapping("/update-with-answers")
    public Question updateQuestionWithAnswers(@RequestBody UpdateQuestionWithAnswersRequest questionRequest) {
        return questionService.updateQuestionWithAnswers(questionRequest);
    }

}
