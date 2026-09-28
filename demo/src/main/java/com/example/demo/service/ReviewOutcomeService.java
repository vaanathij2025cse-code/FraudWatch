package com.example.demo.service;

import com.example.demo.model.ReviewOutcome;
import com.example.demo.repository.ReviewOutcomeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewOutcomeService {

    private final ReviewOutcomeRepository reviewOutcomeRepository;

    public ReviewOutcomeService(
            ReviewOutcomeRepository reviewOutcomeRepository) {
        this.reviewOutcomeRepository = reviewOutcomeRepository;
    }

    public ReviewOutcome saveReviewOutcome(ReviewOutcome reviewOutcome) {
        return reviewOutcomeRepository.save(reviewOutcome);
    }

    public List<ReviewOutcome> getAllReviewOutcomes() {
        return reviewOutcomeRepository.findAll();
    }

    public Optional<ReviewOutcome> getReviewOutcomeById(Long id) {
        return reviewOutcomeRepository.findById(id);
    }

    public void deleteReviewOutcome(Long id) {
        reviewOutcomeRepository.deleteById(id);
    }
    public ReviewOutcome updateReviewOutcome(
        Long id,
        ReviewOutcome reviewOutcome) {

    Optional<ReviewOutcome> existingOutcome =
            reviewOutcomeRepository.findById(id);

    if (existingOutcome.isPresent()) {

        ReviewOutcome existing = existingOutcome.get();

        existing.setFlaggedTransaction(
                reviewOutcome.getFlaggedTransaction());

        existing.setOutcome(reviewOutcome.getOutcome());
        existing.setComment(reviewOutcome.getComment());
        existing.setReviewedAt(reviewOutcome.getReviewedAt());

        return reviewOutcomeRepository.save(existing);
    }

    return null;
}
}
