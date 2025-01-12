package com.example.demo.api.lesson;

import com.example.demo.api.course.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    @Query("SELECT l FROM Lesson l WHERE l.course.id = ?1")
    List<Lesson> findLessonsByCourseId(Long courseId);

    @Modifying
    @Query("DELETE FROM Lesson l WHERE l.course.id = ?1")
    void deleteLessonsByCourseId(Long courseId);
}
