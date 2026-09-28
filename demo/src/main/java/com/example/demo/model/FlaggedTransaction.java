package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "flagged_transactions")
public class FlaggedTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transaction transaction;

    @ManyToOne
    @JoinColumn(name = "rule_id", nullable = false)
    private Rule rule;

    private String reason;

    private String status;

    // Default constructor
    public FlaggedTransaction() {
    }

    // Parameterized constructor
    public FlaggedTransaction(Transaction transaction,
                              Rule rule,
                              String reason,
                              String status) {

        this.transaction = transaction;
        this.rule = rule;
        this.reason = reason;
        this.status = status;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public Rule getRule() {
        return rule;
    }

    public void setRule(Rule rule) {
        this.rule = rule;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}