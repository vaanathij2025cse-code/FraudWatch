package com.example.demo.controller;

import com.example.demo.model.FlaggedTransaction;
import com.example.demo.service.FlaggedTransactionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/flagged-transactions")
public class FlaggedTransactionController {

    private final FlaggedTransactionService flaggedTransactionService;

    public FlaggedTransactionController(
            FlaggedTransactionService flaggedTransactionService) {

        this.flaggedTransactionService = flaggedTransactionService;
    }

    // CREATE
    @PostMapping
    public FlaggedTransaction createFlaggedTransaction(
            @RequestBody FlaggedTransaction flaggedTransaction) {

        return flaggedTransactionService
                .saveFlaggedTransaction(flaggedTransaction);
    }

    // READ ALL
    @GetMapping
    public List<FlaggedTransaction> getAllFlaggedTransactions() {

        return flaggedTransactionService
                .getAllFlaggedTransactions();
    }

    // READ ONE
    @GetMapping("/{id}")
    public FlaggedTransaction getFlaggedTransactionById(
            @PathVariable Long id) {

        return flaggedTransactionService
                .getFlaggedTransactionById(id)
                .orElse(null);
    }

    // UPDATE
    @PutMapping("/{id}")
    public FlaggedTransaction updateFlaggedTransaction(
            @PathVariable Long id,
            @RequestBody FlaggedTransaction flaggedTransaction) {

        return flaggedTransactionService
                .updateFlaggedTransaction(id, flaggedTransaction);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public String deleteFlaggedTransaction(
            @PathVariable Long id) {

        flaggedTransactionService
                .deleteFlaggedTransaction(id);

        return "Flagged transaction deleted successfully";
    }
}