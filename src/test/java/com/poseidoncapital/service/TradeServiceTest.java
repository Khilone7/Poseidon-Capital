package com.poseidoncapital.service;

import com.poseidoncapital.domain.Trade;
import com.poseidoncapital.repositories.TradeRepository;
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
class TradeServiceTest {

    @Mock
    private TradeRepository tradeRepository;

    @InjectMocks
    private TradeService tradeService;

    @Test
    void getAllTrades_shouldReturnEmptyList_whenNoTradesExist() {
        when(tradeRepository.findAll()).thenReturn(emptyList());

        List<Trade> result = tradeService.getAllTrades();

        assertTrue(result.isEmpty());
    }

    @Test
    void getAllTrades_shouldReturnAllTrades() {
        Trade t1 = new Trade();
        Trade t2 = new Trade();
        when(tradeRepository.findAll()).thenReturn(asList(t1, t2));

        List<Trade> result = tradeService.getAllTrades();

        assertEquals(2, result.size());
    }

    @Test
    void addTrade_shouldSetCorrectValues() {
        tradeService.addTrade("TestAccount", "TestType", 150.0);

        verify(tradeRepository).save(argThat(trade ->
                "TestAccount".equals(trade.getAccount()) &&
                        "TestType".equals(trade.getType()) &&
                        Double.valueOf(150.0).equals(trade.getBuyQuantity())
        ));
    }

    @Test
    void getTradeById_shouldReturnTrade_whenExists() {
        Trade expected = new Trade();
        expected.setTradeId(1);
        expected.setAccount("Account");
        when(tradeRepository.findById(1)).thenReturn(Optional.of(expected));

        Trade result = tradeService.getTradeById(1);

        assertEquals("Account", result.getAccount());
    }

    @Test
    void getTradeById_shouldThrowException_whenNotExists() {
        when(tradeRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> tradeService.getTradeById(999));
    }

    @Test
    void updateTrade_shouldUpdateValues_whenExists() {
        Trade existing = new Trade();
        existing.setTradeId(1);
        existing.setAccount("OldAccount");
        existing.setType("OldType");
        existing.setBuyQuantity(100.0);
        when(tradeRepository.findById(1)).thenReturn(Optional.of(existing));

        tradeService.updateTrade(1, "NewAccount", "NewType", 200.0);

        verify(tradeRepository).save(argThat(trade ->
                "NewAccount".equals(trade.getAccount()) &&
                        "NewType".equals(trade.getType()) &&
                        Double.valueOf(200.0).equals(trade.getBuyQuantity())
        ));
    }

    @Test
    void updateTrade_shouldThrowException_whenNotExists() {
        when(tradeRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                tradeService.updateTrade(999, "Account", "Type", 50.0));

        verify(tradeRepository, never()).save(any());
    }

    @Test
    void deleteTrade_shouldDelete_whenExists() {
        Trade existing = new Trade();
        existing.setTradeId(1);
        when(tradeRepository.findById(1)).thenReturn(Optional.of(existing));

        tradeService.deleteTrade(1);

        verify(tradeRepository).delete(existing);
    }

    @Test
    void deleteTrade_shouldThrowException_whenNotExists() {
        when(tradeRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> tradeService.deleteTrade(999));

        verify(tradeRepository, never()).delete(any());
    }
}
