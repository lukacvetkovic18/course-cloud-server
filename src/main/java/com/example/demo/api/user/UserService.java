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
                .profilePicture(user.getProfilePicture())
                .phoneNumber(user.getPhoneNumber())
                .instagram(user.getInstagram())
                .linkedIn(user.getLinkedIn())
                .userRoles(user.getUserRoles())
                .build();
    }

    // Update user
    public UserResponse updateUser(UpdateUserRequest updateUserInput) {
        Optional<User> user = userRepository.findById(updateUserInput.getId());
        List<UserRole> userRoles = new ArrayList<>();
        if(updateUserInput.getUserRoleIds() != null) userRoles = userRoleRepository.findAllById(updateUserInput.getUserRoleIds().get());
        if (user.isPresent()) {
            User existingUser = user.get();
            if(updateUserInput.getFirstName() != null) existingUser.setFirstName(updateUserInput.getFirstName().get());
            if(updateUserInput.getLastName() != null) existingUser.setLastName(updateUserInput.getLastName().get());
            if(updateUserInput.getEmail() != null) existingUser.setEmail(updateUserInput.getEmail().get());
            if(updateUserInput.getDateOfBirth() != null) existingUser.setDateOfBirth(updateUserInput.getDateOfBirth().get());
            if(updateUserInput.getGender() != null) existingUser.setGender(updateUserInput.getGender().get());
            if(updateUserInput.getAddress() != null) existingUser.setAddress(updateUserInput.getAddress().get());
            if(updateUserInput.getIsActive() != null) existingUser.setIsActive(updateUserInput.getIsActive().get());
            if(updateUserInput.getProfilePicture() != null) existingUser.setProfilePicture(updateUserInput.getProfilePicture().get());
            if(updateUserInput.getPhoneNumber() != null) existingUser.setPhoneNumber(updateUserInput.getPhoneNumber().get());
            if(updateUserInput.getInstagram() != null) existingUser.setInstagram(updateUserInput.getInstagram().get());
            if(updateUserInput.getLinkedIn() != null) existingUser.setLinkedIn(updateUserInput.getLinkedIn().get());
            if(!userRoles.isEmpty()) existingUser.setUserRoles(userRoles);
            userRepository.save(existingUser);
            return UserResponse.builder()
                    .id(existingUser.getId())
                    .firstName(existingUser.getFirstName())
                    .lastName(existingUser.getLastName())
                    .email(existingUser.getEmail())
                    .password(existingUser.getPassword())
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
                        .isActive(user.getIsActive())
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
                        .isActive(user.getIsActive())
                        .profilePicture(user.getProfilePicture())
                        .userRoles(user.getUserRoles())
                        .build())
                .collect(Collectors.toList());
    }
}