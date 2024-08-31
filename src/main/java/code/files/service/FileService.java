package code.files.service;

import code.files.model.fileModel;
import code.files.model.folderModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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
    public boolean deleted(File fileOrFolder) {
        if(fileOrFolder.isDirectory()) {
            File[] contents = fileOrFolder.listFiles();
            if (contents != null) {
                for(File file : contents) {
                    if(!deleted(file)) {
                        return false;
                    }
                }
            }
        }
        return fileOrFolder.delete();
    }

    // Move file to trash
    public boolean moveToTrash(File fileOrFolder) {
        if(fileOrFolder.isDirectory()) {
            File[] contents = fileOrFolder.listFiles();
            if (contents != null) {
                for(File file : contents) {
                    if(!deleted(file)) {
                        return false;
                    }
                }
            }
        }

        return fileOrFolder.delete();
    }


    public List<Object> getFolderContent(String baseDir, String path, String type) {
        String fullPath = path != null ? Paths.get(baseDir, path).toString() : baseDir;
        File folder = new File(fullPath);

        File[] folderContent = folder.listFiles();
        if (folderContent != null) {
            List<File> filteredFiles = filterFilesAndFolders(folderContent, type);
            return filteredFiles.isEmpty() ?
                    List.of() :
                    filteredFiles.stream()
                            .map(file -> {

                                if(file.isDirectory()) {
                                    long lastModified= file.lastModified();
                                    Date mod = new Date(lastModified);
                                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
                                    String formattedDate = sdf.format(mod);
                                    return new folderModel(file.getName(), formattedDate);
                                } else {
                                    long lastModified= file.lastModified();
                                    Date mod = new Date(lastModified);
                                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
                                    String formattedDate = sdf.format(mod);
                                    String size = file.isDirectory() ? "" : conversion(file.length()) + " KB";
                                    return new fileModel(file.getName(), size, formattedDate);
                                }
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

    // Get metadata form files
    public ResponseEntity<byte[]> getFileResponse(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            byte[] media = Files.readAllBytes(file.toPath());
            HttpHeaders headers = new HttpHeaders();
            headers.setCacheControl("no-cache");
            headers.setContentType(MediaType.valueOf(Files.probeContentType(file.toPath())));

            return new ResponseEntity<>(media, headers, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}