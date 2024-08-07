package code.files.model;

import lombok.Data;

@Data
public class fileModel {
    private String name;
    private String size;
    private String modificationDate;

    public fileModel(String name, String size, String modificationDate) {
        this.name = name;
        this.modificationDate = modificationDate;
        this.size = size;
    }
}