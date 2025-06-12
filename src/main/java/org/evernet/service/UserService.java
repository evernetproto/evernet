package org.evernet.service;

import lombok.RequiredArgsConstructor;
import org.evernet.auth.Jwt;
import org.evernet.exception.AuthenticationException;
import org.evernet.exception.ClientException;
import org.evernet.exception.NotAllowedException;
import org.evernet.exception.NotFoundException;
import org.evernet.model.Node;
import org.evernet.model.User;
import org.evernet.repository.UserRepository;
import org.evernet.request.*;
import org.evernet.response.UserPasswordResponse;
import org.evernet.response.UserTokenResponse;
import org.evernet.util.Ed25519KeyHelper;
import org.evernet.util.Password;
import org.evernet.util.Random;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.security.GeneralSecurityException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final NodeService nodeService;

    private final Jwt jwt;

    public User signUp(String nodeIdentifier, UserSignUpRequest request) {
        Node node = nodeService.get(nodeIdentifier);

        if (!node.getOpen()) {
            throw new NotAllowedException();
        }

        if (userRepository.existsByIdentifierAndNodeIdentifier(request.getIdentifier(), nodeIdentifier)) {
            throw new ClientException(String.format("User with identifier %s already exists on node %s", request.getIdentifier(), node.getIdentifier()));
        }

        User user = User.builder()
                .nodeIdentifier(node.getIdentifier())
                .identifier(request.getIdentifier())
                .displayName(request.getDisplayName())
                .password(Password.hash(request.getPassword()))
                .build();

        return userRepository.save(user);
    }

    public UserTokenResponse getToken(String nodeIdentifier, UserTokenRequest request) throws GeneralSecurityException {
        Node node = nodeService.get(nodeIdentifier);
        User user = userRepository.findByNodeIdentifierAndIdentifier(nodeIdentifier, request.getIdentifier());

        if (user == null) {
            throw new AuthenticationException();
        }

        if (!Password.verify(request.getPassword(), user.getPassword())) {
            throw new NotAllowedException();
        }

        String token = jwt.getUserToken(user.getIdentifier(), user.getNodeIdentifier(), request.getTargetNodeAddress(), Ed25519KeyHelper.stringToPrivateKey(node.getSigningPrivateKey()));
        return UserTokenResponse.builder().token(token).build();
    }

    public User get(String identifier, String nodeIdentifier) {
        User user = userRepository.findByNodeIdentifierAndIdentifier(nodeIdentifier, identifier);

        if (user == null) {
            throw new NotFoundException(String.format("User %s not found on node %s", identifier, nodeIdentifier));
        }

        return user;
    }

    public User update(String identifier, UserUpdateRequest request, String nodeIdentifier) {
        User user = get(identifier, nodeIdentifier);

        if (StringUtils.hasText(request.getDisplayName())) {
            user.setDisplayName(request.getDisplayName());
        }

        return userRepository.save(user);
    }

    public User changePassword(String identifier, UserPasswordChangeRequest request, String nodeIdentifier) {
        User user = get(identifier, nodeIdentifier);
        user.setPassword(Password.hash(request.getPassword()));
        return userRepository.save(user);
    }

    public User delete(String identifier, String nodeIdentifier) {
        User user = get(identifier, nodeIdentifier);
        userRepository.delete(user);
        return user;
    }

    public UserPasswordResponse add(String nodeIdentifier, UserAdditionRequest request, String creator) {
        Node node = nodeService.get(nodeIdentifier);

        if (userRepository.existsByIdentifierAndNodeIdentifier(request.getIdentifier(), node.getIdentifier())) {
            throw new ClientException(String.format("User %s already exists on node %s", request.getIdentifier(), node.getIdentifier()));
        }

        String password = Random.generateRandomString(16);
        User user = User.builder()
                .nodeIdentifier(node.getIdentifier())
                .identifier(request.getIdentifier())
                .displayName(request.getDisplayName())
                .password(Password.hash(password))
                .creator(creator)
                .build();

        user = userRepository.save(user);
        return UserPasswordResponse.builder().user(user).password(password).build();
    }

    public List<User> list(String nodeIdentifier, Pageable pageable) {
        return userRepository.findByNodeIdentifier(nodeIdentifier, pageable);
    }

    public UserPasswordResponse resetPassword(String identifier, String nodeIdentifier) {
        User user = get(identifier, nodeIdentifier);
        String password = Random.generateRandomString(16);
        user.setPassword(Password.hash(password));
        user = userRepository.save(user);
        return UserPasswordResponse.builder().user(user).password(password).build();
    }
}