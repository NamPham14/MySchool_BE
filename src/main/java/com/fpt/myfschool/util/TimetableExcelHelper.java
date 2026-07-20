package com.fpt.myfschool.util;

import com.fpt.myfschool.dto.request.TimetableRequest;
import com.fpt.myfschool.entity.SchoolClass;
import com.fpt.myfschool.entity.Subject;
import com.fpt.myfschool.entity.User;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TimetableExcelHelper {
    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    static String[] HEADERS = {"Lớp học", "Thứ", "Tiết", "Giờ bắt đầu", "Giờ kết thúc", "Môn học", "Giáo viên", "Phòng", "Ghi chú"};
    static String SHEET = "Timetable";

    public static boolean hasExcelFormat(MultipartFile file) {
        return TYPE.equals(file.getContentType());
    }

    public static ByteArrayInputStream createTemplate(List<SchoolClass> classes, List<Subject> subjects, List<User> teachers) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet(SHEET);
            
            // Header
            Row headerRow = sheet.createRow(0);
            for (int col = 0; col < HEADERS.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(HEADERS[col]);
            }

            // Create hidden reference sheet for Data Validation
            Sheet refSheet = workbook.createSheet("ReferenceData");
            workbook.setSheetHidden(1, true); // Hide the reference sheet
            
            // Populate Reference Data
            Row refHeader = refSheet.createRow(0);
            refHeader.createCell(0).setCellValue("Classes");
            refHeader.createCell(1).setCellValue("Subjects");
            refHeader.createCell(2).setCellValue("Teachers");

            int maxRows = Math.max(classes.size(), Math.max(subjects.size(), teachers.size()));
            for (int i = 0; i < maxRows; i++) {
                Row row = refSheet.createRow(i + 1);
                if (i < classes.size()) row.createCell(0).setCellValue(classes.get(i).getName());
                if (i < subjects.size()) row.createCell(1).setCellValue(subjects.get(i).getName());
                if (i < teachers.size()) row.createCell(2).setCellValue(teachers.get(i).getFullName() + " (" + teachers.get(i).getEmail() + ")");
            }

            // Data Validation Definitions
            DataValidationHelper validationHelper = sheet.getDataValidationHelper();
            
            // 1. Lớp học (Column A / 0)
            if (!classes.isEmpty()) {
                DataValidationConstraint classConstraint = validationHelper.createFormulaListConstraint("ReferenceData!$A$2:$A$" + (classes.size() + 1));
                CellRangeAddressList classAddressList = new CellRangeAddressList(1, 1000, 0, 0);
                DataValidation classValidation = validationHelper.createValidation(classConstraint, classAddressList);
                sheet.addValidationData(classValidation);
            }

            // 2. Thứ (Column B / 1)
            DataValidationConstraint dayConstraint = validationHelper.createExplicitListConstraint(new String[]{"2", "3", "4", "5", "6", "7"});
            CellRangeAddressList dayAddressList = new CellRangeAddressList(1, 1000, 1, 1);
            sheet.addValidationData(validationHelper.createValidation(dayConstraint, dayAddressList));

            // 3. Môn học (Column F / 5)
            if (!subjects.isEmpty()) {
                DataValidationConstraint subjectConstraint = validationHelper.createFormulaListConstraint("ReferenceData!$B$2:$B$" + (subjects.size() + 1));
                CellRangeAddressList subjectAddressList = new CellRangeAddressList(1, 1000, 5, 5);
                sheet.addValidationData(validationHelper.createValidation(subjectConstraint, subjectAddressList));
            }

            // 4. Giáo viên (Column G / 6)
            if (!teachers.isEmpty()) {
                DataValidationConstraint teacherConstraint = validationHelper.createFormulaListConstraint("ReferenceData!$C$2:$C$" + (teachers.size() + 1));
                CellRangeAddressList teacherAddressList = new CellRangeAddressList(1, 1000, 6, 6);
                sheet.addValidationData(validationHelper.createValidation(teacherConstraint, teacherAddressList));
            }

            // Sample Row
            Row row = sheet.createRow(1);
            if (!classes.isEmpty()) row.createCell(0).setCellValue(classes.get(0).getName());
            row.createCell(1).setCellValue("2");
            row.createCell(2).setCellValue("Tiết 1");
            row.createCell(3).setCellValue("07:15");
            row.createCell(4).setCellValue("08:00");
            if (!subjects.isEmpty()) row.createCell(5).setCellValue(subjects.get(0).getName());
            if (!teachers.isEmpty()) row.createCell(6).setCellValue(teachers.get(0).getFullName() + " (" + teachers.get(0).getEmail() + ")");
            row.createCell(7).setCellValue("P201");
            row.createCell(8).setCellValue("");

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi tạo file Excel: " + e.getMessage());
        }
    }

    public static List<TimetableRequest> parseExcel(InputStream is, List<SchoolClass> classes, List<Subject> subjects, List<User> teachers) {
        try (Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheet(SHEET);
            if (sheet == null) sheet = workbook.getSheetAt(0);
            
            List<TimetableRequest> requests = new ArrayList<>();
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header
                
                // Check if row is empty
                if (row.getCell(0) == null || row.getCell(0).getStringCellValue().trim().isEmpty()) continue;

                TimetableRequest request = new TimetableRequest();
                
                // Lớp học
                String className = row.getCell(0).getStringCellValue();
                SchoolClass sClass = classes.stream().filter(c -> c.getName().equals(className)).findFirst().orElseThrow(() -> new RuntimeException("Không tìm thấy lớp học: " + className + " ở dòng " + (row.getRowNum() + 1)));
                request.setClassId(sClass.getId());

                // Thứ
                Cell dayCell = row.getCell(1);
                if (dayCell.getCellType() == CellType.STRING) {
                    request.setDayOfWeek(Integer.parseInt(dayCell.getStringCellValue()));
                } else if (dayCell.getCellType() == CellType.NUMERIC) {
                    request.setDayOfWeek((int) dayCell.getNumericCellValue());
                }

                // Tiết
                Cell periodCell = row.getCell(2);
                String periodStr = "";
                if (periodCell != null) {
                    if (periodCell.getCellType() == CellType.STRING) {
                        periodStr = periodCell.getStringCellValue();
                    } else if (periodCell.getCellType() == CellType.NUMERIC) {
                        periodStr = String.valueOf((int) periodCell.getNumericCellValue());
                    }
                }
                if (periodStr.startsWith("Tiết ")) {
                    periodStr = periodStr.substring(5).trim();
                }
                request.setPeriod(periodStr);

                // Giờ bắt đầu/kết thúc
                Cell startCell = row.getCell(3);
                Cell endCell = row.getCell(4);
                
                try {
                    if (startCell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(startCell)) {
                        request.setStartTime(startCell.getLocalDateTimeCellValue().toLocalTime());
                    } else if (startCell.getCellType() == CellType.STRING) {
                        String timeStr = startCell.getStringCellValue().trim();
                        // Handle formatting like "7:15" -> "07:15"
                        if (timeStr.length() == 4 && timeStr.charAt(1) == ':') {
                            timeStr = "0" + timeStr;
                        }
                        request.setStartTime(LocalTime.parse(timeStr));
                    }
                    
                    if (endCell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(endCell)) {
                        request.setEndTime(endCell.getLocalDateTimeCellValue().toLocalTime());
                    } else if (endCell.getCellType() == CellType.STRING) {
                        String timeStr = endCell.getStringCellValue().trim();
                        if (timeStr.length() == 4 && timeStr.charAt(1) == ':') {
                            timeStr = "0" + timeStr;
                        }
                        request.setEndTime(LocalTime.parse(timeStr));
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Sai định dạng giờ ở dòng " + (row.getRowNum() + 1) + ". Ví dụ đúng: 07:15");
                }

                // Môn học
                String subjectName = row.getCell(5).getStringCellValue();
                Subject subject = subjects.stream().filter(s -> s.getName().equals(subjectName)).findFirst().orElseThrow(() -> new RuntimeException("Không tìm thấy môn học: " + subjectName + " ở dòng " + (row.getRowNum() + 1)));
                request.setSubjectId(subject.getId());

                // Giáo viên
                Cell teacherCell = row.getCell(6);
                if (teacherCell != null && teacherCell.getCellType() == CellType.STRING) {
                    String teacherStr = teacherCell.getStringCellValue().trim();
                    if (!teacherStr.isEmpty()) {
                        int startIdx = teacherStr.lastIndexOf("(");
                        int endIdx = teacherStr.lastIndexOf(")");
                        
                        User teacher;
                        if (startIdx != -1 && endIdx != -1 && startIdx < endIdx) {
                            String email = teacherStr.substring(startIdx + 1, endIdx).trim();
                            teacher = teachers.stream().filter(t -> t.getEmail().equals(email)).findFirst().orElseThrow(() -> new RuntimeException("Không tìm thấy giáo viên với email: " + email + " ở dòng " + (row.getRowNum() + 1)));
                        } else {
                            String finalTeacherStr = teacherStr;
                            teacher = teachers.stream().filter(t -> t.getFullName().equalsIgnoreCase(finalTeacherStr) || ("GV. " + t.getFullName()).equalsIgnoreCase(finalTeacherStr)).findFirst().orElseThrow(() -> new RuntimeException("Không tìm thấy giáo viên với tên: " + finalTeacherStr + " ở dòng " + (row.getRowNum() + 1)));
                        }
                        request.setTeacherId(teacher.getId());
                    }
                }

                // Phòng
                if (row.getCell(7) != null) {
                    request.setRoom(row.getCell(7).getStringCellValue());
                }

                // Ghi chú
                if (row.getCell(8) != null) {
                    request.setNote(row.getCell(8).getStringCellValue());
                }

                request.setIsExam(false);
                requests.add(request);
            }
            return requests;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi phân tích file Excel: " + e.getMessage());
        }
    }
}
