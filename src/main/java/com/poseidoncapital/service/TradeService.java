package com.poseidoncapital.service;

import com.poseidoncapital.domain.Trade;
import com.poseidoncapital.repositories.TradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TradeService {

    private final TradeRepository tradeRepository;

    public void addTrade(String account, String type, Double buyQuantity) {
        Trade trade = new Trade();
        trade.setAccount(account);
        trade.setType(type);
        trade.setBuyQuantity(buyQuantity);
        tradeRepository.save(trade);
    }

    public Trade getTradeById(Integer id) {
        return tradeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid trade Id:" + id));
    }

    public void updateTrade(Integer id, String account, String type, Double buyQuantity) {
        Trade trade = tradeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid trade Id:" + id));
        trade.setAccount(account);
        trade.setType(type);
        trade.setBuyQuantity(buyQuantity);
        tradeRepository.save(trade);
    }

    public void deleteTrade(Integer id) {
        Trade trade = tradeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid trade Id:" + id));
        tradeRepository.delete(trade);
    }

    public List<Trade> getAllTrades() {
        return tradeRepository.findAll();
    }
}