package com.fpt.myfschool.util;

import com.fpt.myfschool.dto.request.GradeRequest;
import com.fpt.myfschool.entity.User;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ExcelHelper {

    public static String TYPE =  "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    static String[] HEADERS = {"Student ID", "Roll Number", "Full Name", "Midterm Score", "Final Score"};
    static String SHEET = "Grades";

    // kiem tra dinh dang file
    public static boolean hasExcelFormat(MultipartFile file) {
        return TYPE.equals(file.getContentType());
    }

    /**
     * TẠO FILE EXCEL MẪU (EXPORT)
     * @param students
     * @return
     */
    public static ByteArrayInputStream gradesToExcel(List<com.fpt.myfschool.dto.response.StudentGradeResponse> students){
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out =  new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet(SHEET);
            // taọ  Header (Dòng đầu tiên)
            Row headerRow = sheet.createRow(0);
            for(int col = 0; col < HEADERS.length; col++){
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(HEADERS[col]);
            }

            // Đổ dự liệu học sinh  vào dòng tiếp theo
            int rowInx = 1;
            for(com.fpt.myfschool.dto.response.StudentGradeResponse student : students){
                Row row = sheet.createRow(rowInx++);

                // cot 0: Student Id
                row.createCell(0).setCellValue(student.getStudentId());
                // cot 1: ma sv
                row.createCell(1).setCellValue(student.getRollNumber() != null ? student.getRollNumber() : "");
                row.createCell(2).setCellValue(student.getStudentName() != null ? student.getStudentName() : "");
                
                if (student.getMidtermScore() != null) {
                    row.createCell(3).setCellValue(student.getMidtermScore());
                }
                
                if (student.getFinalScore() != null) {
                    row.createCell(4).setCellValue(student.getFinalScore());
                }
            }
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());


        }catch (IOException e){
            throw new RuntimeException("Lỗi khi tạo file Excel mẫu: " + e.getMessage());
        }
    }


    /**
     *  ĐỌC FILE EXCEL ĐỂ LẤY DANH SÁCH ĐIỂM (IMPORT)
     * @param inputStream
     * @param subjectId
     * @param semesterId
     * @return
     */
    public static List<GradeRequest> excelToGrades(InputStream inputStream,Integer subjectId,Integer semesterId) {
        try{
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheet(SHEET);
            List<GradeRequest> gradeRequests = new ArrayList<>();


            //Lay cac dong  du lieu (bo qua dong header)
            for (Row currentRow : sheet) {
                if(currentRow.getRowNum()== 0) continue;

                GradeRequest request = new GradeRequest();
                request.setSubjectId(subjectId);
                request.setSemesterId(semesterId);

                // lay studentId  tu cot 0
                if(currentRow.getCell(0)!=null){
                    request.setStudentId((long) currentRow.getCell(0).getNumericCellValue());
                }

                  // Lấy Midterm Score từ cột 3
                Cell midtermCell = currentRow.getCell(3);
                if(midtermCell != null && midtermCell.getCellType() == CellType.NUMERIC){
                    request.setMidtermScore(midtermCell.getNumericCellValue());
                }

                Cell finalCell = currentRow.getCell(4);
                if(finalCell != null && finalCell.getCellType() == CellType.NUMERIC){
                    request.setFinalScore(finalCell.getNumericCellValue());
                }

                // Nếu có bất kỳ điểm nào được nhập, ta mới đưa vào list
                if(request.getStudentId() != null && (request.getMidtermScore() != null || request.getFinalScore() != null)){
                    gradeRequests.add(request);
                }
            }
        workbook.close();
        return gradeRequests;

        }
        catch (IOException e){
            throw new RuntimeException("Lỗi khi đọc file Excel: " + e.getMessage());
        }
    }

}
