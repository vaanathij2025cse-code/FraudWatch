package com.example.demo.service;

import com.example.demo.model.FlaggedTransaction;
import com.example.demo.repository.FlaggedTransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FlaggedTransactionService {

    private final FlaggedTransactionRepository flaggedTransactionRepository;

    public FlaggedTransactionService(
            FlaggedTransactionRepository flaggedTransactionRepository) {
        this.flaggedTransactionRepository = flaggedTransactionRepository;
    }

    public FlaggedTransaction saveFlaggedTransaction(
            FlaggedTransaction flaggedTransaction) {

        return flaggedTransactionRepository.save(flaggedTransaction);
    }

    public List<FlaggedTransaction> getAllFlaggedTransactions() {
        return flaggedTransactionRepository.findAll();
    }

    public Optional<FlaggedTransaction> getFlaggedTransactionById(Long id) {
        return flaggedTransactionRepository.findById(id);
    }

    public void deleteFlaggedTransaction(Long id) {
        flaggedTransactionRepository.deleteById(id);
    }
    public FlaggedTransaction updateFlaggedTransaction(
        Long id,
        FlaggedTransaction flaggedTransaction) {

    Optional<FlaggedTransaction> existingFlag =
            flaggedTransactionRepository.findById(id);

    if (existingFlag.isPresent()) {

        FlaggedTransaction existing = existingFlag.get();

        existing.setTransaction(flaggedTransaction.getTransaction());
        existing.setRule(flaggedTransaction.getRule());
        existing.setReason(flaggedTransaction.getReason());
        existing.setStatus(flaggedTransaction.getStatus());

        return flaggedTransactionRepository.save(existing);
    }

    return null;
}
}