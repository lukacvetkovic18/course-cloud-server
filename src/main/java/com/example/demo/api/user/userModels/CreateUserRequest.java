package com.example.demo.api.user.userModels;

import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Date dateOfBirth;
    private String gender;
    private Boolean isActive;
    private List<Long> userRoleIds;
}