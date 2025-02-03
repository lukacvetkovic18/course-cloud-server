package com.example.demo.api.admin.adminModels;

import com.example.demo.api.user.User;
import lombok.*;

import java.sql.Timestamp;
import java.util.Optional;

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
