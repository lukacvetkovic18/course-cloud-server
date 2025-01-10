package com.example.demo.api.enrollment;

import java.sql.Timestamp;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import com.example.demo.api.course.Course;
import com.example.demo.api.user.User;

@Getter
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "enrollments")
public class Enrollment {
    @Id
    @SequenceGenerator(
            name = "enrollment_sequence",
            sequenceName = "enrollment_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "enrollment_sequence"
    )
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne
    @JoinColumn(name = "course_id")
    Course course;
    
    @Column()
    private Double progress;
    
    @Column()
    private Boolean isInstructor;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Timestamp createdAt;

}
