package com.example.demo.api.quiz;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    @Query("SELECT q FROM Quiz q WHERE q.course.id = ?1")
    Optional<Quiz> findQuizByCourseId(Long courseId);

    @Modifying
    @Query("DELETE FROM Quiz q WHERE q.course.id = ?1")
    void deleteQuizByCourseId(Long courseId);
}
