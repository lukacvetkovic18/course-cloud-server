package com.example.demo.api.enrollment.enrollmentModels;

import lombok.*;

import java.util.Optional;

@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEnrollmentRequest {
    private Long id;
    private Optional<Long> userId;
    private Optional<Long> courseId;
    private Optional<Boolean> isInstructor;
}