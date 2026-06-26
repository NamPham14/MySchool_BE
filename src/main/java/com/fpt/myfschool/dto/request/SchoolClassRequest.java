package com.fpt.myfschool.dto.request;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SchoolClassRequest {
    private String name;
    private Integer grade;
    private String academicYear;
}