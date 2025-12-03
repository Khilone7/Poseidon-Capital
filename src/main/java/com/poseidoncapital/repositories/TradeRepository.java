package com.poseidoncapital.repositories;

import com.poseidoncapital.domain.Trade;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TradeRepository extends JpaRepository<Trade, Integer> {
}
