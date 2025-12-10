package com.poseidoncapital.service;

import com.poseidoncapital.domain.RuleName;
import com.poseidoncapital.repositories.RuleNameRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class RuleNameServiceTest {

    @Mock
    private RuleNameRepository ruleNameRepository;

    @InjectMocks
    private RuleNameService ruleNameService;

    @Test
    void getAllRuleNames_shouldReturnEmptyList_whenNoRuleNamesExist() {
        when(ruleNameRepository.findAll()).thenReturn(emptyList());

        List<RuleName> result = ruleNameService.getAllRuleNames();

        assertTrue(result.isEmpty());
    }

    @Test
    void getAllRuleNames_shouldReturnAllRuleNames() {
        RuleName r1 = new RuleName();
        RuleName r2 = new RuleName();
        when(ruleNameRepository.findAll()).thenReturn(asList(r1, r2));

        List<RuleName> result = ruleNameService.getAllRuleNames();

        assertEquals(2, result.size());
    }

    @Test
    void addRuleName_shouldSetCorrectValues() {
        ruleNameService.addRuleName("NameA", "DescA", "{json}", "tpl", "sqlStr", "sqlPart");

        verify(ruleNameRepository).save(argThat(r ->
                "NameA".equals(r.getName()) &&
                        "DescA".equals(r.getDescription()) &&
                        "{json}".equals(r.getJson()) &&
                        "tpl".equals(r.getTemplate()) &&
                        "sqlStr".equals(r.getSqlStr()) &&
                        "sqlPart".equals(r.getSqlPart())
        ));
    }

    @Test
    void getRuleNameById_shouldReturnRule_whenExists() {
        RuleName expected = new RuleName();
        expected.setId(1);
        expected.setName("MyRule");
        when(ruleNameRepository.findById(1)).thenReturn(Optional.of(expected));

        RuleName result = ruleNameService.getRuleNameById(1);

        assertEquals("MyRule", result.getName());
    }

    @Test
    void getRuleNameById_shouldThrowException_whenNotExists() {
        when(ruleNameRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> ruleNameService.getRuleNameById(999));
    }

    @Test
    void updateRuleName_shouldUpdateValues_whenExists() {
        RuleName existing = new RuleName();
        existing.setId(1);
        existing.setName("OldName");
        existing.setDescription("OldDesc");
        existing.setJson("oldJson");
        existing.setTemplate("oldTpl");
        existing.setSqlStr("oldSql");
        existing.setSqlPart("oldPart");
        when(ruleNameRepository.findById(1)).thenReturn(Optional.of(existing));

        ruleNameService.updateRuleName(1, "NewName", "NewDesc", "newJson", "newTpl", "newSql", "newPart");

        verify(ruleNameRepository).save(argThat(r ->
                "NewName".equals(r.getName()) &&
                        "NewDesc".equals(r.getDescription()) &&
                        "newJson".equals(r.getJson()) &&
                        "newTpl".equals(r.getTemplate()) &&
                        "newSql".equals(r.getSqlStr()) &&
                        "newPart".equals(r.getSqlPart())
        ));
    }

    @Test
    void updateRuleName_shouldThrowException_whenNotExists() {
        when(ruleNameRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                ruleNameService.updateRuleName(999, "N", "D", "j", "t", "s", "p"));

        verify(ruleNameRepository, never()).save(any());
    }

    @Test
    void deleteRuleName_shouldDelete_whenExists() {
        RuleName existing = new RuleName();
        existing.setId(1);
        when(ruleNameRepository.findById(1)).thenReturn(Optional.of(existing));

        ruleNameService.deleteRuleName(1);

        verify(ruleNameRepository).delete(existing);
    }

    @Test
    void deleteRuleName_shouldThrowException_whenNotExists() {
        when(ruleNameRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> ruleNameService.deleteRuleName(999));

        verify(ruleNameRepository, never()).delete(any());
    }
}