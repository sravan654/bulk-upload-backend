package org.example.validator;


import org.apache.poi.ss.usermodel.*;
import com.opencsv.CSVReader;
import org.example.model.UploadResult;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

public class FileParser {
    public UploadResult parseAndValidate(InputStream in, String filename) {
        // detect extension, parse rows into List<ResourceDto>
        // validate types, FK, duplicates, business rules
        // collect RowError and count successes
        return new UploadResult();
    }
}
