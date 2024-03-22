package com.backend.files.controller;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.backend.files.services.FolderDeletionService;
import com.backend.files.services.FolderService;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
public class FileController {

    private String path = "/home/luisxsssx/Documents/Code/files/Upload/";

    private final FolderDeletionService folderDeletionService;

    private final FolderService folderService;

    public FileController(FolderService folderService, FolderDeletionService folderDeletionService) {
        this.folderService = folderService;
        this.folderDeletionService = folderDeletionService;
    }

    // Upload files
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String uploadFile(@RequestParam("file") MultipartFile file,
            @RequestParam("dir") String dir) {
        String uploadsDir = path;
        File directory = new File(uploadsDir + File.separator + dir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Construct the full file path using the given directory name
        String filePath = directory.getAbsolutePath() + File.separator + file.getOriginalFilename();
        String fileUploadStatus;

        try {
            FileOutputStream fout = new FileOutputStream(filePath);
            fout.write(file.getBytes());
            fout.close();
            fileUploadStatus = "File uploaded successfully";
        } catch (Exception e) {
            e.printStackTrace();
            fileUploadStatus = "Error in uploading file: " + e.getMessage();
        }
        return fileUploadStatus;
    }

    // Download files
    @RequestMapping(value = "/download/{folder}/{filename:.+}", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable("folder") String folder,
            @PathVariable("filename") String filename) throws FileNotFoundException {

        // Construct the full path of the file using the path variable
        String filePath = path + folder + "/" + filename;

        // Create an instance of the file
        File file = new File(filePath);

        // Check if the file exists
        if (!file.exists()) {
            String errorMessage = "File Not Found: " + filename;
        }

        // Create an InputStreamResource for the file
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        // Set content type and download header
        String contentType = "application/octet-stream";
        String headerValue = "attachment; filename=\"" + file.getName() + "\"";

        // Return response with file
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .body(resource);
    }

    // Get files
    @RequestMapping(value = "/getAllFiles", method = RequestMethod.GET)
    public ResponseEntity<String[]> getFiles() {
        try {
            String folderPath = path;
            File directory = new File(folderPath);
            String[] files = directory.list();
            if (files == null) {
                throw new FileNotFoundException("File not found or empty");
            }
            return ResponseEntity.ok(files);
        } catch (FileNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new String[] { "File not found or empty" });
        }
    }

    // Get files in a specific directory
    @RequestMapping(value = "/getFiles", method = RequestMethod.GET)
    public ResponseEntity<String[]> getFiles(@RequestParam("dir") String dir) {
        try {
            String folderPath = path + File.separator + dir;
            File directory = new File(folderPath);
            String[] files = directory.list();
            if (files == null) {
                throw new FileNotFoundException("Directory not found or empty");
            }
            return ResponseEntity.ok(files);
        } catch (FileNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new String[] { "Directory not found or empty" });
        }
    }

    // Create dir
    @RequestMapping(value = "/createDir", method = RequestMethod.POST)
    public String createDir(@RequestParam("parentDir") String parentDir,
            @RequestParam("newDirName") String newDirName) {
        Path directoryPath;

        // Verifica si se especificó una carpeta padre
        if (parentDir != null && !parentDir.isEmpty()) {
            directoryPath = Paths.get(path, parentDir, newDirName);
        } else {
            directoryPath = Paths.get(path, newDirName);
        }

        try {
            // Intenta crear el directorio
            Files.createDirectories(directoryPath);
            return "Directory created succesfully";
        } catch (Exception e) {
            // Si hay un error, devuelve un mensaje de error
            return "Failed to create directory: " + e.getMessage();
        }
    }

    // Delete files
    @RequestMapping(value = "/deleteFile", method = RequestMethod.DELETE)
    public String deleteFile(@RequestParam("filename") String filename,
            @RequestParam("dir") String dir) {
        String filePath = path + dir + File.separator + filename;
        File file = new File(filePath);
        if (file.exists()) {
            if (file.delete()) {
                return "File deleted successfully";
            } else {
                return "Failed to delete file";
            }
        } else {
            return "File does not exist";
        }
    }

    @RequestMapping(value = "/getFolders", method = RequestMethod.GET)
    public ResponseEntity<Map<String, String>> getFolders(
            @RequestParam(value = "dir", required = false) String dir) {
        try {
            String folderPath = path;
            if (dir != null && !dir.isEmpty()) {
                folderPath += File.separator + dir;
            }
            File directory = new File(folderPath);
            Map<String, String> folders = new HashMap<>();
            if (directory.exists() && directory.isDirectory()) {
                File[] files = directory.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.isDirectory()) {
                            folders.put(file.getName(), file.getName());
                        }
                    }
                }
                return ResponseEntity.ok(folders);
            } else {
                throw new FileNotFoundException("Directory not found or empty");
            }
        } catch (FileNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new HashMap<String, String>() {
                {
                    put("error", "Directory not found or empty");
                }
            });
        }
    }

    // Delete folders
    @RequestMapping(value = "/deletDir", method = RequestMethod.DELETE)
    public String deleteDirectory(@RequestParam("dir") String dir) {
        String dirPath = path + dir;
        return folderDeletionService.deleteDirectory(dirPath);
    }
}