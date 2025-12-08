package com.poseidoncapital.controllers;

import com.poseidoncapital.domain.RuleName;
import com.poseidoncapital.service.BidListService;
import com.poseidoncapital.service.RuleNameService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;

@RequiredArgsConstructor
@Controller
public class RuleNameController {

    private final RuleNameService ruleNameService;

    @RequestMapping("/ruleName/list")
    public String home(Model model) {
        //  find all RuleName, add to model
        model.addAttribute("ruleNames", ruleNameService.getAllRuleNames());
        return "ruleName/list";
    }

    @GetMapping("/ruleName/add")
    public String addRuleForm(RuleName bid) {
        return "ruleName/add";
    }

    @PostMapping("/ruleName/validate")
    public String validate(@Valid RuleName ruleName, BindingResult result, Model model) {
        //  check data valid and save to db, after saving return RuleName list
        if (result.hasErrors()) {
            return "ruleName/add";
        }
        try {
            ruleNameService.addRuleName(ruleName.getName(), ruleName.getDescription(), ruleName.getJson(),
                    ruleName.getTemplate(), ruleName.getSqlStr(), ruleName.getSqlPart());
        } catch (Exception e) {
            result.rejectValue("name", "error.ruleName", "Erreur lors de l'ajout du Rule Name");
            return "ruleName/add";
        }
        return "redirect:/ruleName/list";
    }

    @GetMapping("/ruleName/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        //  get RuleName by Id and to model then show to the form
        RuleName ruleName = ruleNameService.getRuleNameById(id);
        model.addAttribute("ruleName", ruleName);
        return "ruleName/update";
    }

    @PostMapping("/ruleName/update/{id}")
    public String updateRuleName(@PathVariable("id") Integer id, @Valid RuleName ruleName,
                                 BindingResult result, Model model) {
        //  check required fields, if valid call service to update RuleName and return RuleName list
        if (result.hasErrors()) {
            return "ruleName/update";
        }
        try {
            ruleNameService.updateRuleName(id, ruleName.getName(), ruleName.getDescription(), ruleName.getJson(),
                    ruleName.getTemplate(), ruleName.getSqlStr(), ruleName.getSqlPart());
        } catch (Exception e) {
            result.rejectValue("name", "error.ruleName", "Erreur lors de la mise à jour du Rule Name");
            return "ruleName/update";
        }
        return "redirect:/ruleName/list";
    }

    @GetMapping("/ruleName/delete/{id}")
    public String deleteRuleName(@PathVariable("id") Integer id, Model model) {
        //  Find RuleName by Id and delete the RuleName, return to Rule list
        ruleNameService.deleteRuleName(id);
        return "redirect:/ruleName/list";
    }
}