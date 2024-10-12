package org.evernet.admin.service;

import lombok.RequiredArgsConstructor;
import org.evernet.admin.model.Admin;
import org.evernet.admin.repository.AdminRepository;
import org.evernet.admin.request.AdminInitRequest;
import org.evernet.admin.request.AdminTokenRequest;
import org.evernet.admin.response.AdminTokenResponse;
import org.evernet.core.auth.AuthenticatedAdmin;
import org.evernet.core.auth.Jwt;
import org.evernet.core.exception.AuthenticationException;
import org.evernet.core.exception.NotAllowedException;
import org.evernet.core.util.Password;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
}
