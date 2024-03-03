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

    @RequestMapping(value = "/download/{folder}/{filename:.+}", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable("folder") String folder,
                                                            @PathVariable("filename") String filename) throws FileNotFoundException {

        // Construir la ruta completa del archivo utilizando la variable path
        String filePath = path + folder + "/" + filename;

        // Crear una instancia del archivo
        File file = new File(filePath);

        // Verificar si el archivo existe
        if (!file.exists()) {
            String errorMessage = "File Not Found: " + filename;
        }

        // Crear un InputStreamResource para el archivo
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        // Configurar el tipo de contenido y el encabezado de descarga
        String contentType = "application/octet-stream";
        String headerValue = "attachment; filename=\"" + file.getName() + "\"";

        // Devolver la respuesta con el archivo
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .body(resource);
    }



    // Get files
    @RequestMapping(value = "/getAllFiles", method = RequestMethod.GET)
    public String[] getFiles() {
        String folderPath = path;
        File directory = new File(folderPath);
        return directory.list();
    }

    // Get files in a specific directory
    @RequestMapping(value = "/getFiles", method = RequestMethod.GET)
    public String[] getFiles(@RequestParam("dir") String dir) {
        String folderPath = path + File.separator + dir;
        File directory = new File(folderPath);
        return directory.list();
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