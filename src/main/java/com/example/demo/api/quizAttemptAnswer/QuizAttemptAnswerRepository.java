package com.example.demo.api.quizAttemptAnswer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizAttemptAnswerRepository extends JpaRepository<QuizAttemptAnswer, Long> {
    @Query("SELECT qaa FROM QuizAttemptAnswer qaa WHERE qaa.quizAttempt.id = :quizAttemptId")
    List<QuizAttemptAnswer> findByQuizAttemptId(@Param("quizAttemptId") Long quizAttemptId);
}
