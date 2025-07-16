package org.example.model;

import lombok.Data;

import java.util.List;

@Data
public class RowError {
    private int row;
    private List<String> messages;
    public RowError(int row, List<String> messages) {
        this.row = row; this.messages = messages;
    }
}
