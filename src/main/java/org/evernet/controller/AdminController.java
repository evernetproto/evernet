package org.evernet.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.evernet.auth.AuthenticatedAdminController;
import org.evernet.model.Admin;
import org.evernet.request.AdminAdditionRequest;
import org.evernet.request.AdminPasswordChangeRequest;
import org.evernet.response.AdminPasswordResponse;
import org.evernet.service.AdminService;
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

    @PutMapping("/admins/{identifier}")
    public AdminPasswordResponse resetPassword(@PathVariable String identifier) {
        return adminService.resetPassword(identifier);
    }

    @DeleteMapping("/admins/{identifier}")
    public Admin delete(@PathVariable String identifier) {
        return adminService.delete(identifier);
    }
}
