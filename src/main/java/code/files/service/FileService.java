package code.files.service;

import code.files.model.fileModel;
import code.files.model.folderModel;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
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

    private String baseDir = "/home/luisxsssx/Documents/Code/documents/root/";

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

    public ResponseEntity<String> delete(String path) {
        String targetPath = Paths.get(baseDir, path).toString();
        File targetFileFolder = new File(targetPath);

        if(!targetFileFolder.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File or folder not found");
        }

        if(deleted(targetFileFolder)) {
            return ResponseEntity.ok("Successfully deleted");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting file or folder");
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

    public ResponseEntity<String> uploadFile(MultipartFile file, String folderName) {
        String folderPath;
        if(folderName == null || folderName.isEmpty()) {
            folderPath = baseDir;
        } else {
            folderPath = Paths.get(baseDir, folderName).toString();
            File folder = new File(folderPath);
            if(!folder.exists()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Folder does not exist");
            }
        }

        String filePath = Paths.get(folderPath, file.getOriginalFilename()).toString();
        try {
            // Save the file to the file system
            FileOutputStream fout = new FileOutputStream(filePath);
            fout.write(file.getBytes());
            fout.close();

            return ResponseEntity.ok("File uploaded succesfully to:" + folderName);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error in uploading file: " + e.getMessage());
        }
    }

    public ResponseEntity<String> createFolder(String folderName, String parentFolder){
        String folderPath = (parentFolder == null || parentFolder.isEmpty())
                ? Paths.get(baseDir, folderName).toString()
                : Paths.get(baseDir, parentFolder, folderName).toString();

        File folder = new File(folderPath);
        if (folder.mkdir()) {
            return ResponseEntity.ok("Successfully created folder");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating folder");
        }
    }
}