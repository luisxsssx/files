package code.files.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FileModel {
    private String name;
    private String path;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private long size;
}