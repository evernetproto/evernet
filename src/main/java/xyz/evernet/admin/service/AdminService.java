package xyz.evernet.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import xyz.evernet.admin.model.Admin;
import xyz.evernet.admin.repository.AdminRepository;
import xyz.evernet.admin.request.AdminInitRequest;
import xyz.evernet.core.exception.NotAllowedException;
import xyz.evernet.core.util.Password;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;

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
}
