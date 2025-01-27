package com.example.demo.api.quizAttemptAnswer;

import com.example.demo.api.answer.Answer;
import com.example.demo.api.question.Question;
import com.example.demo.api.quizAttempt.QuizAttempt;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "quiz_attempt_answers")
public class QuizAttemptAnswer {
    @Id
    @SequenceGenerator(
            name = "quiz_attempt_answer_sequence",
            sequenceName = "quiz_attempt_answer_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "quiz_attempt_answer_sequence"
    )
    private Long id;

    @ManyToOne
    @JoinColumn(name = "quiz_attempt_id", nullable = false)
    private QuizAttempt quizAttempt;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @ManyToOne
    @JoinColumn(name = "selected_answer_id")
    private Answer selectedAnswer;

    @Column(nullable = true)
    private String textAnswer;

    @Column(nullable = false)
    private Boolean isCorrect;
}
