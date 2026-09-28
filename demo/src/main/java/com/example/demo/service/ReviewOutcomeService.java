package com.example.demo.service;

import com.example.demo.model.FlaggedTransaction;
import com.example.demo.model.ReviewOutcome;
import com.example.demo.repository.FlaggedTransactionRepository;
import com.example.demo.repository.ReviewOutcomeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewOutcomeService {

    private final ReviewOutcomeRepository reviewOutcomeRepository;
    private final FlaggedTransactionRepository flaggedTransactionRepository;

    public ReviewOutcomeService(
            ReviewOutcomeRepository reviewOutcomeRepository,
            FlaggedTransactionRepository flaggedTransactionRepository) {

        this.reviewOutcomeRepository = reviewOutcomeRepository;
        this.flaggedTransactionRepository = flaggedTransactionRepository;
    }

    // CREATE REVIEW OUTCOME
    public ReviewOutcome saveReviewOutcome(ReviewOutcome reviewOutcome) {

        // Get the ID of the flagged transaction
        Long flaggedTransactionId =
                reviewOutcome.getFlaggedTransaction().getId();

        // Fetch the complete flagged transaction from database
        Optional<FlaggedTransaction> existingFlag =
                flaggedTransactionRepository.findById(flaggedTransactionId);

        if (existingFlag.isEmpty()) {
            return null;
        }

        FlaggedTransaction flaggedTransaction =
                existingFlag.get();

        // Update the status of the real flagged transaction
        flaggedTransaction.setStatus(
                reviewOutcome.getOutcome()
        );

        // Save the updated flagged transaction
        flaggedTransactionRepository.save(flaggedTransaction);

        // Connect the complete flagged transaction to review
        reviewOutcome.setFlaggedTransaction(
                flaggedTransaction
        );

        // Save review time if not provided
        if (reviewOutcome.getReviewedAt() == null) {
            reviewOutcome.setReviewedAt(
                    LocalDateTime.now()
            );
        }

        // Save review outcome
        return reviewOutcomeRepository.save(reviewOutcome);
    }

    // GET ALL REVIEW OUTCOMES
    public List<ReviewOutcome> getAllReviewOutcomes() {

        return reviewOutcomeRepository.findAll();
    }

    // GET REVIEW OUTCOME BY ID
    public Optional<ReviewOutcome> getReviewOutcomeById(Long id) {

        return reviewOutcomeRepository.findById(id);
    }

    // UPDATE REVIEW OUTCOME
    public ReviewOutcome updateReviewOutcome(
            Long id,
            ReviewOutcome reviewOutcome) {

        Optional<ReviewOutcome> existingOutcome =
                reviewOutcomeRepository.findById(id);

        if (existingOutcome.isPresent()) {

            ReviewOutcome existing =
                    existingOutcome.get();

            // Get the new flagged transaction ID
            Long flaggedTransactionId =
                    reviewOutcome.getFlaggedTransaction().getId();

            // Fetch complete flagged transaction
            Optional<FlaggedTransaction> existingFlag =
                    flaggedTransactionRepository
                            .findById(flaggedTransactionId);

            if (existingFlag.isEmpty()) {
                return null;
            }

            FlaggedTransaction flaggedTransaction =
                    existingFlag.get();

            // Update review details
            existing.setFlaggedTransaction(
                    flaggedTransaction
            );

            existing.setOutcome(
                    reviewOutcome.getOutcome()
            );

            existing.setComment(
                    reviewOutcome.getComment()
            );

            existing.setReviewedAt(
                    reviewOutcome.getReviewedAt()
            );

            // Update flagged transaction status
            flaggedTransaction.setStatus(
                    reviewOutcome.getOutcome()
            );

            flaggedTransactionRepository.save(
                    flaggedTransaction
            );

            return reviewOutcomeRepository.save(existing);
        }

        return null;
    }

    // DELETE REVIEW OUTCOME
    public void deleteReviewOutcome(Long id) {

        reviewOutcomeRepository.deleteById(id);
    }
}