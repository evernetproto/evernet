package org.evernet.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.evernet.auth.AuthenticatedAdminController;
import org.evernet.model.User;
import org.evernet.request.UserAdditionRequest;
import org.evernet.request.UserUpdateRequest;
import org.evernet.response.UserPasswordResponse;
import org.evernet.service.UserService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admins")
@RequiredArgsConstructor
public class UserAdminController extends AuthenticatedAdminController {

    private final UserService userService;

    @PostMapping("/nodes/{nodeIdentifier}/users")
    public UserPasswordResponse add(@PathVariable String nodeIdentifier, @Valid @RequestBody UserAdditionRequest request) {
        return userService.add(nodeIdentifier, request, getAdminIdentifier());
    }

    @GetMapping("/nodes/{nodeIdentifier}/users")
    public List<User> list(@PathVariable String nodeIdentifier, Pageable pageable) {
        return userService.list(nodeIdentifier, pageable);
    }

    @GetMapping("/nodes/{nodeIdentifier}/users/{userIdentifier}")
    public User get(@PathVariable String nodeIdentifier, @PathVariable String userIdentifier) {
        return userService.get(userIdentifier, nodeIdentifier);
    }

    @PutMapping("/nodes/{nodeIdentifier}/users/{userIdentifier}")
    public User update(@PathVariable String nodeIdentifier, @PathVariable String userIdentifier, @Valid @RequestBody UserUpdateRequest request) {
        return userService.update(userIdentifier, request, nodeIdentifier);
    }

    @DeleteMapping("/nodes/{nodeIdentifier}/users/{userIdentifier}")
    public User delete(@PathVariable String nodeIdentifier, @PathVariable String userIdentifier) {
        return userService.delete(userIdentifier, nodeIdentifier);
    }

    @PutMapping("/nodes/{nodeIdentifier}/users/{userIdentifier}/password")
    public UserPasswordResponse resetPassword(@PathVariable String nodeIdentifier, @PathVariable String userIdentifier) {
        return userService.resetPassword(userIdentifier, nodeIdentifier);
    }
}