package xyz.evernet.admin.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import xyz.evernet.admin.model.Admin;
import xyz.evernet.admin.request.AdminPasswordChangeRequest;
import xyz.evernet.admin.service.AdminService;
import xyz.evernet.core.auth.AuthenticatedAdminController;

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
