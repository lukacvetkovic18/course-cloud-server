package com.example.demo.api.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.example.demo.api.user.userModels.*;
import com.example.demo.api.userRole.UserRole;
import com.example.demo.api.userRole.UserRoleRepository;
import com.example.demo.config.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserService(UserRepository userRepository, UserRoleRepository userRoleRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public static String getBearerTokenHeader() {
        String res =((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest().getHeader("Authorization");
        return res.replace("Bearer ", "");
    }

    public AuthenticateUserResponse loginUser(LoginUserRequest loginInfo) {
        var user = userRepository.findByEmail(loginInfo.getEmail()).orElseThrow();

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginInfo.getEmail(),
                        loginInfo.getPassword()
                )
        );

        var jwToken = jwtService.generateToken(user);

        return AuthenticateUserResponse
                .builder()
                .token(jwToken)
                .build();
    }

    public AuthenticateUserResponse registerUser(CreateUserRequest createUserInput) throws Exception {
        if(userRepository.findByEmail(createUserInput.getEmail()).isPresent()) {
            throw new Exception(STR."User with email: \{createUserInput.getEmail()} already exists");
        }

        String slug = createUserInput.getFirstName().toLowerCase() + "-" + createUserInput.getLastName().toLowerCase();
        int num = 1;
        while (userRepository.findBySlug(slug).isPresent()) {
            slug = createUserInput.getFirstName().toLowerCase() + "-" + createUserInput.getLastName().toLowerCase() + "-" + num++;
        }

        List<UserRole> userRoles = userRoleRepository.findAllById(createUserInput.getUserRoleIds());
        User user = User.builder()
                .firstName(createUserInput.getFirstName())
                .lastName(createUserInput.getLastName())
                .email(createUserInput.getEmail())
                .password(passwordEncoder.encode(createUserInput.getPassword()))
                .dateOfBirth(createUserInput.getDateOfBirth())
                .gender(createUserInput.getGender())
                .isActive(true)
                .slug(slug)
                .userRoles(userRoles)
                .build();
        userRepository.save(user);

        var jwToken = jwtService.generateToken(user);

        return AuthenticateUserResponse
                .builder()
                .token(jwToken)
                .build();
    }

    // Get logged-in user
    public UserResponse fetchUser() {
        String token = getBearerTokenHeader();
        String email = jwtService.extractUsername(token);
        User user = userRepository.findByEmail(email).orElseThrow();
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .password(user.getPassword())
                .dateOfBirth(user.getDateOfBirth())
                .gender(user.getGender())
                .address(user.getAddress())
                .isActive(user.getIsActive())
                .slug(user.getSlug())
                .profilePicture(user.getProfilePicture())
                .phoneNumber(user.getPhoneNumber())
                .instagram(user.getInstagram())
                .linkedIn(user.getLinkedIn())
                .userRoles(user.getUserRoles())
                .build();
    }

    // Create a new user
    public UserResponse createUser(CreateUserRequest createUserInput) throws Exception {
        if(userRepository.findByEmail(createUserInput.getEmail()).isPresent()) {
            throw new Exception(STR."User with email: \{createUserInput.getEmail()} already exists");
        }
        List<UserRole> userRoles = userRoleRepository.findAllById(createUserInput.getUserRoleIds());
        User user = User.builder()
                .firstName(createUserInput.getFirstName())
                .lastName(createUserInput.getLastName())
                .email(createUserInput.getEmail())
                .password(passwordEncoder.encode(createUserInput.getPassword()))
                .dateOfBirth(createUserInput.getDateOfBirth())
                .gender(createUserInput.getGender())
                .isActive(true)
                .userRoles(userRoles)
                .build();
        userRepository.save(user);
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .password(user.getPassword())
                .dateOfBirth(user.getDateOfBirth())
                .gender(user.getGender())
                .isActive(user.getIsActive())
                .userRoles(user.getUserRoles())
                .build();
    }

    // Get all users
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> UserResponse.builder()
                        .id(user.getId())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .email(user.getEmail())
                        .password(user.getPassword())
                        .dateOfBirth(user.getDateOfBirth())
                        .gender(user.getGender())
                        .address(user.getAddress())
                        .isActive(user.getIsActive())
                        .slug(user.getSlug())
                        .profilePicture(user.getProfilePicture())
                        .phoneNumber(user.getPhoneNumber())
                        .instagram(user.getInstagram())
                        .linkedIn(user.getLinkedIn())
                        .userRoles(user.getUserRoles())
                        .build())
                .collect(Collectors.toList());
    }

    // Get user by ID
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow();
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .password(user.getPassword())
                .dateOfBirth(user.getDateOfBirth())
                .gender(user.getGender())
                .address(user.getAddress())
                .isActive(user.getIsActive())
                .slug(user.getSlug())
                .profilePicture(user.getProfilePicture())
                .phoneNumber(user.getPhoneNumber())
                .instagram(user.getInstagram())
                .linkedIn(user.getLinkedIn())
                .userRoles(user.getUserRoles())
                .build();
    }

    // Update user
    public UserResponse updateUser(UpdateUserRequest updateUserInput) {
        Optional<User> userOptional = userRepository.findById(updateUserInput.getId());

        if (userOptional.isPresent()) {
            User existingUser = userOptional.get();

            updateUserInput.getFirstName().ifPresent(existingUser::setFirstName);
            updateUserInput.getLastName().ifPresent(existingUser::setLastName);
            updateUserInput.getEmail().ifPresent(existingUser::setEmail);
            updateUserInput.getDateOfBirth().ifPresent(existingUser::setDateOfBirth);
            updateUserInput.getGender().ifPresent(existingUser::setGender);
            updateUserInput.getAddress().ifPresent(existingUser::setAddress);
            updateUserInput.getIsActive().ifPresent(existingUser::setIsActive);
            updateUserInput.getProfilePicture().ifPresent(existingUser::setProfilePicture);
            updateUserInput.getPhoneNumber().ifPresent(existingUser::setPhoneNumber);
            updateUserInput.getInstagram().ifPresent(existingUser::setInstagram);
            updateUserInput.getLinkedIn().ifPresent(existingUser::setLinkedIn);

            if (updateUserInput.getUserRoleIds().isPresent()) {
                List<UserRole> userRoles = userRoleRepository.findAllById(updateUserInput.getUserRoleIds().get());
                if (!userRoles.isEmpty()) {
                    existingUser.setUserRoles(userRoles);
                }
            }

            userRepository.save(existingUser);

            return UserResponse.builder()
                    .id(existingUser.getId())
                    .firstName(existingUser.getFirstName())
                    .lastName(existingUser.getLastName())
                    .email(existingUser.getEmail())
                    .slug(existingUser.getSlug())
                    .dateOfBirth(existingUser.getDateOfBirth())
                    .gender(existingUser.getGender())
                    .address(existingUser.getAddress())
                    .isActive(existingUser.getIsActive())
                    .profilePicture(existingUser.getProfilePicture())
                    .phoneNumber(existingUser.getPhoneNumber())
                    .instagram(existingUser.getInstagram())
                    .linkedIn(existingUser.getLinkedIn())
                    .userRoles(existingUser.getUserRoles())
                    .build();
        }

        return null;
    }

    // Delete all users
    public void deleteAllUsers() {
        userRepository.deleteAll();
    }

    // Delete user
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public List<UserRole> getUserRoles() {
        return userRoleRepository.findAll();
    }

    public List<UserResponse> getUsersByRole(String role) {
        List<User> users = userRepository.findByRole(role);
        return users.stream()
                .map(user -> UserResponse.builder()
                        .id(user.getId())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .email(user.getEmail())
                        .password(user.getPassword())
                        .dateOfBirth(user.getDateOfBirth())
                        .gender(user.getGender())
                        .address(user.getAddress())
                        .isActive(user.getIsActive())
                        .slug(user.getSlug())
                        .profilePicture(user.getProfilePicture())
                        .phoneNumber(user.getPhoneNumber())
                        .instagram(user.getInstagram())
                        .linkedIn(user.getLinkedIn())
                        .userRoles(user.getUserRoles())
                        .build())
                .collect(Collectors.toList());
    }

    public UserResponse updatePassword(String newPassword) throws Exception {
        String token = getBearerTokenHeader();
        String email = jwtService.extractUsername(token);
        User user = userRepository.findByEmail(email).orElseThrow();
        if(newPassword != user.getPassword()){
            user.setPassword(newPassword);
            userRepository.save(user);
            return UserResponse.builder()
                    .id(user.getId())
                    .password(user.getPassword())
                    .build();
        } else {
            throw new Exception("New password can't be the same as old password.");
        }

    }
    public List<UserResponse> getInstructorSearchResults(String query) {
        List<User> users = userRepository.findInstructorsBySearchQuery(query);
        return users.stream()
                .map(user -> UserResponse.builder()
                        .id(user.getId())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .email(user.getEmail())
                        .password(user.getPassword())
                        .dateOfBirth(user.getDateOfBirth())
                        .gender(user.getGender())
                        .address(user.getAddress())
                        .isActive(user.getIsActive())
                        .slug(user.getSlug())
                        .profilePicture(user.getProfilePicture())
                        .phoneNumber(user.getPhoneNumber())
                        .instagram(user.getInstagram())
                        .linkedIn(user.getLinkedIn())
                        .userRoles(user.getUserRoles())
                        .build())
                .collect(Collectors.toList());
    }

    public boolean IsTokenValid() {
        String token = getBearerTokenHeader();
        String email = jwtService.extractUsername(token);
        User user = userRepository.findByEmail(email).orElseThrow();
        return jwtService.isTokenValid(token, user);
    }

    public UserResponse getUserBySlug(String slug) {
        User user = userRepository.findBySlug(slug).orElseThrow();
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .password(user.getPassword())
                .dateOfBirth(user.getDateOfBirth())
                .gender(user.getGender())
                .address(user.getAddress())
                .isActive(user.getIsActive())
                .slug(user.getSlug())
                .profilePicture(user.getProfilePicture())
                .phoneNumber(user.getPhoneNumber())
                .instagram(user.getInstagram())
                .linkedIn(user.getLinkedIn())
                .userRoles(user.getUserRoles())
                .build();
    }

    public boolean isUserStudent() {
        String token = getBearerTokenHeader();
        String email = jwtService.extractUsername(token);
        User user = userRepository.findByEmail(email).orElseThrow();
        List<String> roles = userRepository.findRolesByUserId(user.getId());
        return roles.contains("student");
    }

    public boolean isUserInstructor() {
        String token = getBearerTokenHeader();
        String email = jwtService.extractUsername(token);
        User user = userRepository.findByEmail(email).orElseThrow();
        List<String> roles = userRepository.findRolesByUserId(user.getId());
        return roles.contains("instructor");
    }

    public boolean isUserAdmin() {
        String token = getBearerTokenHeader();
        String email = jwtService.extractUsername(token);
        User user = userRepository.findByEmail(email).orElseThrow();
        return user.getIsAdmin();
    }
}