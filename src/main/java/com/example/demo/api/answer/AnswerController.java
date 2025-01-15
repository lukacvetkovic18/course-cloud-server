package com.example.demo.api.answer;

import com.example.demo.api.answer.answerModels.CreateAnswerRequest;
import com.example.demo.api.answer.answerModels.UpdateAnswerRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/api/answers")
public class AnswerController {

    private final AnswerService answerService;

    @Autowired
    public AnswerController(AnswerService answerService) {
        this.answerService = answerService;
    }

    @PostMapping
    public Answer createAnswer(@RequestBody CreateAnswerRequest answer) {
        return answerService.createAnswer(answer);
    }

    @GetMapping
    public List<Answer> getAllAnswers() { return answerService.getAllAnswers(); }

    @GetMapping(path = "/{id}")
    public Optional<Answer> getAnswerById(@PathVariable("id") long id) {
        return answerService.getAnswerById(id);
    }

    @PutMapping
    public Answer updateAnswer(@RequestBody UpdateAnswerRequest answer) {
        return answerService.updateAnswer(answer);
    }

    @DeleteMapping
    public void deleteAllAnswers() {
        answerService.deleteAllAnswers();
    }

    @DeleteMapping(path = "/{id}")
    public void deleteAnswer(@PathVariable("id") long id) {
        answerService.deleteAnswer(id);
    }

    @GetMapping(path = "/question/{id}")
    public List<Answer> getAnswersByQuestionId(@PathVariable("id") long id) {
        return answerService.getAnswersByQuestionId(id);
    }

    @DeleteMapping(path = "/question/{id}")
    public void deleteAnswersByQuestionId(@PathVariable("id") long id) {
        answerService.deleteAnswersByQuestionId(id);
    }

    @PostMapping(path = "/questions/answers")
    public List<Answer> getAnswersByQuestionIds(@RequestBody List<Long> questionIds) {
        return answerService.getAnswersByQuestionIds(questionIds);
    }
}
