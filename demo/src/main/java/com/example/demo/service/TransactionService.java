package com.example.demo.service;

import com.example.demo.model.Transaction;
import com.example.demo.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final FraudDetectionService fraudDetectionService;

    public TransactionService(
            TransactionRepository transactionRepository,
            FraudDetectionService fraudDetectionService) {

        this.transactionRepository = transactionRepository;
        this.fraudDetectionService = fraudDetectionService;
    }

    public Transaction saveTransaction(Transaction transaction) {

        Transaction savedTransaction =
                transactionRepository.save(transaction);

        // Check the transaction for fraud
        fraudDetectionService.checkTransaction(savedTransaction);

        return savedTransaction;
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }

    public Transaction updateTransaction(
            Long id,
            Transaction transaction) {

        Optional<Transaction> existingTransaction =
                transactionRepository.findById(id);

        if (existingTransaction.isPresent()) {

            Transaction existing = existingTransaction.get();

            existing.setSender(transaction.getSender());
            existing.setReceiver(transaction.getReceiver());
            existing.setAmount(transaction.getAmount());
            existing.setTimestamp(transaction.getTimestamp());

            return transactionRepository.save(existing);
        }

        return null;
    }

    public void deleteTransaction(Long id) {
        transactionRepository.deleteById(id);
    }
}