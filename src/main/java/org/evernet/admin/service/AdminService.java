package org.evernet.admin.service;

import lombok.RequiredArgsConstructor;
import org.evernet.admin.model.Admin;
import org.evernet.admin.repository.AdminRepository;
import org.evernet.admin.request.AdminInitRequest;
import org.evernet.core.exception.NotAllowedException;
import org.evernet.core.util.Password;
import org.springframework.stereotype.Service;

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
