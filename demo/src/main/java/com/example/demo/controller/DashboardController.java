package com.example.demo.controller;

import com.example.demo.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/total-transactions")
    public long getTotalTransactions() {
        return dashboardService.getTotalTransactions();
    }

    @GetMapping("/total-flagged")
    public long getTotalFlaggedTransactions() {
        return dashboardService.getTotalFlaggedTransactions();
    }

    @GetMapping("/pending")
    public long getPendingReviews() {
        return dashboardService.getPendingReviews();
    }

    @GetMapping("/blocked")
    public long getBlockedTransactions() {
        return dashboardService.getBlockedTransactions();
    }

    @GetMapping("/approved")
    public long getApprovedTransactions() {
        return dashboardService.getApprovedTransactions();
    }

    @GetMapping("/total-reviews")
    public long getTotalReviews() {
        return dashboardService.getTotalReviews();
    }
}