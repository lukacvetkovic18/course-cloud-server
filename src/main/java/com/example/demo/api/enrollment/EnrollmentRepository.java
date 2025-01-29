package com.example.demo.api.enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    @Modifying
    @Query("DELETE FROM Enrollment e WHERE e.course.id = ?1")
    void deleteEnrollmentsByCourseId(Long courseId);

    @Query("SELECT e FROM Enrollment e WHERE e.course.id = ?1 AND e.isInstructor = true")
    Enrollment findOwnerEnrollmentByCourseId(Long courseId);
}
