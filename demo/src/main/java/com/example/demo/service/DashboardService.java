package com.example.demo.service;

import com.example.demo.repository.FlaggedTransactionRepository;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.repository.ReviewOutcomeRepository;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final TransactionRepository transactionRepository;
    private final FlaggedTransactionRepository flaggedTransactionRepository;
    private final ReviewOutcomeRepository reviewOutcomeRepository;

    public DashboardService(
            TransactionRepository transactionRepository,
            FlaggedTransactionRepository flaggedTransactionRepository,
            ReviewOutcomeRepository reviewOutcomeRepository) {

        this.transactionRepository = transactionRepository;
        this.flaggedTransactionRepository = flaggedTransactionRepository;
        this.reviewOutcomeRepository = reviewOutcomeRepository;
    }

    // Total number of transactions
    public long getTotalTransactions() {
        return transactionRepository.count();
    }

    // Total number of flagged transactions
    public long getTotalFlaggedTransactions() {
        return flaggedTransactionRepository.count();
    }

    // Total number of pending reviews
    public long getPendingReviews() {
        return flaggedTransactionRepository
                .findAll()
                .stream()
                .filter(flag -> "PENDING".equalsIgnoreCase(flag.getStatus()))
                .count();
    }

    // Total number of blocked transactions
    public long getBlockedTransactions() {
        return flaggedTransactionRepository
                .findAll()
                .stream()
                .filter(flag -> "BLOCKED".equalsIgnoreCase(flag.getStatus()))
                .count();
    }

    // Total number of approved transactions
    public long getApprovedTransactions() {
        return flaggedTransactionRepository
                .findAll()
                .stream()
                .filter(flag -> "APPROVED".equalsIgnoreCase(flag.getStatus()))
                .count();
    }

    // Total number of completed reviews
    public long getTotalReviews() {
        return reviewOutcomeRepository.count();
    }
}