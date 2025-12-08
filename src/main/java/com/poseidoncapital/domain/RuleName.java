package com.poseidoncapital.domain;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "rule_name")
public class RuleName {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name", length = 125)
    private String name;

    @Column(name = "description", length = 125)
    private String description;

    @Column(name = "json", length = 125)
    private String json;

    @Column(name = "template", length = 512)
    private String template;

    @Column(name = "sql_str", length = 125)
    private String sqlStr;

    @Column(name = "sql_part", length = 125)
    private String sqlPart;
}