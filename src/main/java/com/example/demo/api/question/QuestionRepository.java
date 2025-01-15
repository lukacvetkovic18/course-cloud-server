package com.example.demo.api.question;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    @Query("SELECT q FROM Question q WHERE q.quiz.id = ?1")
    List<Question> findQuestionsByQuizId(Long quizId);

    @Modifying
    @Query("DELETE FROM Question q WHERE q.quiz.id = ?1")
    void deleteQuestionsByQuizId(Long quizId);
}
