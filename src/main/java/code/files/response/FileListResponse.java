package code.files.response;

import code.files.model.FileModel;
import lombok.Data;

import java.util.List;

@Data
public class FileListResponse {
    private List<FileModel> files;
    private String message;

    public FileListResponse(List<FileModel> files, String message) {
        this.files = files;
        this.message = message;
    }
}