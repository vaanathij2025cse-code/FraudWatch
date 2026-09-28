package com.example.demo.controller;

import com.example.demo.model.ReviewOutcome;
import com.example.demo.service.ReviewOutcomeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/review-outcomes")
public class ReviewOutcomeController {

    private final ReviewOutcomeService reviewOutcomeService;

    public ReviewOutcomeController(
            ReviewOutcomeService reviewOutcomeService) {

        this.reviewOutcomeService = reviewOutcomeService;
    }

    // CREATE
    @PostMapping
    public ReviewOutcome createReviewOutcome(
            @RequestBody ReviewOutcome reviewOutcome) {

        return reviewOutcomeService
                .saveReviewOutcome(reviewOutcome);
    }

    // READ ALL
    @GetMapping
    public List<ReviewOutcome> getAllReviewOutcomes() {

        return reviewOutcomeService
                .getAllReviewOutcomes();
    }

    // READ ONE
    @GetMapping("/{id}")
    public ReviewOutcome getReviewOutcomeById(
            @PathVariable Long id) {

        return reviewOutcomeService
                .getReviewOutcomeById(id)
                .orElse(null);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ReviewOutcome updateReviewOutcome(
            @PathVariable Long id,
            @RequestBody ReviewOutcome reviewOutcome) {

        return reviewOutcomeService
                .updateReviewOutcome(id, reviewOutcome);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public String deleteReviewOutcome(
            @PathVariable Long id) {

        reviewOutcomeService
                .deleteReviewOutcome(id);

        return "Review outcome deleted successfully";
    }
}