package com.poseidoncapital.service;

import com.poseidoncapital.domain.Rating;
import com.poseidoncapital.repositories.RatingRepository;
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
class RatingServiceTest {

    @Mock
    private RatingRepository ratingRepository;

    @InjectMocks
    private RatingService ratingService;

    @Test
    void getAllRatings_shouldReturnEmptyList_whenNoRatingsExist() {
        when(ratingRepository.findAll()).thenReturn(emptyList());

        List<Rating> result = ratingService.getAllRatings();

        assertTrue(result.isEmpty());
    }

    @Test
    void getAllRatings_shouldReturnAllRatings() {
        Rating r1 = new Rating();
        Rating r2 = new Rating();
        when(ratingRepository.findAll()).thenReturn(asList(r1, r2));

        List<Rating> result = ratingService.getAllRatings();

        assertEquals(2, result.size());
    }

    @Test
    void addRating_shouldSetCorrectValues() {
        ratingService.addRating("Moodys", "SandP", "Fitch", 7);

        verify(ratingRepository).save(argThat(r ->
                "Moodys".equals(r.getMoodysRating()) &&
                        "SandP".equals(r.getSandPRating()) &&
                        "Fitch".equals(r.getFitchRating()) &&
                        Integer.valueOf(7).equals(r.getOrderNumber())
        ));
    }

    @Test
    void getRatingById_shouldReturnRating_whenExists() {
        Rating expected = new Rating();
        expected.setId(1);
        expected.setMoodysRating("Some");
        when(ratingRepository.findById(1)).thenReturn(Optional.of(expected));

        Rating result = ratingService.getRatingById(1);

        assertEquals("Some", result.getMoodysRating());
    }

    @Test
    void getRatingById_shouldThrowException_whenNotExists() {
        when(ratingRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> ratingService.getRatingById(999));
    }

    @Test
    void updateRating_shouldUpdateValues_whenExists() {
        Rating existing = new Rating();
        existing.setId(1);
        existing.setMoodysRating("Old");
        existing.setSandPRating("OldS");
        existing.setFitchRating("OldF");
        existing.setOrderNumber(10);
        when(ratingRepository.findById(1)).thenReturn(Optional.of(existing));

        ratingService.updateRating(1, "NewM", "NewS", "NewF", 20);

        verify(ratingRepository).save(argThat(r ->
                "NewM".equals(r.getMoodysRating()) &&
                "NewS".equals(r.getSandPRating()) &&
                "NewF".equals(r.getFitchRating()) &&
                Integer.valueOf(20).equals(r.getOrderNumber())
        ));
    }

    @Test
    void updateRating_shouldThrowException_whenNotExists() {
        when(ratingRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                ratingService.updateRating(999, "M", "S", "F", 1));

        verify(ratingRepository, never()).save(any());
    }

    @Test
    void deleteRating_shouldDelete_whenExists() {
        Rating existing = new Rating();
        existing.setId(1);
        when(ratingRepository.findById(1)).thenReturn(Optional.of(existing));

        ratingService.deleteRating(1);

        verify(ratingRepository).delete(existing);
    }

    @Test
    void deleteRating_shouldThrowException_whenNotExists() {
        when(ratingRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> ratingService.deleteRating(999));

        verify(ratingRepository, never()).delete(any());
    }
}