package org.example;

import org.example.model.RowError;
import org.example.model.UploadResult;
import org.example.validator.FileParser;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Service
public class BulkUploadService {
    private final FileParser parser = new FileParser();

    public UploadResult handleUpload(MultipartFile file) {
        try (var in = file.getInputStream()) {
            return parser.parseAndValidate(in, file.getOriginalFilename());
        } catch (Exception e) {
            // wrap into UploadResult with global error
            return new UploadResult(
//                    0, 0, List.of(new RowError(0, List.of(e.getMessage())))
            );
        }
    }
}
