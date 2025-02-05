package code.files.service;

import code.files.model.fileModel;
import code.files.model.folderModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FolderService {

    private static final DecimalFormat df = new DecimalFormat("#.##");

    @Value("${file.storage.bin}")
    private String binDir;

    @Value("${file.storage.location}")
    private String baseDir;

    public ResponseEntity<Map<String, String>> createFolder(String folderName, String parentFolder){
        String folderPath = (parentFolder == null || parentFolder.isEmpty())
                ? Paths.get(baseDir, folderName).toString()
                : Paths.get(baseDir, parentFolder, folderName).toString();

        File folder = new File(folderPath);
        if (folder.mkdir()) {

            Map<String, String> successResponse = new HashMap<>();
            successResponse.put("message", "Successfully created folder");
            return ResponseEntity.ok(successResponse);
        } else {

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error creating the folder");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
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
            try {
                throw new FileNotFoundException("Parameter with name " + path + " is not found");
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
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

    private String conversion(long bytes) {
        double kilobytes = bytes / 1024.0;
        return df.format(kilobytes);
    }

    public List<Object> allContent(String type) {
        List<Object> filesList = new ArrayList<>();
        File baseDirectory = new File(baseDir);
        File[] initialFiles = baseDirectory.listFiles();

        if (initialFiles != null) {
            List<File> filteredFiles = filterFilesAndFolders(initialFiles, type);
            return filteredFiles.isEmpty() ?
                    List.of() :
                    filteredFiles.stream()
                            .map(file -> {


                                if (file.isDirectory()) {
                                    long folderLastModified = file.lastModified();
                                    Date folderMod = new Date(folderLastModified);
                                    SimpleDateFormat SDF = new SimpleDateFormat("dd-MMM-yyyy");
                                    String formatDate = SDF.format(folderMod);
                                    return new folderModel(file.getName(), formatDate);
                                } else {
                                    long filelastModified= file.lastModified();
                                    Date fileMod = new Date(filelastModified);
                                    SimpleDateFormat sdff = new SimpleDateFormat("dd-MMM-yyyy");
                                    String formattedDated = sdff.format(fileMod);
                                    String size = file.isDirectory() ? "" : conversion(file.length()) + " KB";
                                    return new fileModel(file.getName(), size, formattedDated);
                                }
                            })
                            .collect(Collectors.toList());
        } else {
            return List.of();
        }
    }
}