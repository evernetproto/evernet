package xyz.evernet.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import xyz.evernet.admin.model.Admin;
import xyz.evernet.admin.repository.AdminRepository;
import xyz.evernet.admin.request.AdminAdditionRequest;
import xyz.evernet.admin.request.AdminInitRequest;
import xyz.evernet.admin.request.AdminPasswordChangeRequest;
import xyz.evernet.admin.request.AdminTokenRequest;
import xyz.evernet.admin.response.AdminPasswordResponse;
import xyz.evernet.admin.response.AdminTokenResponse;
import xyz.evernet.core.auth.AuthenticatedAdmin;
import xyz.evernet.core.auth.Jwt;
import xyz.evernet.core.exception.AuthenticationException;
import xyz.evernet.core.exception.ClientException;
import xyz.evernet.core.exception.NotAllowedException;
import xyz.evernet.core.exception.NotFoundException;
import xyz.evernet.core.util.Password;
import xyz.evernet.core.util.Random;
import xyz.evernet.vertex.service.VertexConfigService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;

    private final VertexConfigService vertexConfigService;

    private final Jwt jwt;

    public Admin init(AdminInitRequest request) {
        if (adminRepository.count() != 0) {
            throw new NotAllowedException();
        }

        Admin admin = Admin.builder()
                .identifier(request.getIdentifier())
                .password(Password.hash(request.getPassword()))
                .build();

        admin = adminRepository.save(admin);
        vertexConfigService.setVertexEndpoint(request.getVertexEndpoint());
        return admin;
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

    public Admin changePassword(String identifier, AdminPasswordChangeRequest request) {
        Admin admin = get(identifier);
        admin.setPassword(Password.hash(request.getPassword()));
        return adminRepository.save(admin);
    }

    public AdminPasswordResponse add(AdminAdditionRequest request, String creator) {
        if (adminRepository.existsByIdentifier(request.getIdentifier())) {
            throw new ClientException(String.format("Admin %s already exists", request.getIdentifier()));
        }

        String password = Random.generateRandomString(16);

        Admin admin = Admin.builder()
                .identifier(request.getIdentifier())
                .password(Password.hash(password))
                .creator(creator)
                .build();

        admin = adminRepository.save(admin);

        return AdminPasswordResponse.builder().admin(admin).password(password).build();
    }

    public List<Admin> list(Pageable pageable) {
        return adminRepository.findAll(pageable).getContent();
    }

    public AdminPasswordResponse resetPassword(String identifier) {
        Admin admin = get(identifier);
        String password = Random.generateRandomString(16);
        admin.setPassword(Password.hash(password));
        admin = adminRepository.save(admin);
        return AdminPasswordResponse.builder().admin(admin).password(password).build();
    }

    public Admin delete(String identifier) {
        Admin admin = get(identifier);
        adminRepository.delete(admin);
        return admin;
    }
}
