package org.evernet.admin.controller;

import lombok.RequiredArgsConstructor;
import org.evernet.admin.model.Admin;
import org.evernet.admin.service.AdminService;
import org.evernet.common.auth.AuthenticatedAdminController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AdminController extends AuthenticatedAdminController {

    private final AdminService adminService;

    @GetMapping("/admins/current")
    public Admin get() {
        return adminService.get(getAdminIdentifier());
    }
}
