package com.example.demo.api.quiz;

import com.example.demo.api.answer.Answer;
import com.example.demo.api.answer.AnswerRepository;
import com.example.demo.api.course.Course;
import com.example.demo.api.course.CourseRepository;
import com.example.demo.api.question.Question;
import com.example.demo.api.question.QuestionRepository;
import com.example.demo.api.questionType.QuestionType;
import com.example.demo.api.questionType.QuestionTypeRepository;
import com.example.demo.api.quiz.quizModels.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuizService {
    private final QuizRepository quizRepository;
    private final CourseRepository courseRepository;
    private final QuestionRepository questionRepository;
    private final QuestionTypeRepository questionTypeRepository;
    private final AnswerRepository answerRepository;

    @Autowired
    public QuizService(
            QuizRepository quizRepository,
            CourseRepository courseRepository,
            QuestionRepository questionRepository,
            QuestionTypeRepository questionTypeRepository,
            AnswerRepository answerRepository
    ) {
        this.quizRepository = quizRepository;
        this.courseRepository = courseRepository;
        this.questionRepository = questionRepository;
        this.questionTypeRepository = questionTypeRepository;
        this.answerRepository = answerRepository;
    }

    public QuizResponse createQuiz(CreateQuizRequest quizRequest) {
        Course course = courseRepository.findById(quizRequest.getCourseId().orElseThrow())
                .orElseThrow(() -> new RuntimeException("Course not found"));

        Quiz quiz = Quiz.builder()
                .title(quizRequest.getTitle())
                .course(course)
                .build();
        Quiz savedQuiz = quizRepository.save(quiz);


        List<Question> questions = quizRequest.getQuestions().stream()
                .map(questionRequest -> createQuestion(questionRequest, savedQuiz))
                .toList();

        return QuizResponse.builder()
                .id(savedQuiz.getId())
                .title(savedQuiz.getTitle())
                .course(savedQuiz.getCourse())
                .createdAt(savedQuiz.getCreatedAt())
                .questions(questions.stream()
                        .map(this::mapToQuestionResponse)
                        .collect(Collectors.toList()))
                .build();
    }

    private Question createQuestion(QuestionRequest questionRequest, Quiz quiz) {
        QuestionType questionType = questionTypeRepository.findById(questionRequest.getQuestionTypeId())
                .orElseThrow(() -> new RuntimeException("Question type not found"));

        Question question = Question.builder()
                .title(questionRequest.getTitle())
                .questionType(questionType)
                .quiz(quiz)
                .build();
        Question savedQuestion = questionRepository.save(question);

        List<Answer> answers = questionRequest.getAnswers().stream()
                .map(answerRequest -> createAnswer(answerRequest, savedQuestion))
                .toList();

        return savedQuestion;
    }

    private Answer createAnswer(AnswerRequest answerRequest, Question question) {
        Answer answer = Answer.builder()
                .title(answerRequest.getTitle())
                .isCorrect(answerRequest.getIsCorrect())
                .question(question)
                .build();
        return answerRepository.save(answer);
    }

    private QuestionResponse mapToQuestionResponse(Question question) {
        return QuestionResponse.builder()
                .id(question.getId())
                .title(question.getTitle())
                .questionType(question.getQuestionType())
                .answers(answerRepository.findAnswersByQuestionId(question.getId()))
                .build();
    }

    public List<QuizResponse> getAllQuizzes() {
        List<Quiz> quizzes = quizRepository.findAll();
        return quizzes.stream()
                .map(this::mapToQuizResponse)
                .collect(Collectors.toList());
    }
    public QuizResponse getQuizById(Long id) {
        Quiz quiz = quizRepository.findById(id).orElseThrow(() -> new RuntimeException("Quiz not found"));
        return mapToQuizResponse(quiz);
    }

    @Transactional
    public QuizResponse updateQuiz(UpdateQuizRequest quizRequest, Long quizId) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new RuntimeException("Quiz not found"));

        quizRequest.getTitle().ifPresent(quiz::setTitle);
        quizRequest.getCourseId().ifPresent(courseId -> {
            Course course = courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("Course not found"));
            quiz.setCourse(course);
        });

        if (quizRequest.getQuestions().isPresent()) {
            List<QuestionRequest> newQuestions = quizRequest.getQuestions().get();
            List<Question> existingQuestions = questionRepository.findQuestionsByQuizId(quiz.getId());

            Map<String, Question> existingQuestionsMap = existingQuestions.stream()
                    .collect(Collectors.toMap(Question::getTitle, question -> question));

            Set<Long> questionsToDelete = new HashSet<>(existingQuestions.stream().map(Question::getId).collect(Collectors.toSet()));

            for (QuestionRequest questionRequest : newQuestions) {
                Question existingQuestion = existingQuestionsMap.get(questionRequest.getTitle());
                if (existingQuestion != null) {
                    updateQuestion(existingQuestion, questionRequest);
                    questionsToDelete.remove(existingQuestion.getId());
                } else {
                    createQuestion(questionRequest, quiz);
                }
            }

            for (Long questionId : questionsToDelete) {
                questionRepository.deleteById(questionId);
            }
        }

        Quiz savedQuiz = quizRepository.save(quiz);

        List<QuestionResponse> questionResponses = questionRepository.findQuestionsByQuizId(savedQuiz.getId()).stream()
                .map(this::mapToQuestionResponse)
                .collect(Collectors.toList());

        return QuizResponse.builder()
                .id(savedQuiz.getId())
                .title(savedQuiz.getTitle())
                .course(savedQuiz.getCourse())
                .createdAt(savedQuiz.getCreatedAt())
                .questions(questionResponses)
                .build();
    }

    private void updateQuestion(Question existingQuestion, QuestionRequest questionRequest) {
        existingQuestion.setTitle(questionRequest.getTitle());
        QuestionType questionType = questionTypeRepository.findById(questionRequest.getQuestionTypeId())
                .orElseThrow(() -> new RuntimeException("Question type not found"));
        existingQuestion.setQuestionType(questionType);

        List<AnswerRequest> newAnswers = questionRequest.getAnswers();
        List<Answer> existingAnswers = answerRepository.findAnswersByQuestionId(existingQuestion.getId());

        // Map of existing answers by title for fast lookup
        Map<String, Answer> existingAnswersMap = existingAnswers.stream()
                .collect(Collectors.toMap(Answer::getTitle, answer -> answer));

        // Keep track of answers to delete
        Set<Long> answersToDelete = new HashSet<>(existingAnswers.stream().map(Answer::getId).collect(Collectors.toSet()));

        for (AnswerRequest answerRequest : newAnswers) {
            Answer existingAnswer = existingAnswersMap.get(answerRequest.getTitle());
            if (existingAnswer != null) {
                existingAnswer.setIsCorrect(answerRequest.getIsCorrect());
                answersToDelete.remove(existingAnswer.getId());
            } else {
                createAnswer(answerRequest, existingQuestion);
            }
        }

        // Delete answers that are not in the new list
        for (Long answerId : answersToDelete) {
            answerRepository.deleteById(answerId);
        }

        questionRepository.save(existingQuestion);
    }

    @Transactional
    public void deleteAllQuizzes() {
        // Delete all quizzes and their associated questions and answers
        List<Quiz> quizzes = quizRepository.findAll();
        for (Quiz quiz : quizzes) {
            List<Question> questions = questionRepository.findQuestionsByQuizId(quiz.getId());
            for (Question question : questions) {
                answerRepository.deleteAnswersByQuestionId(question.getId());
            }
            questionRepository.deleteById(quiz.getId());
        }
        quizRepository.deleteAll();
    }

    @Transactional
    public void deleteQuiz(Long id) {
        Quiz quiz = quizRepository.findById(id).orElseThrow(() -> new RuntimeException("Quiz not found"));
        // Delete associated questions and answers
        List<Question> questions = questionRepository.findQuestionsByQuizId(quiz.getId());
        for (Question question : questions) {
            answerRepository.deleteAnswersByQuestionId(question.getId());
        }
        questionRepository.deleteQuestionsByQuizId(quiz.getId());
        quizRepository.deleteQuizByCourseId(quiz.getCourse().getId());
    }

    private QuizResponse mapToQuizResponse(Quiz quiz) {
        List<QuestionResponse> questionResponses = questionRepository.findQuestionsByQuizId(quiz.getId()).stream()
                .map(this::mapToQuestionResponse)
                .collect(Collectors.toList());

        return QuizResponse.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .course(quiz.getCourse())
                .createdAt(quiz.getCreatedAt())
                .questions(questionResponses)
                .build();
    }

    public QuizResponse getQuizByCourseId(Long id) {
        Quiz quiz = quizRepository.findQuizByCourseId(id).orElseThrow(() -> new RuntimeException("Quiz not found"));
        return mapToQuizResponse(quiz);
    }

    @Transactional
    public void deleteQuizByCourseId(Long id) {
        Quiz quiz = quizRepository.findQuizByCourseId(id).orElseThrow(() -> new RuntimeException("Quiz not found"));
        // Delete associated questions and answers
        List<Question> questions = questionRepository.findQuestionsByQuizId(quiz.getId());
        for (Question question : questions) {
            answerRepository.deleteAnswersByQuestionId(question.getId());
        }
        questionRepository.deleteQuestionsByQuizId(quiz.getId());
        quizRepository.deleteQuizByCourseId(id);
    }
    
}
