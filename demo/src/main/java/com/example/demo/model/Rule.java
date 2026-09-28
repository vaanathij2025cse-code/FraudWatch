
package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "rules")
public class Rule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ruleName;

    private double amountThreshold;

    private int transactionLimit;

    private int timeWindowMinutes;

    private boolean enabled;

    // Default constructor
    public Rule() {
    }

    // Parameterized constructor
    public Rule(String ruleName, double amountThreshold,
                int transactionLimit, int timeWindowMinutes,
                boolean enabled) {
        this.ruleName = ruleName;
        this.amountThreshold = amountThreshold;
        this.transactionLimit = transactionLimit;
        this.timeWindowMinutes = timeWindowMinutes;
        this.enabled = enabled;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public double getAmountThreshold() {
        return amountThreshold;
    }

    public void setAmountThreshold(double amountThreshold) {
        this.amountThreshold = amountThreshold;
    }

    public int getTransactionLimit() {
        return transactionLimit;
    }

    public void setTransactionLimit(int transactionLimit) {
        this.transactionLimit = transactionLimit;
    }

    public int getTimeWindowMinutes() {
        return timeWindowMinutes;
    }

    public void setTimeWindowMinutes(int timeWindowMinutes) {
        this.timeWindowMinutes = timeWindowMinutes;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}

