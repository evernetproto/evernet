package org.evernet.admin.service;

import lombok.RequiredArgsConstructor;
import org.evernet.admin.model.Admin;
import org.evernet.admin.repository.AdminRepository;
import org.evernet.admin.request.AdminAdditionRequest;
import org.evernet.admin.request.AdminInitRequest;
import org.evernet.admin.request.AdminPasswordChangeRequest;
import org.evernet.admin.request.AdminTokenRequest;
import org.evernet.admin.response.AdminPasswordResponse;
import org.evernet.admin.response.AdminTokenResponse;
import org.evernet.core.auth.AuthenticatedAdmin;
import org.evernet.core.auth.Jwt;
import org.evernet.core.exception.AuthenticationException;
import org.evernet.core.exception.ClientException;
import org.evernet.core.exception.NotAllowedException;
import org.evernet.core.exception.NotFoundException;
import org.evernet.core.util.Password;
import org.evernet.core.util.Random;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;

    private final Jwt jwt;

    @Value("${evernet.vertex}")
    private String vertex;

    public Admin init(AdminInitRequest request) {
        if (adminRepository.count() != 0) {
            throw new NotAllowedException();
        }

        Admin admin = Admin.builder()
                .identifier(request.getIdentifier())
                .password(Password.hash(request.getPassword()))
                .creator(request.getIdentifier())
                .build();

        return adminRepository.save(admin);
    }

    public AdminTokenResponse getToken(AdminTokenRequest request) {
        Admin admin = adminRepository.findByIdentifier(request.getIdentifier())
                .orElseThrow(AuthenticationException::new);

        if (!Password.verify(request.getPassword(), admin.getPassword())) {
            throw new AuthenticationException();
        }

        String token = jwt.getAdminToken(AuthenticatedAdmin.builder()
                .identifier(request.getIdentifier())
                .build(), vertex, vertex);

        return AdminTokenResponse.builder().token(token).build();
    }

    public Admin get(String identifier) {
        return adminRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new NotFoundException(String.format("Admin %s not found", identifier)));
    }

    public Admin changePassword(String identifier, AdminPasswordChangeRequest request) {
        Admin admin = get(identifier);
        admin.setPassword(Password.hash(request.getPassword()));
        return adminRepository.save(admin);
    }

    public AdminPasswordResponse add(AdminAdditionRequest request, AuthenticatedAdmin creator) {
        if (exists(request.getIdentifier())) {
            throw new ClientException(String.format("Admin %s already exists", request.getIdentifier()));
        }

        String password = Random.generateRandomString(16);

        Admin admin = Admin.builder()
                .identifier(request.getIdentifier())
                .password(Password.hash(password))
                .creator(creator.getIdentifier())
                .build();

        admin = adminRepository.save(admin);

        return AdminPasswordResponse.builder()
                .admin(admin)
                .password(password)
                .build();
    }

    public Admin delete(String identifier) {
        Admin admin = get(identifier);
        adminRepository.delete(admin);
        return admin;
    }

    public Page<Admin> list(Pageable pageable) {
        return adminRepository.findAll(pageable);
    }

    public AdminPasswordResponse resetPassword(String identifier) {
        Admin admin = get(identifier);
        String password = Random.generateRandomString(16);
        admin.setPassword(Password.hash(password));
        admin = adminRepository.save(admin);
        return AdminPasswordResponse.builder()
                .password(password)
                .admin(admin)
                .build();
    }

    private Boolean exists(String identifier) {
        return adminRepository.existsByIdentifier(identifier);
    }
}
