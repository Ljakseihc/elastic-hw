package com.epam.nosql.search.dto;

import lombok.Data;

import java.util.List;

@Data
public class Employee {

    private String name;
    private String dob;
    private Address address;
    private String email;
    private List<String> skills;
    private int experience;
    private double rating;
    private String description;
    private boolean verified;
    private int salary;

}
