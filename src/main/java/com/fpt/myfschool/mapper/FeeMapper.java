package com.fpt.myfschool.mapper;
import com.fpt.myfschool.dto.response.FeeInvoiceDto;
import com.fpt.myfschool.dto.response.FeeTransactionDto;
import com.fpt.myfschool.entity.FeeInvoice;
import com.fpt.myfschool.entity.FeeTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FeeMapper {
    @Mapping(target = "semesterName", source = "semester.name")
    @Mapping(target = "studentId", source = "student.id")
    @Mapping(target = "studentName", source = "student.fullName")
    @Mapping(target = "studentRollNumber", source = "student.rollNumber")
    FeeInvoiceDto toInvoiceDto(FeeInvoice invoice);

    FeeTransactionDto toTransactionDto(FeeTransaction transaction);
}