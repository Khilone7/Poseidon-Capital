package com.poseidoncapital.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Entity
@Table(name = "Bid_list")
public class BidList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bid_list_id")
    private Integer bidListId;

    @Size(max = 30, min = 3, message = "The field must be between 3 and 30 characters long.")
    @Column(name = "account")
    private String account;

    @Size(max = 30, min = 3, message = "The field must be between 3 and 30 characters long.")
    @Column(name = "type")
    private String type;

    @Digits(integer = 308, fraction = 10, message = "Too many digits after the decimal point")
    @Column(name = "bid_quantity")
    private Double bidQuantity;

    @Column(name = "ask_quantity")
    private Double askQuantity;

    @Column(name = "bid")
    private Double bid;

    @Column(name = "ask")
    private Double ask;

    @Column(name = "benchmark", length = 125)
    private String benchmark;

    @Column(name = "bid_list_date")
    private Timestamp bidListDate;

    @Column(name = "commentary", length = 125)
    private String commentary;

    @Column(name = "security", length = 125)
    private String security;

    @Column(name = "status", length = 10)
    private String status;

    @Column(name = "trader", length = 125)
    private String trader;

    @Column(name = "book", length = 125)
    private String book;

    @Column(name = "creation_name", length = 125)
    private String creationName;

    @Column(name = "creation_date")
    private Timestamp creationDate;

    @Column(name = "revision_name", length = 125)
    private String revisionName;

    @Column(name = "revision_date")
    private Timestamp revisionDate;

    @Column(name = "deal_name", length = 125)
    private String dealName;

    @Column(name = "deal_type", length = 125)
    private String dealType;

    @Column(name = "source_list_id", length = 125)
    private String sourceListId;

    @Column(name = "side", length = 125)
    private String side;
}