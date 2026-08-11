package com.rentagreement.controller;

import com.rentagreement.dto.dashboard.BrokerDashboardResponseDTO;
import com.rentagreement.response.ApiResponse;
import com.rentagreement.service.BrokerDashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/broker/dashboard")
@CrossOrigin(origins = "http://localhost:5173")
public class BrokerDashboardController {

    private final BrokerDashboardService dashboardService;


    public BrokerDashboardController(
            BrokerDashboardService dashboardService
    ) {

        this.dashboardService = dashboardService;

    }


    @GetMapping
    public ResponseEntity<
            ApiResponse<BrokerDashboardResponseDTO>
            > getDashboardStats() {

        return ResponseEntity.ok(

                new ApiResponse<>(

                        true,

                        "Broker Dashboard Stats Fetched Successfully",

                        dashboardService.getDashboardStats()

                )

        );

    }

}