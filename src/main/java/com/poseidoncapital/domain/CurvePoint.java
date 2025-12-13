package com.poseidoncapital.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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

    @Max(value = 127, message = "The field cannot exceed 127.")
    @Min(value = -127, message = "The field cannot be less than -127.")
    @Column(name = "curve_id")
    private Integer curveId;

    @Column(name = "as_of_date")
    private Timestamp asOfDate;

    @Digits(integer = 308, fraction = 10, message = "Too many digits after the decimal point")
    @Column(name = "term")
    private Double term;


    @Digits(integer = 308, fraction = 10, message = "Too many digits after the decimal point")
    @Column(name = "value")
    private Double value;

    @Column(name = "creation_date")
    private Timestamp creationDate;
}