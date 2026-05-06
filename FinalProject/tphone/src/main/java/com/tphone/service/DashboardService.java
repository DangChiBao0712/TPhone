package com.tphone.service;

import com.tphone.dto.response.AdminDashboardResponse;

import java.time.LocalDate;

public interface DashboardService {
    AdminDashboardResponse getDashboard(String period, LocalDate date);
}