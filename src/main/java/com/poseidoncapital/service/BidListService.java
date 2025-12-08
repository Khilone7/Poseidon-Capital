package com.poseidoncapital.service;

import com.poseidoncapital.domain.BidList;
import com.poseidoncapital.repositories.BidListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BidListService {

    private final BidListRepository bidListRepository;

    public List<BidList> getAllBidLists() {
        return bidListRepository.findAll();
    }

    public void addBidList(String account, String type, double bidQuantity){
        BidList bidList = new BidList();
        bidList.setAccount(account);
        bidList.setType(type);
        bidList.setBidQuantity(bidQuantity);
        bidListRepository.save(bidList);
    }

    public BidList getBidListById(Integer id) {
        return bidListRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid bid list Id:" + id));
    }

    public void updateBidList(Integer id, String account, String type, double bidQuantity) {
        BidList bidList = bidListRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid bid list Id:" + id));
        bidList.setAccount(account);
        bidList.setType(type);
        bidList.setBidQuantity(bidQuantity);
        bidListRepository.save(bidList);
    }

    public void deleteBidList(Integer id) {
        BidList bidList = bidListRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid bid list Id:" + id));
        bidListRepository.delete(bidList);
    }
}
