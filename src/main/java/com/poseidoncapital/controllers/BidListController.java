package com.poseidoncapital.controllers;

import com.poseidoncapital.domain.BidList;
import com.poseidoncapital.service.BidListService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
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
    public String home(Model model, @AuthenticationPrincipal OidcUser oidcUser) {
        //  call service find all bids to show to the view
        model.addAttribute("bidLists", bidListService.getAllBidLists());
        model.addAttribute("username", oidcUser.getPreferredUsername());
        return "bidList/list";
    }

    @GetMapping("/bidList/add")
    public String addBidForm(BidList bid) {
        return "bidList/add";
    }

    @PostMapping("/bidList/validate")
    public String validate(@Valid BidList bid, BindingResult result) {
        //  check data valid and save to db, after saving return bid list
        if (!result.hasErrors()){
            bidListService.addBidList(bid.getAccount(), bid.getType(), bid.getBidQuantity());
            return "redirect:/bidList/list";
        }
        return "bidList/add";
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
        if (!result.hasErrors()){
            bidListService.updateBidList(id,bidList.getAccount(),bidList.getType(), bidList.getBidQuantity());
            return "redirect:/bidList/list";
        }
        model.addAttribute("bidList", bidList);
        return "bidList/update";
    }

    @GetMapping("/bidList/delete/{id}")
    public String deleteBid(@PathVariable("id") Integer id) {
        //  Find Bid by Id and delete the bid, return to Bid list
        bidListService.deleteBidList(id);
        return "redirect:/bidList/list";
    }
}