package com.poseidoncapital.controllers;

import com.poseidoncapital.domain.Rating;
import com.poseidoncapital.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;

@Controller
public class RatingController {

    @Autowired
    private RatingService ratingService;

    @RequestMapping("/rating/list")
    public String home(Model model) {
        //  find all Rating, add to model
        model.addAttribute("ratings", ratingService.getAllRatings());
        return "rating/list";
    }

    @GetMapping("/rating/add")
    public String addRatingForm(Rating rating) {
        return "rating/add";
    }

    @PostMapping("/rating/validate")
    public String validate(@Valid Rating rating, BindingResult result, Model model) {
        //  check data valid and save to db, after saving return Rating list
        if (result.hasErrors()) {
            return "rating/add";
        }
        try {
            ratingService.addRating(rating.getMoodysRating(), rating.getSandPRating(), rating.getFitchRating(), rating.getOrderNumber());
        } catch (Exception e) {
            result.rejectValue("moodysRating", "error.rating", "Erreur lors de l'ajout de la notation");
            return "rating/add";
        }
        return "redirect:/rating/list";
    }

    @GetMapping("/rating/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        //  get Rating by Id and to model then show to the form
        Rating rating = ratingService.getRatingById(id);
        model.addAttribute("rating", rating);
        return "rating/update";
    }

    @PostMapping("/rating/update/{id}")
    public String updateRating(@PathVariable("id") Integer id, @Valid Rating rating,
                               BindingResult result, Model model) {
        //  check required fields, if valid call service to update Rating and return Rating list
        if (result.hasErrors()) {
            return "rating/update";
        }
        try {
            ratingService.updateRating(id, rating.getMoodysRating(), rating.getSandPRating(), rating.getFitchRating(), rating.getOrderNumber());
        } catch (IllegalArgumentException e) {
            result.rejectValue("moodysRating", "error.rating", "Erreur lors de la mise à jour de la notation");
            return "rating/update";
        }
        return "redirect:/rating/list";
    }

    @GetMapping("/rating/delete/{id}")
    public String deleteRating(@PathVariable("id") Integer id, Model model) {
        //  Find Rating by Id and delete the Rating, return to Rating list
        ratingService.deleteRating(id);
        return "redirect:/rating/list";
    }
}