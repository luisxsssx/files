package code.files.service;

import code.files.model.fileModel;
import code.files.model.folderModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.task.TaskExecutionProperties;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FileService {

    private static final DecimalFormat df = new DecimalFormat("#.##");

    @Value("${file.storage.bin}")
    private String binDir;

    @Value("${file.storage.location}")
    private String baseDir;

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

    public ResponseEntity<InputStreamResource> downloadFile(String path, String type) throws FileNotFoundException {
        String filePath = Paths.get(baseDir, path).toString();
        File file = new File(filePath);

        if(!file.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        HttpHeaders headers = new HttpHeaders();
        String contentType = "application/octet-stream";
        String headerValue = "attachment; filename=\"" + resource.getFilename() + "\"";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .body(resource);
    }

    public ResponseEntity<Map<String, String>> delete(String path) {
        String targetPath = Paths.get(baseDir, path).toString();
        File targetFileFolder = new File(targetPath);

        if(!targetFileFolder.exists()) {
            Map<String, String> notFoundResponse = new HashMap<>();
            notFoundResponse.put("message", "File not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFoundResponse);
        }

        if(deleted(targetFileFolder)) {
            Map<String, String> successResponse = new HashMap<>();
            successResponse.put("message", "Successfully deleted");
            return ResponseEntity.ok(successResponse);
        } else {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error deleting file or folder");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    public ResponseEntity<String> rename(String currentName, String newName, String folderName) {
        String folderPath = (folderName == null || folderName.isEmpty()) ? baseDir : Paths.get(baseDir, folderName).toString();
        File folder = new File(folderPath);
        if (!folder.exists()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Folder does not exist");
        }

        File currentFileOrFolder = new File(Paths.get(folderPath, currentName).toString());
        if (!currentFileOrFolder.exists()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File or folder does not exist");
        }

        File newFileOrFolder = new File(Paths.get(folderPath, newName).toString());
        if (currentFileOrFolder.renameTo(newFileOrFolder)) {
            return ResponseEntity.ok("File or folder renamed successfully");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error renaming file or folder");
        }
    }

    public ResponseEntity<Map<String, String>> uploadFile(MultipartFile file, String folderName) {
        String folderPath;
        if (folderName == null || folderName.isEmpty()) {
            folderPath = baseDir;
        } else {
            folderPath = Paths.get(baseDir, folderName).toString();
            File folder = new File(folderPath);
            if (!folder.exists()) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Folder does not exist");

                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(errorResponse);
            }
        }

        String filePath = Paths.get(folderPath, file.getOriginalFilename()).toString();
        try {
            FileOutputStream fout = new FileOutputStream(filePath);
            fout.write(file.getBytes());
            fout.close();

            Map<String, String> successResponse = new HashMap<>();
            successResponse.put("message", "File uploaded successfully");

            return ResponseEntity.ok(successResponse);
        } catch (IOException e) {
            e.printStackTrace();

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to upload file");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }

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

    public ResponseEntity<String> moveToPaperBin(String filePath) {
        Path source = Paths.get(baseDir, filePath);
        String filename = source.getFileName().toString();
        Path target = Paths.get(binDir, filename);

        try {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
            return ResponseEntity.ok(filename + " moved to paper bin successfully");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error moving file: " + e.getMessage());
        }
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