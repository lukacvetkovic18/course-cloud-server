package com.example.demo.api.course.courseModels;

import com.example.demo.api.user.User;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponse {
    private Long id;
    private String title;
    private String shortDescription;
    private String description;
    private Boolean isActive;
    private String image;
    private User owner;
    private Timestamp createdAt;
    private String slug;
}
