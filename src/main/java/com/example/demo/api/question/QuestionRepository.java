package com.example.demo.api.question;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    @Query("SELECT q FROM Question q WHERE q.quiz.id = ?1")
    List<Question> findQuestionsByQuizId(Long quizId);

    @Modifying
    @Query("DELETE FROM Question q WHERE q.quiz.id = ?1")
    void deleteQuestionsByQuizId(Long quizId);

    @Modifying
    @Query("DELETE FROM Question q WHERE q.quiz.id IN (SELECT z.id FROM Quiz z WHERE z.course.id = :courseId)")
    void deleteQuestionsByCourseId(@Param("courseId") Long courseId);
}
