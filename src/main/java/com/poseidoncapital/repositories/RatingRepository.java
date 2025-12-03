package com.poseidoncapital.repositories;

import com.poseidoncapital.domain.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingRepository extends JpaRepository<Rating, Integer> {

}