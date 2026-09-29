package com.example.demo.config;

import com.example.demo.model.FlaggedTransaction;
import com.example.demo.model.ReviewOutcome;
import com.example.demo.model.Rule;
import com.example.demo.model.Transaction;
import com.example.demo.repository.FlaggedTransactionRepository;
import com.example.demo.repository.ReviewOutcomeRepository;
import com.example.demo.repository.RuleRepository;
import com.example.demo.repository.TransactionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RuleRepository ruleRepository;
    private final TransactionRepository transactionRepository;
    private final FlaggedTransactionRepository flaggedTransactionRepository;
    private final ReviewOutcomeRepository reviewOutcomeRepository;

    public DataInitializer(RuleRepository ruleRepository,
                           TransactionRepository transactionRepository,
                           FlaggedTransactionRepository flaggedTransactionRepository,
                           ReviewOutcomeRepository reviewOutcomeRepository) {
        this.ruleRepository = ruleRepository;
        this.transactionRepository = transactionRepository;
        this.flaggedTransactionRepository = flaggedTransactionRepository;
        this.reviewOutcomeRepository = reviewOutcomeRepository;
    }

    @Override
    public void run(String... args) {
        // Only seed if database is completely empty
        if (ruleRepository.count() > 0 || transactionRepository.count() > 0) {
            return;
        }

        // 1. Seed Rules
        Rule r1 = ruleRepository.save(new Rule("High Amount Rule", 10000.0, 0, 0, true));
        Rule r2 = ruleRepository.save(new Rule("Multiple Transactions Rule", 0.0, 3, 15, true));

        // 2. Seed Transactions
        Transaction tx1 = transactionRepository.save(new Transaction("ACC001", "ACC002", 5000.0, LocalDateTime.parse("2026-09-28T05:00:00")));
        Transaction tx2 = transactionRepository.save(new Transaction("ACC003", "ACC004", 15000.0, LocalDateTime.parse("2026-09-28T09:40:00")));
        Transaction tx3 = transactionRepository.save(new Transaction("ACC001", "ACC005", 2500.0, LocalDateTime.parse("2026-09-28T09:45:00")));
        Transaction tx4 = transactionRepository.save(new Transaction("ACC001", "ACC006", 3000.0, LocalDateTime.parse("2026-09-28T09:50:00")));
        Transaction tx5 = transactionRepository.save(new Transaction("ACC001", "ACC007", 4000.0, LocalDateTime.parse("2026-09-28T09:55:00")));
        Transaction tx6 = transactionRepository.save(new Transaction("ACC010", "ACC020", 20000.0, LocalDateTime.parse("2026-09-28T10:30:00")));
        Transaction tx7 = transactionRepository.save(new Transaction("ACC100", "ACC200", 25000.0, LocalDateTime.parse("2026-09-28T11:00:00")));
        Transaction tx8 = transactionRepository.save(new Transaction("ACC500", "ACC601", 1000.0, LocalDateTime.parse("2026-09-28T11:30:00")));
        Transaction tx9 = transactionRepository.save(new Transaction("ACC500", "ACC602", 1200.0, LocalDateTime.parse("2026-09-28T11:35:00")));
        Transaction tx10 = transactionRepository.save(new Transaction("ACC500", "ACC604", 1800.0, LocalDateTime.parse("2026-09-28T11:42:00")));
        Transaction tx11 = transactionRepository.save(new Transaction("ACC500", "ACC604", 1900.0, LocalDateTime.parse("2026-09-28T11:42:00")));
        Transaction tx12 = transactionRepository.save(new Transaction("ACC-VAANATHI", "ACC-RECIPIENT", 16500.0, LocalDateTime.parse("2026-09-28T16:20:00")));
        Transaction tx13 = transactionRepository.save(new Transaction("ACC-890", "ACC-898", 100000.0, LocalDateTime.parse("2026-09-28T17:31:00")));

        // 3. Seed Flagged Transactions
        FlaggedTransaction ft1 = flaggedTransactionRepository.save(new FlaggedTransaction(tx7, r1, "Transaction amount exceeded the allowed threshold", "BLOCKED"));
        FlaggedTransaction ft2 = flaggedTransactionRepository.save(new FlaggedTransaction(tx11, r2, "Multiple transactions detected from the same account within the configured time window", "BLOCKED"));
        FlaggedTransaction ft3 = flaggedTransactionRepository.save(new FlaggedTransaction(tx12, r1, "Transaction amount exceeded the allowed threshold", "APPROVED"));
        FlaggedTransaction ft4 = flaggedTransactionRepository.save(new FlaggedTransaction(tx13, r1, "Transaction amount exceeded the allowed threshold", "BLOCKED"));

        // 4. Seed Review Outcomes
        reviewOutcomeRepository.save(new ReviewOutcome(ft1, "BLOCKED", "Suspicious transaction detected", LocalDateTime.parse("2026-09-28T12:00:00")));
        reviewOutcomeRepository.save(new ReviewOutcome(ft2, "BLOCKED", "Suspicious transaction detected", LocalDateTime.parse("2026-09-28T12:00:00")));
        reviewOutcomeRepository.save(new ReviewOutcome(ft3, "APPROVED", "User verified by KYC identity check", LocalDateTime.parse("2026-09-28T16:21:16")));
        reviewOutcomeRepository.save(new ReviewOutcome(ft4, "BLOCKED", "Manual review completed: Transaction blocked", LocalDateTime.parse("2026-09-28T17:33:18")));
    }
}
