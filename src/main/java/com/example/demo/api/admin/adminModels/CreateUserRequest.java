package com.example.demo.api.admin.adminModels;

import lombok.*;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {
    private String firstName;
    private String lastName;
    private String email;
    private Optional<String> slug;
    private String password;
    private Date dateOfBirth;
    private String gender;
    private Optional<String> address;
    private Boolean isActive;
    private Optional<String> profilePicture;
    private Optional<String> phoneNumber;
    private Optional<String> instagram;
    private Optional<String> linkedIn;
    private List<Long> userRoleIds;
}
