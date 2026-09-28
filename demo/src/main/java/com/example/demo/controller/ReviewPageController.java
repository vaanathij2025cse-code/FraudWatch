package com.example.demo.controller;

import com.example.demo.model.FlaggedTransaction;
import com.example.demo.model.ReviewOutcome;
import com.example.demo.service.FlaggedTransactionService;
import com.example.demo.service.ReviewOutcomeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
public class ReviewPageController {

    private final FlaggedTransactionService flaggedTransactionService;
    private final ReviewOutcomeService reviewOutcomeService;

    public ReviewPageController(
            FlaggedTransactionService flaggedTransactionService,
            ReviewOutcomeService reviewOutcomeService) {
        this.flaggedTransactionService = flaggedTransactionService;
        this.reviewOutcomeService = reviewOutcomeService;
    }

    @GetMapping("/review/{id}")
    public String showReviewPage(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes) {

        Optional<FlaggedTransaction> flagOpt =
                flaggedTransactionService.getFlaggedTransactionById(id);

        if (flagOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Flagged transaction #" + id + " was not found."
            );
            return "redirect:/flagged-page";
        }

        FlaggedTransaction flaggedTransaction = flagOpt.get();
        model.addAttribute("flaggedTransaction", flaggedTransaction);

        // Check if there is an existing review outcome for this flagged transaction
        List<ReviewOutcome> allOutcomes = reviewOutcomeService.getAllReviewOutcomes();
        ReviewOutcome existingReview = allOutcomes.stream()
                .filter(ro -> ro.getFlaggedTransaction() != null &&
                        id.equals(ro.getFlaggedTransaction().getId()))
                .findFirst()
                .orElse(null);

        model.addAttribute("existingReview", existingReview);
        model.addAttribute("activePage", "reviews");
        return "review";
    }

    @PostMapping("/review/{id}")
    public String submitReview(
            @PathVariable Long id,
            @RequestParam("decision") String decision,
            @RequestParam(value = "comment", required = false) String comment,
            RedirectAttributes redirectAttributes) {

        Optional<FlaggedTransaction> flagOpt =
                flaggedTransactionService.getFlaggedTransactionById(id);

        if (flagOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Flagged transaction #" + id + " not found."
            );
            return "redirect:/flagged-page";
        }

        FlaggedTransaction flaggedTransaction = flagOpt.get();

        String outcomeStatus = "APPROVE".equalsIgnoreCase(decision) || "APPROVED".equalsIgnoreCase(decision)
                ? "APPROVED"
                : "BLOCKED";

        String reviewComment = (comment != null && !comment.trim().isEmpty())
                ? comment.trim()
                : "Manual review completed: Transaction " + outcomeStatus.toLowerCase();

        ReviewOutcome reviewOutcome = new ReviewOutcome();
        reviewOutcome.setFlaggedTransaction(flaggedTransaction);
        reviewOutcome.setOutcome(outcomeStatus);
        reviewOutcome.setComment(reviewComment);
        reviewOutcome.setReviewedAt(LocalDateTime.now());

        try {
            reviewOutcomeService.saveReviewOutcome(reviewOutcome);
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Review completed successfully! Transaction #" +
                            flaggedTransaction.getTransaction().getId() +
                            " marked as " + outcomeStatus + "."
            );
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Failed to record review: " + e.getMessage()
            );
        }

        return "redirect:/flagged-page";
    }

    @GetMapping("/reviews-page")
    public String showReviewsOverviewPage(Model model) {
        List<ReviewOutcome> reviewOutcomes = reviewOutcomeService.getAllReviewOutcomes();
        List<FlaggedTransaction> allFlagged = flaggedTransactionService.getAllFlaggedTransactions();

        long pendingCount = allFlagged.stream()
                .filter(f -> "PENDING".equalsIgnoreCase(f.getStatus()))
                .count();

        model.addAttribute("reviewOutcomes", reviewOutcomes);
        model.addAttribute("allFlagged", allFlagged);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("activePage", "reviews");
        return "reviews-overview";
    }
}
