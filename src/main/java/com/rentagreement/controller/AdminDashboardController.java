package com.rentagreement.controller;

import com.rentagreement.dto.dashboard.AdminDashboardResponseDTO;
import com.rentagreement.response.ApiResponse;
import com.rentagreement.service.AdminDashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/dashboard")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminDashboardController {

    private final AdminDashboardService dashboardService;

    public AdminDashboardController(
            AdminDashboardService dashboardService
    ) {

        this.dashboardService = dashboardService;

    }

    @GetMapping
    public ResponseEntity<ApiResponse<AdminDashboardResponseDTO>>
    getDashboardStats() {

        return ResponseEntity.ok(

                new ApiResponse<>(
                        true,
                        "Admin Dashboard Stats Fetched Successfully",
                        dashboardService.getDashboardStats()
                )

        );

    }

}