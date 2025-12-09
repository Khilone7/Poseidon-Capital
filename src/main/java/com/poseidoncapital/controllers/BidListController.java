package com.poseidoncapital.controllers;

import com.poseidoncapital.domain.BidList;
import com.poseidoncapital.service.BidListService;
import lombok.RequiredArgsConstructor;
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
public class BidListController {

    private final BidListService bidListService;

    @RequestMapping("/bidList/list")
    public String home(Model model) {
        //  call service find all bids to show to the view
        model.addAttribute("bidLists", bidListService.getAllBidLists());
        return "bidList/list";
    }

    @GetMapping("/bidList/add")
    public String addBidForm(BidList bid) {
        return "bidList/add";
    }

    @PostMapping("/bidList/validate")
    public String validate(@Valid BidList bid, BindingResult result, Model model) {
        //  check data valid and save to db, after saving return bid list
        if (result.hasErrors()) {
            return "bidList/add";
        }
        try {
            bidListService.addBidList(bid.getAccount(), bid.getType(), bid.getBidQuantity());
        } catch (Exception e) {
            result.rejectValue("account", "error.bidList", "Erreur lors de l'ajout du Bid");
            return "bidList/add";
        }
        return "bidList/list";
    }

    @GetMapping("/bidList/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        //  get Bid by Id and to model then show to the form
        BidList bidList = bidListService.getBidListById(id);
        model.addAttribute("bidList", bidList);
        return "bidList/update";
    }

    @PostMapping("/bidList/update/{id}")
    public String updateBid(@PathVariable("id") Integer id, @Valid BidList bidList,
                            BindingResult result, Model model) {
        //  check required fields, if valid call service to update Bid and return list Bid
        if (result.hasErrors()){
            return "bidList/update";
        }
        try {
            bidListService.updateBidList(id,bidList.getAccount(),bidList.getType(), bidList.getBidQuantity());
        }catch (IllegalArgumentException e) {
            result.rejectValue("account", "error.bidList", "Erreur lors de la mise à jour du Bid");
            return "bidList/update";
        }
        return "redirect:/bidList/list";
    }

    @GetMapping("/bidList/delete/{id}")
    public String deleteBid(@PathVariable("id") Integer id, Model model) {
        //  Find Bid by Id and delete the bid, return to Bid list
        bidListService.deleteBidList(id);
        return "redirect:/bidList/list";
    }
}