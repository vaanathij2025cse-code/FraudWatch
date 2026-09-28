package com.example.demo.controller;

import com.example.demo.model.Transaction;
import com.example.demo.service.TransactionService;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.beans.PropertyEditorSupport;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Controller
public class TransactionPageController {

    private final TransactionService transactionService;

    public TransactionPageController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(LocalDateTime.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                if (text == null || text.trim().isEmpty()) {
                    setValue(LocalDateTime.now());
                } else {
                    try {
                        setValue(LocalDateTime.parse(text));
                    } catch (Exception e) {
                        try {
                            setValue(LocalDateTime.parse(text, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                        } catch (Exception ex) {
                            setValue(LocalDateTime.now());
                        }
                    }
                }
            }
        });
    }

    @GetMapping("/transactions-page")
    public String showTransactionsPage(Model model) {
        List<Transaction> transactions = transactionService.getAllTransactions();
        model.addAttribute("transactions", transactions);

        if (!model.containsAttribute("transaction")) {
            Transaction newTx = new Transaction();
            newTx.setTimestamp(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
            model.addAttribute("transaction", newTx);
        }

        model.addAttribute("activePage", "transactions");
        return "transactions";
    }

    @PostMapping("/transactions-page")
    public String addTransaction(
            @ModelAttribute("transaction") Transaction transaction,
            RedirectAttributes redirectAttributes) {
        try {
            if (transaction.getTimestamp() == null) {
                transaction.setTimestamp(LocalDateTime.now());
            }

            Transaction saved = transactionService.saveTransaction(transaction);
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Transaction #" + saved.getId() + " created successfully. Fraud detection rules evaluated."
            );
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Failed to create transaction: " + e.getMessage()
            );
        }

        return "redirect:/transactions-page";
    }

    @GetMapping("/transactions-page/edit/{id}")
    public String showEditForm(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes) {
        Optional<Transaction> txOpt = transactionService.getTransactionById(id);
        if (txOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Transaction #" + id + " not found.");
            return "redirect:/transactions-page";
        }

        model.addAttribute("transactions", transactionService.getAllTransactions());
        model.addAttribute("transaction", new Transaction());
        model.addAttribute("editTransaction", txOpt.get());
        model.addAttribute("activePage", "transactions");
        return "transactions";
    }

    @PostMapping("/transactions-page/edit/{id}")
    public String updateTransaction(
            @PathVariable Long id,
            @ModelAttribute Transaction transaction,
            RedirectAttributes redirectAttributes) {
        try {
            if (transaction.getTimestamp() == null) {
                transaction.setTimestamp(LocalDateTime.now());
            }
            transactionService.updateTransaction(id, transaction);
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Transaction #" + id + " updated successfully."
            );
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Failed to update transaction #" + id + ": " + e.getMessage()
            );
        }

        return "redirect:/transactions-page";
    }

    @GetMapping("/transactions-page/delete/{id}")
    public String deleteTransaction(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        try {
            transactionService.deleteTransaction(id);
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Transaction #" + id + " deleted successfully."
            );
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Cannot delete transaction #" + id + ". It may be referenced by existing flagged transaction records."
            );
        }

        return "redirect:/transactions-page";
    }
}
