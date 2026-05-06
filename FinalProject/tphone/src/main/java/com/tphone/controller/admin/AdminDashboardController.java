package com.tphone.controller.admin;

import com.tphone.dto.response.AdminDashboardResponse;
import com.tphone.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/analytics")
    public AdminDashboardResponse getAnalytics(
            @RequestParam(defaultValue = "day") String period,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        return dashboardService.getDashboard(period, date);
    }
}