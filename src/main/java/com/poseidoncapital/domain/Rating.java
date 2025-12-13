package com.poseidoncapital.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "rating")
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Size(max = 125, message = "The field cannot exceed {max} characters.")
    @Column(name = "moodys_rating")
    private String moodysRating;

    @Size(max = 125, message = "The field cannot exceed {max} characters.")
    @Column(name = "sandp_rating")
    private String sandPRating;

    @Size(max = 125, message = "The field cannot exceed {max} characters.")
    @Column(name = "fitch_rating")
    private String fitchRating;

    @Max(value = 127, message = "The field cannot exceed 127.")
    @Min(value = -127, message = "The field cannot be less than -127.")
    @Column(name = "order_number")
    private Integer orderNumber;
}