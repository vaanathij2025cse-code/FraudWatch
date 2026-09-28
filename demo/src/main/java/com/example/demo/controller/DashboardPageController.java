package com.example.demo.controller;

import com.example.demo.service.DashboardService;
import com.example.demo.service.FlaggedTransactionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardPageController {

    private final DashboardService dashboardService;
    private final FlaggedTransactionService flaggedTransactionService;

    public DashboardPageController(
            DashboardService dashboardService,
            FlaggedTransactionService flaggedTransactionService) {
        this.dashboardService = dashboardService;
        this.flaggedTransactionService = flaggedTransactionService;
    }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        model.addAttribute("totalTransactions", dashboardService.getTotalTransactions());
        model.addAttribute("totalFlagged", dashboardService.getTotalFlaggedTransactions());
        model.addAttribute("pendingReviews", dashboardService.getPendingReviews());
        model.addAttribute("blockedTransactions", dashboardService.getBlockedTransactions());
        model.addAttribute("approvedTransactions", dashboardService.getApprovedTransactions());
        model.addAttribute("totalReviews", dashboardService.getTotalReviews());
        model.addAttribute("flaggedTransactions", flaggedTransactionService.getAllFlaggedTransactions());
        model.addAttribute("activePage", "dashboard");

        return "dashboard";
    }
}