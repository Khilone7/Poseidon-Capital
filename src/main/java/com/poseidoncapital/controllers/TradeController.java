package com.poseidoncapital.controllers;

import com.poseidoncapital.domain.Trade;
import com.poseidoncapital.service.BidListService;
import com.poseidoncapital.service.TradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
public class TradeController {

    private final TradeService tradeService;

    @RequestMapping("/trade/list")
    public String home(Model model) {
        //  find all Trade, add to model
        model.addAttribute("trades", tradeService.getAllTrades());
        return "trade/list";
    }

    @GetMapping("/trade/add")
    public String addUser(Trade bid) {
        return "trade/add";
    }

    @PostMapping("/trade/validate")
    public String validate(@Valid Trade trade, BindingResult result, Model model) {
        //  check data valid and save to db, after saving return Trade list
        if (result.hasErrors()) {
            return "trade/add";
        }
        try {
            tradeService.addTrade(trade.getAccount(), trade.getType(), trade.getBuyQuantity());
        } catch (Exception e) {
            result.rejectValue("account", "error.trade", "Erreur lors de l'ajout du Trade");
            return "trade/add";
        }
        return "redirect:/trade/list";
    }

    @GetMapping("/trade/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        //  get Trade by Id and to model then show to the form
        Trade trade = tradeService.getTradeById(id);
        model.addAttribute("trade", trade);
        return "trade/update";
    }

    @PostMapping("/trade/update/{id}")
    public String updateTrade(@PathVariable("id") Integer id, @Valid Trade trade,
                              BindingResult result, Model model) {
        //  check required fields, if valid call service to update Trade and return Trade list
        if (result.hasErrors()) {
            return "trade/update";
        }
        try {
            tradeService.updateTrade(id, trade.getAccount(), trade.getType(), trade.getBuyQuantity());
        } catch (Exception e) {
            result.rejectValue("account", "error.trade", "Erreur lors de la mise à jour du Trade");
            return "trade/update";
        }
        return "redirect:/trade/list";
    }

    @GetMapping("/trade/delete/{id}")
    public String deleteTrade(@PathVariable("id") Integer id, Model model) {
        //  Find Trade by Id and delete the Trade, return to Trade list
        tradeService.deleteTrade(id);
        return "redirect:/trade/list";
    }
}