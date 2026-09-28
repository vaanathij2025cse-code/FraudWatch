package com.example.demo.service;

import com.example.demo.model.Rule;
import com.example.demo.repository.RuleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RuleService {

    private final RuleRepository ruleRepository;

    public RuleService(RuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }

    public Rule saveRule(Rule rule) {
        return ruleRepository.save(rule);
    }

    public List<Rule> getAllRules() {
        return ruleRepository.findAll();
    }

    public Optional<Rule> getRuleById(Long id) {
        return ruleRepository.findById(id);
    }

    public void deleteRule(Long id) {
        ruleRepository.deleteById(id);
    }
    public Rule updateRule(Long id, Rule rule) {

    Optional<Rule> existingRule =
            ruleRepository.findById(id);

    if (existingRule.isPresent()) {

        Rule existing = existingRule.get();

        existing.setRuleName(rule.getRuleName());
        existing.setAmountThreshold(rule.getAmountThreshold());
        existing.setTransactionLimit(rule.getTransactionLimit());
        existing.setTimeWindowMinutes(rule.getTimeWindowMinutes());
        existing.setEnabled(rule.isEnabled());

        return ruleRepository.save(existing);
    }

    return null;
}
}