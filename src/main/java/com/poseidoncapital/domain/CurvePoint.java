package com.poseidoncapital.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Entity
@Table(name = "curve_point")
public class CurvePoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Max(value = 127, message = "CurveId ne peut pas dépasser 127")
    @Column(name = "curve_id")
    private Integer curveId;

    @Column(name = "as_of_date")
    private Timestamp asOfDate;

    @Column(name = "term")
    private Double term;

    @Column(name = "value")
    private Double value;

    @Column(name = "creation_date")
    private Timestamp creationDate;
}