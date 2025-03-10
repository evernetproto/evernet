package org.evernet.admin.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.evernet.admin.model.Admin;
import org.evernet.admin.request.AdminAdditionRequest;
import org.evernet.admin.request.AdminPasswordChangeRequest;
import org.evernet.admin.response.AdminPasswordResponse;
import org.evernet.admin.service.AdminService;
import org.evernet.auth.AuthenticatedAdminController;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping("/admins")
    public AdminPasswordResponse add(@Valid @RequestBody AdminAdditionRequest request) {
        return adminService.add(request, getAdminIdentifier());
    }

    @GetMapping("/admins")
    public List<Admin> list(Pageable pageable) {
        return adminService.list(pageable);
    }

    @GetMapping("/admins/{identifier}")
    public Admin get(@PathVariable String identifier) {
        return adminService.get(identifier);
    }

    @PutMapping("/admins/{identifier}/password")
    public AdminPasswordResponse resetPassword(@PathVariable String identifier) {
        return adminService.resetPassword(identifier);
    }

    @DeleteMapping("/admins/{identifier}")
    public Admin delete(@PathVariable String identifier) {
        return adminService.delete(identifier);
    }
}
