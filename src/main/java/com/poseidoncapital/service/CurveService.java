package com.poseidoncapital.service;

import com.poseidoncapital.domain.CurvePoint;
import com.poseidoncapital.repositories.CurvePointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CurveService {

    private final CurvePointRepository curvePointRepository;

    public void addCurvePoint(Integer curveId, Double term, Double value) {
        CurvePoint curvePoint = new CurvePoint();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        Timestamp asOfDate = Timestamp.valueOf(LocalDate.now().atStartOfDay());

        curvePoint.setCurveId(curveId);
        curvePoint.setAsOfDate(now);
        curvePoint.setTerm(term);
        curvePoint.setValue(value);
        curvePoint.setCreationDate(now);
        curvePointRepository.save(curvePoint);
    }

    public List<CurvePoint> getAllCurvePoints() {
        return curvePointRepository.findAll();
    }

    public CurvePoint getCurvePointsById(Integer id) {
        return curvePointRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid curve point Id:" + id));
    }

    public void updateCurvePoint(Integer id, Integer curveId, Double term, Double value) {
        CurvePoint curvePoint = curvePointRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid curve point Id:" + id));

        curvePoint.setCurveId(curveId);
        curvePoint.setTerm(term);
        curvePoint.setValue(value);
        curvePointRepository.save(curvePoint);
    }

    public void deleteCurvePoint(Integer id) {
        CurvePoint curvePoint = curvePointRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid curve point Id:" + id));
        curvePointRepository.delete(curvePoint);
    }

}
