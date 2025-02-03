package com.example.demo.api.enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    @Modifying
    @Query("DELETE FROM Enrollment e WHERE e.course.id = ?1")
    void deleteEnrollmentsByCourseId(Long courseId);

    @Query("SELECT e FROM Enrollment e WHERE e.course.id = ?1 AND e.isInstructor = true")
    Optional<Enrollment> findOwnerEnrollmentByCourseId(Long courseId);

    @Query("SELECT e FROM Enrollment e WHERE e.user.id = :userId AND e.course.id = :courseId AND e.isInstructor = false")
    Optional<Enrollment> findEnrollmentByUserIdAndCourseId(@Param("userId") Long userId, @Param("courseId") Long courseId);
}
