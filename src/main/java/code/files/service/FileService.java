package code.files.service;

import org.springframework.stereotype.Service;

import java.io.File;

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
}