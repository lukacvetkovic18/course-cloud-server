package com.example.demo.api.quizAttempt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long>{
    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.user.id = :userId")
    List<QuizAttempt> findByUserId(@Param("userId") Long userId);

    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.quiz.id = :quizId AND qa.user.id = :userId")
    Optional<QuizAttempt> findByQuizIdAndUserId(@Param("quizId") Long quizId, @Param("userId") Long userId);
}
