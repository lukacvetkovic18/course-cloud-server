package com.example.demo.api.course;

import java.sql.Timestamp;

import com.example.demo.api.quiz.Quiz;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

@Getter
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "courses")
public class Course {
    @Id
    @SequenceGenerator(
            name = "course_sequence",
            sequenceName = "course_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "course_sequence"
    )
    private Long id;

    @Column()
    private String title;

    @Column()
    private String description;

    @Column()
    private Boolean isActive;

    @Column()
    private Float duration;

    @OneToOne(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Quiz quiz;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Timestamp createdAt;

    // @OneToMany(cascade = CascadeType.ALL)
    // private List<Enrollment> enrollments;

    // @OneToMany(cascade = CascadeType.ALL)
    // private List<Lesson> lessons;

    // @OneToMany(cascade = CascadeType.ALL)
    // private List<Quiz> quizzes;

}