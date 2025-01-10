package com.example.demo.api.user.userModels;

import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    private Long id;
    private Optional<String> firstName;
    private Optional<String> lastName;
    private Optional<String> email;
    private Optional<Date> dateOfBirth;
    private Optional<String> gender;
    private Optional<String> address;
    private Optional<Boolean> isActive;
    private Optional<String> profilePicture;
    private Optional<String> phoneNumber;
    private Optional<String> instagram;
    private Optional<String> linkedIn;
    private Optional<List<Long>> userRoleIds;
}
