package com.backend.files.controller;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;

@RestController
public class FileController {

    private String path = "/home/luisxsssx/Pictures/Upload/";

    // Upload files
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String uploadFile(@RequestParam("file") MultipartFile file,
                             @RequestParam("dir") String dir) {
        String uploadsDir = path;
        File directory = new File(uploadsDir+ File.separator + dir);
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
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new String[]{"File not found or empty"});
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
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new String[]{"Directory not found or empty"});
        }
    }

    // Create dir
    @RequestMapping(value = "/createDir", method = RequestMethod.POST)
    public String createDir(@RequestParam("dir") String dir) {
        String directoryPath = path + dir;
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            if (directory.mkdirs()) {
                return "Directory created successfully";
            } else {
                return "Failed to create directory";
            }
        } else {
            return "Directory already exists";
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

    // Delete folders
    @RequestMapping(value = "/deleteDir", method = RequestMethod.DELETE)
    public String deleteDirectory(@RequestParam("dir") String dir) {
        String dirPath = path + dir;
        File directory = new File(dirPath);
        if (directory.exists()) {
            if (deleteRecursive(directory)) {
                return "Directory deleted successfully";
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