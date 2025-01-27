package com.example.demo.api.answer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    @Query("SELECT a FROM Answer a WHERE a.question.id = ?1")
    List<Answer> findAnswersByQuestionId(Long questionId);

    @Modifying
    @Query("DELETE FROM Answer a WHERE a.question.id = ?1")
    void deleteAnswersByQuestionId(Long questionId);

    @Query("SELECT a FROM Answer a WHERE a.question.id IN :questionIds")
    List<Answer> findAnswersByQuestionIds(@Param("questionIds") List<Long> questionIds);

    @Modifying
    @Query("DELETE FROM Answer a WHERE a.question.id IN (SELECT q.id FROM Question q WHERE q.quiz.id IN (SELECT z.id FROM Quiz z WHERE z.course.id = :courseId))")
    void deleteAnswersByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT a FROM Answer a WHERE a.question.id = :questionId AND a.isCorrect = true")
    List<Answer> findCorrectAnswersByQuestionId(@Param("questionId") Long questionId);
}