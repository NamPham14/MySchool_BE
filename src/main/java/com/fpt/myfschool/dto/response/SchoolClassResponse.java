package com.fpt.myfschool.dto.response;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SchoolClassResponse {
    private Integer id;
    private String name;
    private Integer grade;
    private String academicYear;
}