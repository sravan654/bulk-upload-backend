package org.example.model;

import lombok.Data;

import java.util.List;

@Data
public class UploadResult {
    private int totalRows;
    private long successCount;
    private List<RowError> errors;
}
