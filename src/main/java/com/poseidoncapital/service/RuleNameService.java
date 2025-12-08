package com.poseidoncapital.service;

import com.poseidoncapital.domain.RuleName;
import com.poseidoncapital.repositories.RuleNameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RuleNameService {

    private final RuleNameRepository ruleNameRepository;

    public void addRuleName(String name, String description, String json, String template, String sqlStr, String sqlPart) {
        RuleName ruleName = new RuleName();
        ruleName.setName(name);
        ruleName.setDescription(description);
        ruleName.setJson(json);
        ruleName.setTemplate(template);
        ruleName.setSqlStr(sqlStr);
        ruleName.setSqlPart(sqlPart);
        ruleNameRepository.save(ruleName);
    }

    public RuleName getRuleNameById(Integer id) {
        return ruleNameRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid rule name Id:" + id));
    }

    public void updateRuleName(Integer id, String name, String description, String json, String template, String sqlStr, String sqlPart) {
        RuleName ruleName = ruleNameRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid rule name Id:" + id));

        ruleName.setName(name);
        ruleName.setDescription(description);
        ruleName.setJson(json);
        ruleName.setTemplate(template);
        ruleName.setSqlStr(sqlStr);
        ruleName.setSqlPart(sqlPart);
        ruleNameRepository.save(ruleName);
    }

    public void deleteRuleName(Integer id) {
        RuleName ruleName = ruleNameRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid rule name Id:" + id));
        ruleNameRepository.delete(ruleName);
    }

    public List<RuleName> getAllRuleNames() {
        return ruleNameRepository.findAll();
    }
}
