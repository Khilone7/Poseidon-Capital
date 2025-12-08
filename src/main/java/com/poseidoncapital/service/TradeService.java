package com.poseidoncapital.service;

import com.poseidoncapital.repositories.TradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TradeService {

    @Autowired
    private TradeRepository tradeRepository;


}