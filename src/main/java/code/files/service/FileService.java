package code.files.service;

import code.files.model.fileModel;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
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

    public List<fileModel> getFolderContent(String baseDir, String path, String type) {
        String fullPath = path != null ? Paths.get(baseDir, path).toString() : baseDir;
        File folder = new File(fullPath);

        File[] folderContent = folder.listFiles();
        if (folderContent != null) {
            List<File> filteredFiles = filterFilesAndFolders(folderContent, type);

            return filteredFiles.isEmpty() ?
                    List.of() :
                    filteredFiles.stream()
                            .map(file -> {
                                String size = file.isDirectory() ? "" : conversion(file.length()) + " KB";
                                long lastModifiec = file.lastModified();
                                Date mod = new Date(lastModifiec);
                                SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
                                String formattedDate = sdf.format(mod);
                                return new fileModel(file.getName(), size, formattedDate);
                            })
                            .collect(Collectors.toList());
        } else {
            return List.of();
        }
    }

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