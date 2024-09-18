package code.files.model;

import lombok.Data;

@Data
public class fileModel {
    private String name;
    private String size;
    private String creationDate;

    public fileModel(String name, String size, String creationDate) {
        this.name = name;
        this.creationDate = creationDate;
        this.size = size;
    }

}