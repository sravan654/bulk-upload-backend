package org.example.validator;

import org.apache.poi.ss.usermodel.*;
import com.opencsv.CSVReader;
import org.example.model.UploadResult;
import org.example.model.ResourceDto;
import org.example.model.RowError;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class FileParser {
    public UploadResult parseAndValidate(InputStream in, String filename) {
        List<ResourceDto> resources = new ArrayList<>();
        List<RowError> errors = new ArrayList<>();
        int successCount = 0;

        String ext = getExtension(filename);
        try {
            if (ext.equalsIgnoreCase("csv")) {
                resources = parseCsv(in, errors);
            } else if (ext.equalsIgnoreCase("xlsx") || ext.equalsIgnoreCase("xls")) {
                resources = parseExcel(in, errors);
            } else {
                errors.add(new RowError(0, "Unsupported file type: " + ext));
            }
        } catch (Exception e) {
            errors.add(new RowError(0, "Failed to parse file: " + e.getMessage()));
        }

        // Validate resources
        Set<String> seen = new HashSet<>();
        for (int i = 0; i < resources.size(); i++) {
            ResourceDto dto = resources.get(i);
            List<String> rowErrors = validateRow(dto, seen);
            if (rowErrors.isEmpty()) {
                successCount++;
            } else {
                errors.add(new RowError(i + 1, String.join("; ", rowErrors)));
            }
        }

        return new UploadResult(successCount, errors);
    }

    private String getExtension(String filename) {
        int idx = filename.lastIndexOf('.');
        return (idx > 0) ? filename.substring(idx + 1) : "";
    }

    private List<ResourceDto> parseCsv(InputStream in, List<RowError> errors) throws Exception {
        List<ResourceDto> list = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new InputStreamReader(in))) {
            String[] header = reader.readNext();
            if (header == null) {
                errors.add(new RowError(0, "CSV file is empty"));
                return list;
            }
            String[] row;
            int rowNum = 1;
            while ((row = reader.readNext()) != null) {
                rowNum++;
                try {
                    list.add(ResourceDto.fromCsvRow(row));
                } catch (Exception e) {
                    errors.add(new RowError(rowNum, "Invalid row: " + e.getMessage()));
                }
            }
        }
        return list;
    }

    private List<ResourceDto> parseExcel(InputStream in, List<RowError> errors) throws Exception {
        List<ResourceDto> list = new ArrayList<>();
        try (Workbook workbook = WorkbookFactory.create(in)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> it = sheet.iterator();
            if (!it.hasNext()) {
                errors.add(new RowError(0, "Excel sheet is empty"));
                return list;
            }
            Row header = it.next();
            int rowNum = 1;
            while (it.hasNext()) {
                rowNum++;
                Row row = it.next();
                try {
                    list.add(ResourceDto.fromExcelRow(row));
                } catch (Exception e) {
                    errors.add(new RowError(rowNum, "Invalid row: " + e.getMessage()));
                }
            }
        }
        return list;
    }

    private List<String> validateRow(ResourceDto dto, Set<String> seen) {
        List<String> errs = new ArrayList<>();
        // Example validations:
        if (dto.getId() == null || dto.getId().isEmpty()) {
            errs.add("Missing ID");
        }
        if (!seen.add(dto.getId())) {
            errs.add("Duplicate ID: " + dto.getId());
        }
        // Add more FK, type, and business rule validations as needed
        return errs;
    }
}