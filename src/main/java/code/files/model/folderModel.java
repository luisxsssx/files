package code.files.model;

import lombok.Data;

@Data
public class folderModel {
    private String name;
    private String creationDate;

    public folderModel(String name, String creationDate) {
        this.name = name;
        this.creationDate = creationDate;
    }
}