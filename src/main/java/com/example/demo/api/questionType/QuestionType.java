package com.example.demo.api.questionType;

import java.sql.Date;
import java.sql.Timestamp;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "question_types")
public class QuestionType {
    @Id
    @SequenceGenerator(
            name = "question_type_sequence",
            sequenceName = "question_type_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "question_type_sequence"
    )
    private Long id;
    
    @Column()
    private String name;

    // @OneToMany(cascade = CascadeType.ALL)
    // private Set<Question> questions;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Timestamp createdAt;

    // Constructors, Getters, and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }
}
