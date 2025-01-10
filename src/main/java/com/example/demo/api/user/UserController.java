package com.example.demo.api.user;

import com.example.demo.api.user.userModels.*;
import com.example.demo.api.userRole.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(path = "/login")
    public AuthenticateUserResponse loginUser(@RequestBody LoginUserRequest loginInfo) {
        return userService.loginUser(loginInfo);
    }

    @PostMapping(path = "/register")
    public AuthenticateUserResponse registerUser(@RequestBody CreateUserRequest user) throws Exception {
        return userService.registerUser(user);
    }

    @GetMapping(path = "/logged-in")
    public UserResponse getLoggedInUser() {
        return userService.fetchUser();
    }

    @PostMapping
    public UserResponse createUser(@RequestBody CreateUserRequest user) throws Exception {
        return userService.createUser(user);
    }

    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping(path = "/{id}")
    public UserResponse getUserById(@PathVariable("id") long id) {
        return userService.getUserById(id);
    }

    @PutMapping
    public UserResponse updateUser(@RequestBody UpdateUserRequest user) {
        return userService.updateUser(user);
    }

    @DeleteMapping
    public void deleteAllUsers() {
        userService.deleteAllUsers();
    }

    @DeleteMapping(path = "/{id}")
    public void deleteUser(@PathVariable("id") long id) {
        userService.deleteUser(id);
    }

    @GetMapping(path = "/roles")
    public List<UserRole> getUserRoles() {
        return userService.getUserRoles();
    }

    @GetMapping(path = "/role/{role}")
    public List<UserResponse> getUsersByRole(@PathVariable("role") String role) {
        return userService.getUsersByRole(role);
    }

    @PutMapping(path = "/password")
    public UserResponse updatePassword(@RequestBody String newPassword) throws Exception {
        return userService.updatePassword(newPassword);
    }

}
