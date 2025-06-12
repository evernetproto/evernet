package org.evernet.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.evernet.model.User;
import org.evernet.request.UserSignUpRequest;
import org.evernet.request.UserTokenRequest;
import org.evernet.response.UserTokenResponse;
import org.evernet.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.security.GeneralSecurityException;

@RestController
@RequestMapping("/api/v1/nodes/{nodeIdentifier}")
@RequiredArgsConstructor
public class UserAccountController {

    private final UserService userService;

    @PostMapping("/users/signup")
    public User signUp(@PathVariable String nodeIdentifier, @Valid @RequestBody UserSignUpRequest request) {
        return userService.signUp(nodeIdentifier, request);
    }

    @PostMapping("/users/token")
    public UserTokenResponse getToken(@PathVariable String nodeIdentifier, @Valid @RequestBody UserTokenRequest request) throws GeneralSecurityException {
        return userService.getToken(nodeIdentifier, request);
    }
}