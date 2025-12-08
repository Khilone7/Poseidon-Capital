package com.poseidoncapital.controllers;

import com.poseidoncapital.domain.CurvePoint;
import com.poseidoncapital.service.CurveService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class CurveController {


    private final CurveService curveService;

    @RequestMapping("/curvePoint/list")
    public String home(Model model, Authentication auth) {
        //  find all Curve Point, add to model
        model.addAttribute("curvePoints", curveService.getAllCurvePoints());
        return "curvePoint/list";
    }

    @GetMapping("/curvePoint/add")
    public String addBidForm(CurvePoint bid) {
        return "curvePoint/add";
    }

    @PostMapping("/curvePoint/validate")
    public String validate(@Valid CurvePoint curvePoint, BindingResult result, Model model) {
        //  check data valid and save to db, after saving return Curve list
        if (result.hasErrors()) {
            return "curvePoint/add";
        }
        try {
            curveService.addCurvePoint(curvePoint.getCurveId(), curvePoint.getTerm(), curvePoint.getValue());
        } catch (Exception e) {
            result.rejectValue("curveId", "error.curvePoint", "Erreur lors de l'ajout du Curve Point");
            return "curvePoint/add";
        }
        return "redirect:/curvePoint/list";
    }

    @GetMapping("/curvePoint/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        //  get CurvePoint by Id and to model then show to the form
        CurvePoint curvePoint = curveService.getCurvePointsById(id);
        model.addAttribute("curvePoint", curvePoint);
        return "curvePoint/update";
    }

    @PostMapping("/curvePoint/update/{id}")
    public String updateBid(@PathVariable("id") Integer id, @Valid CurvePoint curvePoint,
                            BindingResult result, Model model) {
        //  check required fields, if valid call service to update Curve and return Curve list
        if (result.hasErrors()) {
            return "curvePoint/update";
        }
        try {
            curveService.updateCurvePoint(id, curvePoint.getCurveId(), curvePoint.getTerm(), curvePoint.getValue());
        } catch (IllegalArgumentException e) {
            result.rejectValue("curveId", "error.curvePoint", "Curve Point introuvable");
            return "curvePoint/update";
        }
        return "redirect:/curvePoint/list";
    }

    @GetMapping("/curvePoint/delete/{id}")
    public String deleteBid(@PathVariable("id") Integer id, Model model) {
        // Find Curve by Id and delete the Curve, return to Curve list
        curveService.deleteCurvePoint(id);
        return "redirect:/curvePoint/list";
    }
}