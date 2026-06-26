package com.fpt.myfschool.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class GenerateFeeReqDto {
    private Integer classId;
    private Integer semesterId;
    private String title;
    private BigDecimal amount;
    private LocalDate dueDate;
}
