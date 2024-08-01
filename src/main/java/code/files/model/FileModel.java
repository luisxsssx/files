package code.files.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FileModel {
    private String name;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
}