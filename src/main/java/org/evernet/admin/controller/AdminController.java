package org.evernet.admin.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.evernet.admin.model.Admin;
import org.evernet.admin.request.AdminPasswordChangeRequest;
import org.evernet.admin.service.AdminService;
import org.evernet.common.auth.AuthenticatedAdminController;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AdminController extends AuthenticatedAdminController {

    private final AdminService adminService;

    @GetMapping("/admins/current")
    public Admin get() {
        return adminService.get(getAdminIdentifier());
    }

    @PutMapping("/admins/current/password")
    public Admin changePassword(@Valid @RequestBody AdminPasswordChangeRequest request) {
        return adminService.changePassword(getAdminIdentifier(), request);
    }
}
