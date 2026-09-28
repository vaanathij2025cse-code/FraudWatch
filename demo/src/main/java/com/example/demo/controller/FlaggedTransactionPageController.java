package com.example.demo.controller;

import com.example.demo.model.FlaggedTransaction;
import com.example.demo.service.FlaggedTransactionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class FlaggedTransactionPageController {

    private final FlaggedTransactionService flaggedTransactionService;

    public FlaggedTransactionPageController(
            FlaggedTransactionService flaggedTransactionService) {
        this.flaggedTransactionService = flaggedTransactionService;
    }

    @GetMapping("/flagged-page")
    public String showFlaggedPage(Model model) {
        List<FlaggedTransaction> flaggedList =
                flaggedTransactionService.getAllFlaggedTransactions();

        model.addAttribute("flaggedTransactions", flaggedList);
        model.addAttribute("activePage", "flagged");
        return "flagged-transactions";
    }

    @GetMapping("/flagged-page/delete/{id}")
    public String deleteFlagged(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        try {
            flaggedTransactionService.deleteFlaggedTransaction(id);
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Flagged record #" + id + " deleted successfully."
            );
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Could not delete flagged record #" + id + ": " + e.getMessage()
            );
        }

        return "redirect:/flagged-page";
    }
}
