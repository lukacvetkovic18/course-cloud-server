package com.example.demo.api.quiz;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    @Query("SELECT q FROM Quiz q WHERE q.course.id = ?1")
    List<Quiz> findQuizzesByCourseId(Long courseId);

    @Modifying
    @Query("DELETE FROM Quiz q WHERE q.course.id = ?1")
    void deleteQuizzesByCourseId(Long courseId);
}
