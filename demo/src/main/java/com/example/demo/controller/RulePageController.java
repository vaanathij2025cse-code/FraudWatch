package com.example.demo.controller;

import com.example.demo.model.Rule;
import com.example.demo.service.RuleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class RulePageController {

    private final RuleService ruleService;

    public RulePageController(RuleService ruleService) {
        this.ruleService = ruleService;
    }

    @GetMapping("/rules-page")
    public String showRulesPage(Model model) {
        List<Rule> rules = ruleService.getAllRules();
        model.addAttribute("rules", rules);

        if (!model.containsAttribute("rule")) {
            Rule newRule = new Rule();
            newRule.setEnabled(true);
            model.addAttribute("rule", newRule);
        }

        model.addAttribute("activePage", "rules");
        return "rules";
    }

    @PostMapping("/rules-page")
    public String addRule(
            @ModelAttribute("rule") Rule rule,
            RedirectAttributes redirectAttributes) {
        try {
            Rule saved = ruleService.saveRule(rule);
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Rule '" + saved.getRuleName() + "' created successfully!"
            );
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Failed to create rule: " + e.getMessage()
            );
        }

        return "redirect:/rules-page";
    }

    @GetMapping("/rules-page/edit/{id}")
    public String showEditForm(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes) {
        Optional<Rule> ruleOpt = ruleService.getRuleById(id);
        if (ruleOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Rule #" + id + " not found.");
            return "redirect:/rules-page";
        }

        model.addAttribute("rules", ruleService.getAllRules());
        model.addAttribute("rule", new Rule());
        model.addAttribute("editRule", ruleOpt.get());
        model.addAttribute("activePage", "rules");
        return "rules";
    }

    @PostMapping("/rules-page/edit/{id}")
    public String updateRule(
            @PathVariable Long id,
            @ModelAttribute Rule rule,
            RedirectAttributes redirectAttributes) {
        try {
            ruleService.updateRule(id, rule);
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Rule #" + id + " ('" + rule.getRuleName() + "') updated successfully."
            );
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Failed to update rule #" + id + ": " + e.getMessage()
            );
        }

        return "redirect:/rules-page";
    }

    @GetMapping("/rules-page/toggle/{id}")
    public String toggleRuleStatus(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        Optional<Rule> ruleOpt = ruleService.getRuleById(id);
        if (ruleOpt.isPresent()) {
            Rule r = ruleOpt.get();
            r.setEnabled(!r.isEnabled());
            ruleService.updateRule(id, r);
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Rule '" + r.getRuleName() + "' is now " + (r.isEnabled() ? "ENABLED" : "DISABLED") + "."
            );
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Rule #" + id + " not found.");
        }

        return "redirect:/rules-page";
    }

    @GetMapping("/rules-page/delete/{id}")
    public String deleteRule(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        try {
            ruleService.deleteRule(id);
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Rule #" + id + " deleted successfully."
            );
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Cannot delete rule #" + id + ". It may be linked to existing flagged records."
            );
        }

        return "redirect:/rules-page";
    }
}
