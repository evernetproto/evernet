package org.evernet.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.evernet.auth.AuthenticatedUserController;
import org.evernet.model.User;
import org.evernet.request.UserPasswordChangeRequest;
import org.evernet.request.UserUpdateRequest;
import org.evernet.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController extends AuthenticatedUserController {

    private final UserService userService;

    @GetMapping("/users/current")
    public User get() {
        checkLocal();
        return userService.get(getUserIdentifier(), getUserNodeIdentifier());
    }

    @PutMapping("/users/current")
    public User update(@Valid @RequestBody UserUpdateRequest request) {
        checkLocal();
        return userService.update(getUserIdentifier(), request, getUserNodeIdentifier());
    }

    @PutMapping("/users/current/password")
    public User changePassword(@Valid @RequestBody UserPasswordChangeRequest request) {
        checkLocal();
        return userService.changePassword(getUserIdentifier(), request, getUserNodeIdentifier());
    }

    @DeleteMapping("/users/current")
    public User delete() {
        checkLocal();
        return userService.delete(getUserIdentifier(), getUserNodeIdentifier());
    }
}