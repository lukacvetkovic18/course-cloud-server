package com.example.demo.api.course;
import com.example.demo.api.user.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    @Query("SELECT e.course FROM Enrollment e WHERE e.user.id = :userId AND e.isInstructor = true")
    List<Course> findCoursesOfInstructor(@Param("userId") Long userId);

    @Query("SELECT e.course FROM Enrollment e WHERE e.user.id = :userId AND e.isInstructor = false")
    List<Course> findEnrolledCoursesOfUser(@Param("userId") Long userId);

    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN TRUE ELSE FALSE END FROM Enrollment e WHERE e.user.id = :userId AND e.course.id = :courseId AND e.isInstructor = false")
    boolean isUserEnrolledInCourse(@Param("userId") Long userId, @Param("courseId") Long courseId);

    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN TRUE ELSE FALSE END FROM Enrollment e WHERE e.user.id = :userId AND e.course.id = :courseId AND e.isInstructor = true")
    boolean isUserOwnerOfCourse(@Param("userId") Long userId, @Param("courseId") Long courseId);

    @Query("SELECT e.user FROM Enrollment e WHERE e.course.id = :courseId AND e.isInstructor = true")
    User findOwnerOfCourse(@Param("courseId") Long courseId);

    @Query("SELECT e.user FROM Enrollment e WHERE e.course.id = :courseId AND e.isInstructor = false")
    List<User> findStudentsInCourse(@Param("courseId") Long courseId);

    @Query("SELECT c FROM Course c WHERE " +
            "LOWER(c.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(c.shortDescription) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(c.description) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Course> findCoursesBySearchQuery(@Param("query") String query);

    @Query("SELECT c FROM Course c ORDER BY RANDOM()")
    List<Course> findRandomCourses(Pageable pageable);

    @Query("SELECT c FROM Course c WHERE c.quiz IS NULL")
    List<Course> findCoursesWithoutQuiz();

}
