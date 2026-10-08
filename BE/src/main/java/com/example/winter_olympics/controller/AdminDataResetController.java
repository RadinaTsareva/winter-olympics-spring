package com.example.winter_olympics.controller;

import com.example.winter_olympics.dto.AdminDataResetRequest;
import com.example.winter_olympics.dto.AdminDataResetResponse;
import com.example.winter_olympics.service.AdminDataResetService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminDataResetController {

    private final AdminDataResetService resetService;

    public AdminDataResetController(AdminDataResetService resetService) {
        this.resetService = resetService;
    }

    @PostMapping("/reset-data")
    public AdminDataResetResponse resetData(
            @RequestBody AdminDataResetRequest request,
            Authentication authentication
    ) {
        return resetService.resetAllData(authentication.getName(), request.confirmation());
    }
}
