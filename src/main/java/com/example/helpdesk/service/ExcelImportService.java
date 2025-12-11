package com.example.helpdesk.service;

import com.example.helpdesk.entity.Student;
import com.example.helpdesk.entity.Staff;
import com.example.helpdesk.entity.User;
import com.example.helpdesk.repository.StudentRepository;
import com.example.helpdesk.repository.StaffRepository;
import com.example.helpdesk.repository.UserRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for importing users from Excel files.
 * 
 * @author Facility Helpdesk Team
 */
@Service
public class ExcelImportService {

    private static final Logger logger = LoggerFactory.getLogger(ExcelImportService.class);

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final StaffRepository staffRepository;

    public ExcelImportService(
            UserRepository userRepository,
            StudentRepository studentRepository,
            StaffRepository staffRepository) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.staffRepository = staffRepository;
    }

    /**
     * Import users from Excel file.
     * Expected columns: Email, Password, Full Name, Role, Student Code, Class Name, Position, Department Name
     * 
     * @param file the Excel file to import
     * @return ImportResult containing success count, failure count, and error messages
     */
    @Transactional
    public ImportResult importUsersFromExcel(MultipartFile file) {
        logger.info("Starting Excel import for file: {}", file.getOriginalFilename());

        ImportResult result = new ImportResult();
        List<String> errors = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            
            // Skip header row (row 0)
            int totalRows = sheet.getLastRowNum();
            if (totalRows < 1) {
                errors.add("Excel file is empty or has no data rows");
                result.setErrors(errors);
                return result;
            }

            // Read header row to find column indices
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                errors.add("Header row is missing");
                result.setErrors(errors);
                return result;
            }

            int emailCol = findColumnIndex(headerRow, "Email");
            int passwordCol = findColumnIndex(headerRow, "Password");
            int fullNameCol = findColumnIndex(headerRow, "Full Name");
            int roleCol = findColumnIndex(headerRow, "Role");
            int studentCodeCol = findColumnIndex(headerRow, "Student Code");
            int classNameCol = findColumnIndex(headerRow, "Class Name");
            int positionCol = findColumnIndex(headerRow, "Position");

            // Validate required columns
            if (emailCol == -1 || passwordCol == -1 || fullNameCol == -1 || roleCol == -1) {
                errors.add("Missing required columns. Required: Email, Password, Full Name, Role");
                result.setErrors(errors);
                return result;
            }

            // Process data rows
            for (int i = 1; i <= totalRows; i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                try {
                    String email = getCellValueAsString(row.getCell(emailCol));
                    String password = getCellValueAsString(row.getCell(passwordCol));
                    String fullName = getCellValueAsString(row.getCell(fullNameCol));
                    String role = getCellValueAsString(row.getCell(roleCol));
                    String studentCode = studentCodeCol != -1 ? getCellValueAsString(row.getCell(studentCodeCol)) : null;
                    String className = classNameCol != -1 ? getCellValueAsString(row.getCell(classNameCol)) : null;
                    String position = positionCol != -1 ? getCellValueAsString(row.getCell(positionCol)) : null;

                    // Validate required fields
                    if (email == null || email.trim().isEmpty()) {
                        errors.add("Row " + (i + 1) + ": Email is required");
                        result.incrementFailed();
                        continue;
                    }

                    if (password == null || password.trim().isEmpty()) {
                        errors.add("Row " + (i + 1) + ": Password is required");
                        result.incrementFailed();
                        continue;
                    }

                    if (fullName == null || fullName.trim().isEmpty()) {
                        errors.add("Row " + (i + 1) + ": Full Name is required");
                        result.incrementFailed();
                        continue;
                    }

                    if (role == null || role.trim().isEmpty()) {
                        errors.add("Row " + (i + 1) + ": Role is required");
                        result.incrementFailed();
                        continue;
                    }

                    role = role.toUpperCase().trim();
                    if (!role.equals("STUDENT") && !role.equals("STAFF") && !role.equals("ADMIN")) {
                        errors.add("Row " + (i + 1) + ": Invalid role '" + role + "'. Must be STUDENT, STAFF, or ADMIN");
                        result.incrementFailed();
                        continue;
                    }

                    // Check if user already exists
                    if (userRepository.existsByEmail(email.trim())) {
                        errors.add("Row " + (i + 1) + ": Email '" + email + "' already exists");
                        result.incrementFailed();
                        continue;
                    }

                    // Create user
                    User user = User.builder()
                            .email(email.trim())
                            .password(password.trim())
                            .fullName(fullName.trim())
                            .role(role)
                            .build();

                    user = userRepository.save(user);

                    // Create role-specific entities
                    if (role.equals("STUDENT")) {
                        if (studentCode != null && !studentCode.trim().isEmpty()) {
                            // Check if student code already exists
                            if (studentRepository.findByStudentCode(studentCode.trim()).isPresent()) {
                                errors.add("Row " + (i + 1) + ": Student Code '" + studentCode + "' already exists");
                                userRepository.delete(user);
                                result.incrementFailed();
                                continue;
                            }

                            Student student = Student.builder()
                                    .studentCode(studentCode.trim())
                                    .className(className != null && !className.trim().isEmpty() ? className.trim() : "N/A")
                                    .user(user)
                                    .build();
                            studentRepository.save(student);
                        }
                    } else if (role.equals("STAFF")) {
                        Staff staff = Staff.builder()
                                .position(position != null && !position.trim().isEmpty() ? position.trim() : "Staff")
                                .user(user)
                                .build();
                        staffRepository.save(staff);
                    }

                    result.incrementSuccess();
                    logger.debug("Successfully imported user: {}", email);

                } catch (Exception e) {
                    logger.error("Error processing row {}: {}", i + 1, e.getMessage());
                    errors.add("Row " + (i + 1) + ": " + e.getMessage());
                    result.incrementFailed();
                }
            }

        } catch (IOException e) {
            logger.error("Error reading Excel file: {}", e.getMessage());
            errors.add("Error reading Excel file: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error during Excel import: {}", e.getMessage());
            errors.add("Unexpected error: " + e.getMessage());
        }

        result.setErrors(errors);
        logger.info("Excel import completed. Success: {}, Failed: {}", result.getSuccessCount(), result.getFailedCount());
        return result;
    }

    /**
     * Find column index by header name (case-insensitive).
     */
    private int findColumnIndex(Row headerRow, String columnName) {
        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            Cell cell = headerRow.getCell(i);
            if (cell != null) {
                String cellValue = getCellValueAsString(cell);
                if (cellValue != null && cellValue.trim().equalsIgnoreCase(columnName)) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Get cell value as string.
     */
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return null;
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    // Convert numeric to string without decimal if it's a whole number
                    double numericValue = cell.getNumericCellValue();
                    if (numericValue == (long) numericValue) {
                        return String.valueOf((long) numericValue);
                    } else {
                        return String.valueOf(numericValue);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }

    /**
     * Generate Excel template file with headers and sample data.
     * 
     * @return byte array of the Excel file
     * @throws IOException if error creating file
     */
    public byte[] generateExcelTemplate() throws IOException {
        logger.info("Generating Excel template file");

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Users");

            // Create header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.ORANGE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            // Create data style (to prevent hyperlink auto-detection)
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setWrapText(false);
            // Set format to text to prevent Excel from auto-detecting email/URL as hyperlink
            DataFormat format = workbook.createDataFormat();
            dataStyle.setDataFormat(format.getFormat("@")); // "@" means text format

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {
                "Email", "Password", "Full Name", "Role", 
                "Student Code", "Class Name", "Position", "Department Name"
            };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Create sample data rows
            String[][] sampleData = {
                {"student1@fpt.edu.vn", "12345", "Nguyễn Văn A", "STUDENT", "SE12345", "SE1701", "", ""},
                {"student2@fpt.edu.vn", "12345", "Trần Thị B", "STUDENT", "SE12346", "SE1702", "", ""},
                {"staff1@fpt.edu.vn", "12345", "Lê Văn C", "STAFF", "", "", "IT Support", "IT Department"},
                {"admin1@fpt.edu.vn", "12345", "Phạm Thị D", "ADMIN", "", "", "", ""}
            };

            for (int i = 0; i < sampleData.length; i++) {
                Row row = sheet.createRow(i + 1);
                for (int j = 0; j < sampleData[i].length; j++) {
                    Cell cell = row.createCell(j);
                    // Apply text format style to prevent Excel from auto-detecting email/URL as hyperlink
                    cell.setCellValue(sampleData[i][j]);
                    cell.setCellStyle(dataStyle);
                }
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    /**
     * Result class for Excel import operation.
     */
    public static class ImportResult {
        private int successCount = 0;
        private int failedCount = 0;
        private List<String> errors = new ArrayList<>();

        public void incrementSuccess() {
            successCount++;
        }

        public void incrementFailed() {
            failedCount++;
        }

        public int getSuccessCount() {
            return successCount;
        }

        public int getFailedCount() {
            return failedCount;
        }

        public List<String> getErrors() {
            return errors;
        }

        public void setErrors(List<String> errors) {
            this.errors = errors;
        }
    }
}

