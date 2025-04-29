package xyz.evernet.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import xyz.evernet.admin.model.Admin;
import xyz.evernet.admin.repository.AdminRepository;
import xyz.evernet.admin.request.AdminInitRequest;
import xyz.evernet.admin.request.AdminTokenRequest;
import xyz.evernet.admin.response.AdminTokenResponse;
import xyz.evernet.core.auth.AuthenticatedAdmin;
import xyz.evernet.core.auth.Jwt;
import xyz.evernet.core.exception.AuthenticationException;
import xyz.evernet.core.exception.NotAllowedException;
import xyz.evernet.core.util.Password;
import xyz.evernet.vertex.service.VertexConfigService;

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
}
