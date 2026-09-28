package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "review_outcomes")
public class ReviewOutcome {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "flagged_transaction_id")
    private FlaggedTransaction flaggedTransaction;

    private String outcome;

    private String comment;

    private LocalDateTime reviewedAt;

    // Default constructor
    public ReviewOutcome() {
    }

    // Parameterized constructor
    public ReviewOutcome(FlaggedTransaction flaggedTransaction,
                         String outcome,
                         String comment,
                         LocalDateTime reviewedAt) {
        this.flaggedTransaction = flaggedTransaction;
        this.outcome = outcome;
        this.comment = comment;
        this.reviewedAt = reviewedAt;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FlaggedTransaction getFlaggedTransaction() {
        return flaggedTransaction;
    }

    public void setFlaggedTransaction(FlaggedTransaction flaggedTransaction) {
        this.flaggedTransaction = flaggedTransaction;
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }
}