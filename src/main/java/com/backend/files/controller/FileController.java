package com.backend.files.controller;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.util.Arrays;

@RestController
public class FileController {

    private String path = "/home/luisxsssx/Pictures/";

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        String uploadsDir = path;
        File directory = new File(uploadsDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Agregar el nombre del directorio dinámico al final de la ruta
        String filePath = uploadsDir + File.separator + directory + File.separator + file.getOriginalFilename();
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

    @RequestMapping(value = "/getFiles", method = RequestMethod.GET)
    public String[] getFiles() {
        String folderPath = path;
        File directory = new File(folderPath);
        return directory.list();
    }

    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> downloadFile(@RequestParam("path") String filename) throws FileNotFoundException {
        String fileUploadPath = path;
        String[] filenames = this.getFiles();
        boolean contains = Arrays.asList(filenames).contains(filename);
        if (!contains) {
            return new ResponseEntity("File Not Found", HttpStatus.NOT_FOUND);
        }

        String filePath = fileUploadPath + File.separator + filename;
        File file = new File(filePath);
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
        HttpHeaders header = new HttpHeaders();
        String contentType = "application/octet-stream";
        String headerValue = "attachment; filename=\"" + resource.getFilename() + "\"";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .body(resource);
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
    
}