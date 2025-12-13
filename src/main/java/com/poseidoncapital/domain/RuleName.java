package com.poseidoncapital.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "rule_name")
public class RuleName {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Size(max = 125, message = "The field cannot exceed {max} characters.")
    @Column(name = "name")
    private String name;

    @Size(max = 125, message = "The field cannot exceed {max} characters.")
    @Column(name = "description")
    private String description;

    @Size(max = 125, message = "The field cannot exceed {max} characters.")
    @Column(name = "json")
    private String json;

    @Size(max = 512, message = "The field cannot exceed {max} characters.")
    @Column(name = "template")
    private String template;

    @Size(max = 125, message = "The field cannot exceed {max} characters.")
    @Column(name = "sql_str")
    private String sqlStr;

    @Size(max = 125, message = "The field cannot exceed {max} characters.")
    @Column(name = "sql_part")
    private String sqlPart;
}