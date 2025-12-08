package com.poseidoncapital.domain;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "rating")
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "moodys_rating", length = 125)
    private String moodysRating;

    @Column(name = "sandp_rating", length = 125)
    private String sandPRating;

    @Column(name = "fitch_rating", length = 125)
    private String fitchRating;

    @Column(name = "order_number")
    private Integer orderNumber;
}