package com.backend.files.services;

import java.io.File;
import org.springframework.stereotype.Service;

@Service
public class FolderDeletionService {
    public String deleteDirectory(String path) {
        File directory = new File(path);
        if (directory.exists()) {
            if (deleteRecursive(directory)) {
                return "Directory deleted succesfully";
            } else {
                return "Failed to delete directory";
            }
        } else {
            return "Directory does not exist";
        }
    }

    private boolean deleteRecursive(File file) {
        if (file.isDirectory()) {
            for (File subFile : file.listFiles()) {
                if (!deleteRecursive(subFile)) {
                    return false;
                }
            }
        }
        return file.delete();
    }
}
