package com.poseidoncapital;

import com.poseidoncapital.domain.BidList;
import com.poseidoncapital.repositories.BidListRepository;
import com.poseidoncapital.service.BidListService;
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

@ExtendWith(MockitoExtension.class)
class BidListServiceTest {

    @Mock
    private BidListRepository bidListRepository;

    @InjectMocks
    private BidListService bidListService;

    @Test
    void getAllBidLists_shouldReturnEmptyList_whenNoBidsExist() {
        when(bidListRepository.findAll()).thenReturn(emptyList());

        List<BidList> result = bidListService.getAllBidLists();

        assertTrue(result.isEmpty());
    }

    @Test
    void getAllBidLists_shouldReturnAllBids() {
        BidList bid1 = new BidList();
        BidList bid2 = new BidList();
        when(bidListRepository.findAll()).thenReturn(asList(bid1, bid2));

        List<BidList> result = bidListService.getAllBidLists();

        assertEquals(2, result.size());
    }

    @Test
    void addBidList_shouldSetCorrectValues() {
        bidListService.addBidList("TestAccount", "TestType", 150.0);

        verify(bidListRepository).save(argThat(bid -> "TestAccount".equals(bid.getAccount()) &&
                        "TestType".equals(bid.getType()) && bid.getBidQuantity() == 150.0));
    }

    @Test
    void getBidListById_shouldReturnBid_whenExists() {
        BidList expectedBid = new BidList();
        expectedBid.setBidListId(1);
        expectedBid.setAccount("Account");
        when(bidListRepository.findById(1)).thenReturn(Optional.of(expectedBid));

        BidList result = bidListService.getBidListById(1);

        assertEquals("Account", result.getAccount());
    }

    @Test
    void getBidListById_shouldThrowException_whenNotExists() {
        when(bidListRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> bidListService.getBidListById(999));
    }

    @Test
    void updateBidList_shouldUpdateValues_whenExists() {
        BidList existingBid = new BidList();
        existingBid.setBidListId(1);
        existingBid.setAccount("OldAccount");
        existingBid.setType("OldType");
        existingBid.setBidQuantity(100.0);
        when(bidListRepository.findById(1)).thenReturn(Optional.of(existingBid));

        bidListService.updateBidList(1, "NewAccount", "NewType", 200.0);

        verify(bidListRepository).save(argThat(bid -> "NewAccount".equals(bid.getAccount()) &&
                        "NewType".equals(bid.getType()) && bid.getBidQuantity() == 200.0));
    }

    @Test
    void updateBidList_shouldThrowException_whenNotExists() {
        when(bidListRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, ()
                -> bidListService.updateBidList(999, "Account", "Type", 50.0));

        verify(bidListRepository, never()).save(any());
    }

    @Test
    void deleteBidList_shouldDelete_whenExists() {
        BidList existingBid = new BidList();
        existingBid.setBidListId(1);
        when(bidListRepository.findById(1)).thenReturn(Optional.of(existingBid));

        bidListService.deleteBidList(1);

        verify(bidListRepository).delete(existingBid);
    }

    @Test
    void deleteBidList_shouldThrowException_whenNotExists() {
        when(bidListRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> bidListService.deleteBidList(999));

        verify(bidListRepository, never()).delete(any());
    }
}