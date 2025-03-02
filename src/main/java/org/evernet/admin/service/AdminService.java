package org.evernet.admin.service;

import lombok.RequiredArgsConstructor;
import org.evernet.admin.model.Admin;
import org.evernet.admin.repository.AdminRepository;
import org.evernet.admin.request.AdminInitRequest;
import org.evernet.admin.request.AdminTokenRequest;
import org.evernet.admin.response.AdminTokenResponse;
import org.evernet.common.auth.AuthenticatedAdmin;
import org.evernet.common.auth.Jwt;
import org.evernet.common.exception.AuthenticationException;
import org.evernet.common.exception.NotAllowedException;
import org.evernet.common.exception.NotFoundException;
import org.evernet.common.util.Password;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final Jwt jwt;

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

        String token = jwt.getAdminToken(AuthenticatedAdmin.builder().identifier(admin.getIdentifier()).build());
        return AdminTokenResponse.builder().token(token).build();
    }

    public Admin get(String identifier) {
        return adminRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new NotFoundException(String.format("Admin %s not found", identifier)));
    }
}
