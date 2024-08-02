package code.files.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileService {

    // Helper method to delete files and directories recursively
    public boolean deleteRecursively(File fileOrFolder) {
        if(fileOrFolder.isDirectory()) {
            File[] contents = fileOrFolder.listFiles();
            if (contents != null) {
                for(File file : contents) {
                    if(!deleteRecursively(file)) {
                        return false;
                    }
                }
            }
        }
        return fileOrFolder.delete();
    }

    // Filter files and folders
    public List<File> filterFilesAndFolders(File[] files, String type) {
        List<File> result = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                switch (type != null ? type.toLowerCase() : "") {
                    case "folder":
                        if (file.isDirectory()) {
                            result.add(file);
                        }
                        break;
                    case "file":
                        if (file.isFile()) {
                            result.add(file);
                        }
                        break;
                    default:
                        result.add(file);
                        break;
                }
            }
        }
        return result;
    }
}