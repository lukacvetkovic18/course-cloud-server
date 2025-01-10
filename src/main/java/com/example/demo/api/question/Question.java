package com.example.demo.api.question;

import java.sql.Timestamp;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import com.example.demo.api.questionType.QuestionType;
import com.example.demo.api.quiz.Quiz;

@Getter
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "questions")
public class Question {
    @Id
    @SequenceGenerator(
            name = "question_sequence",
            sequenceName = "question_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "question_sequence"
    )
    private Long id;
    
    @Column()
    private String title;

    @ManyToOne
    @JoinColumn(name = "question_type_id")
    QuestionType questionType;

    @ManyToOne
    @JoinColumn(name = "quiz_id")
    Quiz quiz;

    // @OneToMany(cascade = CascadeType.ALL)
    // private List<Answer> answers;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Timestamp createdAt;
}
