package com.example.demo.service;

import com.example.demo.model.Transaction;
import com.example.demo.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction saveTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }

    public void deleteTransaction(Long id) {
        transactionRepository.deleteById(id);
    }
    public Transaction updateTransaction(Long id, Transaction transaction) {

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

}
