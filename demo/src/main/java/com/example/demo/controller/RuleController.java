package com.example.demo.controller;

import com.example.demo.model.Rule;
import com.example.demo.service.RuleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rules")
public class RuleController {

    private final RuleService ruleService;

    public RuleController(RuleService ruleService) {
        this.ruleService = ruleService;
    }

    // CREATE
    @PostMapping
    public Rule createRule(@RequestBody Rule rule) {
        return ruleService.saveRule(rule);
    }

    // READ ALL
    @GetMapping
    public List<Rule> getAllRules() {
        return ruleService.getAllRules();
    }

    // READ ONE
    @GetMapping("/{id}")
    public Rule getRuleById(@PathVariable Long id) {
        return ruleService.getRuleById(id).orElse(null);
    }

    // UPDATE
    @PutMapping("/{id}")
    public Rule updateRule(
            @PathVariable Long id,
            @RequestBody Rule rule) {

        return ruleService.updateRule(id, rule);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public String deleteRule(@PathVariable Long id) {

        ruleService.deleteRule(id);

        return "Rule deleted successfully";
    }
}
