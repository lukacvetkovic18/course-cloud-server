package com.example.demo.api.admin.adminModels;

import com.example.demo.api.userRole.UserRole;
import lombok.*;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String slug;
    private Date dateOfBirth;
    private String gender;
    private String address;
    private Boolean isActive;
    private String profilePicture;
    private String phoneNumber;
    private String instagram;
    private String linkedIn;
    private List<UserRole> userRoles;
    private Timestamp createdAt;
}
