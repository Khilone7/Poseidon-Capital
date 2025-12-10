package com.poseidoncapital.service;

import com.poseidoncapital.domain.CurvePoint;
import com.poseidoncapital.repositories.CurvePointRepository;
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
class CurveServiceTest {

    @Mock
    private CurvePointRepository curvePointRepository;

    @InjectMocks
    private CurveService curveService;

    @Test
    void getAllCurvePoints_shouldReturnEmptyList_whenNoCurvesExist() {
        when(curvePointRepository.findAll()).thenReturn(emptyList());

        List<CurvePoint> result = curveService.getAllCurvePoints();

        assertTrue(result.isEmpty());
    }

    @Test
    void getAllCurvePoints_shouldReturnAllCurves() {
        CurvePoint c1 = new CurvePoint();
        CurvePoint c2 = new CurvePoint();
        when(curvePointRepository.findAll()).thenReturn(asList(c1, c2));

        List<CurvePoint> result = curveService.getAllCurvePoints();

        assertEquals(2, result.size());
    }

    @Test
    void addCurvePoint_shouldSetCorrectValues() {
        curveService.addCurvePoint(5, 10.5, 99.9);

        verify(curvePointRepository).save(argThat(cp ->
                Integer.valueOf(5).equals(cp.getCurveId()) &&
                Double.valueOf(10.5).equals(cp.getTerm()) &&
                Double.valueOf(99.9).equals(cp.getValue())
        ));
    }

    @Test
    void getCurvePointsById_shouldReturnCurve_whenExists() {
        CurvePoint expected = new CurvePoint();
        expected.setId(1);
        expected.setCurveId(2);
        when(curvePointRepository.findById(1)).thenReturn(Optional.of(expected));

        CurvePoint result = curveService.getCurvePointsById(1);

        assertEquals(2, result.getCurveId());
    }

    @Test
    void getCurvePointsById_shouldThrowException_whenNotExists() {
        when(curvePointRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> curveService.getCurvePointsById(999));
    }

    @Test
    void updateCurvePoint_shouldUpdateValues_whenExists() {
        CurvePoint existing = new CurvePoint();
        existing.setId(1);
        existing.setCurveId(1);
        existing.setTerm(1.0);
        existing.setValue(1.0);
        when(curvePointRepository.findById(1)).thenReturn(Optional.of(existing));

        curveService.updateCurvePoint(1, 42, 7.7, 88.8);

        verify(curvePointRepository).save(argThat(cp ->
                Integer.valueOf(42).equals(cp.getCurveId()) &&
                Double.valueOf(7.7).equals(cp.getTerm()) &&
                Double.valueOf(88.8).equals(cp.getValue())
        ));
    }

    @Test
    void updateCurvePoint_shouldThrowException_whenNotExists() {
        when(curvePointRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> curveService.updateCurvePoint(999, 1, 2.0, 3.0));

        verify(curvePointRepository, never()).save(any());
    }

    @Test
    void deleteCurvePoint_shouldDelete_whenExists() {
        CurvePoint existing = new CurvePoint();
        existing.setId(1);
        when(curvePointRepository.findById(1)).thenReturn(Optional.of(existing));

        curveService.deleteCurvePoint(1);

        verify(curvePointRepository).delete(existing);
    }

    @Test
    void deleteCurvePoint_shouldThrowException_whenNotExists() {
        when(curvePointRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> curveService.deleteCurvePoint(999));

        verify(curvePointRepository, never()).delete(any());
    }
}
