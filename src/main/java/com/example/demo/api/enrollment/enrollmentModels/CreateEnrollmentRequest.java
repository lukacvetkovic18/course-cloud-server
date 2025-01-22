package com.example.demo.api.enrollment.enrollmentModels;

import lombok.*;

@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateEnrollmentRequest {
    private Long userId;
    private Long courseId;
    private Boolean isInstructor;
}
