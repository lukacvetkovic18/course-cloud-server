package com.example.demo.api.file;

import com.example.demo.api.lesson.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface FileRepository extends JpaRepository<File, Long> {
    @Query("SELECT f FROM File f WHERE f.lesson.id = ?1")
    List<File> findFilesByLessonId(Long lessonId);

    @Modifying
    @Query("DELETE FROM File f WHERE f.lesson.id = ?1")
    void deleteFilesByLessonId(Long lessonId);

    @Query("SELECT f FROM File f WHERE f.lesson.id IN :lessonIds")
    List<File> findFilesByLessonIds(@Param("lessonIds") List<Long> lessonIds);
}