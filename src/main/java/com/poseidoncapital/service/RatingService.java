package com.poseidoncapital.service;

import com.poseidoncapital.domain.Rating;
import com.poseidoncapital.repositories.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;

    public void addRating(String moodysRating, String sandPRating, String fitchRating, Integer orderNumber) {
        Rating rating = new Rating();
        rating.setMoodysRating(moodysRating);
        rating.setSandPRating(sandPRating);
        rating.setFitchRating(fitchRating);
        rating.setOrderNumber(orderNumber);
        ratingRepository.save(rating);
    }

    public List<Rating> getAllRatings() {
        return ratingRepository.findAll();
    }

    public Rating getRatingById(Integer id) {
        return ratingRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid rating Id:" + id));
    }

    public void updateRating(Integer id, String moodysRating, String sandPRating, String fitchRating, Integer orderNumber) {
        Rating rating = ratingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid rating Id:" + id));

        rating.setMoodysRating(moodysRating);
        rating.setSandPRating(sandPRating);
        rating.setFitchRating(fitchRating);
        rating.setOrderNumber(orderNumber);
        ratingRepository.save(rating);
    }

    public void deleteRating(Integer id) {
        Rating rating = ratingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid rating Id:" + id));
        ratingRepository.delete(rating);
    }
}
