package code.files.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileService {

    private static final DecimalFormat df = new DecimalFormat("#.##");

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

    public Object[] getFolderContent(String baseDir, String path, String type) {
        String fullPath = path != null ? Paths.get(baseDir, path).toString() : baseDir;
        File folder = new File(fullPath);

        File[] folderContent = folder.listFiles();
        if (folderContent != null) {
            List<File> filteredFiles = filterFilesAndFolders(folderContent, type);

            return filteredFiles.isEmpty() ?
                    new Object[0] :
                    filteredFiles.stream()
                            .map(file -> {
                                if (file.isDirectory()) {
                                    long lastModified = file.lastModified();
                                    Date mod = new Date(lastModified);
                                    return "Name: " + file.getName() + ", " + "Modification date: " + mod;
                                } else {
                                    long fileKB = file.length();
                                    return file.getName() + " " + conversion(fileKB) + " KB";
                                }
                            })
                            .toArray(Object[]::new);
        } else {
            return new Object[0];
        }
    }

    // Filter files and folders
    public List<File> filterFilesAndFolders(File[] files, String type) {
        if (type == null || type.isEmpty()) {
            return Arrays.asList(files);
        }
        return Arrays.stream(files)
                .filter(file -> "file".equalsIgnoreCase(type) && file.isFile() ||
                        "folder".equalsIgnoreCase(type) && file.isDirectory())
                .collect(Collectors.toList());
    }

    // Convert file size to megabytes
    private String conversion(long bytes) {
        double kilobytes = bytes / 1024.0;
        return df.format(kilobytes);
    }
}