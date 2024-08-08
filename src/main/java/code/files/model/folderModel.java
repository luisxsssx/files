package code.files.model;

import lombok.Data;

@Data
public class folderModel {
    private String name;
    private String modificationDate;

    public folderModel(String name, String modificationDate) {
        this.name = name;
        this.modificationDate = modificationDate;
    }
}