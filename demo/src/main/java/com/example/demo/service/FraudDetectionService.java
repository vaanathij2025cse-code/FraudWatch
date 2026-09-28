package com.example.demo.service;

import com.example.demo.model.FlaggedTransaction;
import com.example.demo.model.Rule;
import com.example.demo.model.Transaction;
import com.example.demo.repository.FlaggedTransactionRepository;
import com.example.demo.repository.RuleRepository;
import com.example.demo.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FraudDetectionService {

    private final RuleRepository ruleRepository;
    private final FlaggedTransactionRepository flaggedTransactionRepository;
    private final TransactionRepository transactionRepository;

    public FraudDetectionService(
            RuleRepository ruleRepository,
            FlaggedTransactionRepository flaggedTransactionRepository,
            TransactionRepository transactionRepository) {

        this.ruleRepository = ruleRepository;
        this.flaggedTransactionRepository = flaggedTransactionRepository;
        this.transactionRepository = transactionRepository;
    }

    public void checkTransaction(Transaction transaction) {

        List<Rule> rules = ruleRepository.findAll();

        for (Rule rule : rules) {

            // Ignore disabled rules
            if (!rule.isEnabled()) {
                continue;
            }

            // RULE 1: HIGH AMOUNT
            if (rule.getAmountThreshold() > 0 &&
                    transaction.getAmount() > rule.getAmountThreshold()) {

                FlaggedTransaction flaggedTransaction =
                        new FlaggedTransaction();

                flaggedTransaction.setTransaction(transaction);
                flaggedTransaction.setRule(rule);
                flaggedTransaction.setReason(
                        "Transaction amount exceeded the allowed threshold"
                );
                flaggedTransaction.setStatus("PENDING");

                flaggedTransactionRepository.save(flaggedTransaction);
            }

            // RULE 2: MULTIPLE TRANSACTIONS
            if (rule.getTransactionLimit() > 0 &&
                    rule.getTimeWindowMinutes() > 0) {

                LocalDateTime startTime =
                        transaction.getTimestamp()
                                .minusMinutes(rule.getTimeWindowMinutes());

                LocalDateTime endTime =
                        transaction.getTimestamp();

                List<Transaction> recentTransactions =
                        transactionRepository
                                .findBySenderAndTimestampBetween(
                                        transaction.getSender(),
                                        startTime,
                                        endTime
                                );

                if (recentTransactions.size() > rule.getTransactionLimit()) {

                    FlaggedTransaction flaggedTransaction =
                            new FlaggedTransaction();

                    flaggedTransaction.setTransaction(transaction);
                    flaggedTransaction.setRule(rule);
                    flaggedTransaction.setReason(
                            "Multiple transactions detected from the same account within the configured time window"
                    );
                    flaggedTransaction.setStatus("PENDING");

                    flaggedTransactionRepository.save(flaggedTransaction);
                }
            }
        }
    }
}