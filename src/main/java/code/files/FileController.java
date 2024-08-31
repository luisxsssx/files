package code.files;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.ResourceLoader;
import code.files.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Controller
@RequestMapping("/home")
public class FileController {

    @Autowired
    private final FileService fileService;

    private String baseDir = "/home/luisxsssx/Documents/Code/documents/root/";
    private String binDir = "/home/luisxsssx/Documents/Code/documents/bin/";
    private final ResourceLoader resourceLoader;

    public FileController(FileService fileService, ResourceLoader resourceLoader) {
        this.fileService = fileService;
        this.resourceLoader = resourceLoader;
    }

      /////////////////////////////////////
     ///      Folders Section          ///
    /////////////////////////////////////

    // Create folders
    @PostMapping("/folder/create")
    public ResponseEntity<String> createAnotherFolder(@RequestParam("folderName") String folderName,
                                                      @RequestParam(value = "parentFolder", required = false) String parentFolder) {
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

    // Enter Folder
    @GetMapping("/folder")
    public ResponseEntity<List<Object>> folderContent(
            @RequestParam(value = "path") String path,
            @RequestParam(value = "type", required = false) String type) {
        List<Object> content = fileService.getFolderContent(baseDir, path, type);
        return ResponseEntity.ok(content);
    }

      //////////////////////////////////
     //      Files Section          ///
    //////////////////////////////////

    // Upload file to a specific folder
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFileToFolder(@RequestParam("file") MultipartFile file,
                                                     @RequestParam(value = "folderName", required = false) String folderName) {
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

    // Rename a file or folder
    @PostMapping("/rename")
    public ResponseEntity<String> rename(@RequestParam("currentName") String currentName,
                                         @RequestParam("newName") String newName,
                                         @RequestParam(value = "folderName", required = false) String folderName) {
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

    // Delete file/folder
    @DeleteMapping("/delete")
    public ResponseEntity<String> delete(@RequestParam("path") String path){
        String targetPath = Paths.get(baseDir, path).toString();
        File targetFileFolder = new File(targetPath);

        if(!targetFileFolder.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File or folder not found");
        }

        if(fileService.deleted(targetFileFolder)) {
            return ResponseEntity.ok("Succesfully deleted");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting file or folder");
        }

    }

    // Move file to paper bin
    @PostMapping("/paper-bin")
    public ResponseEntity<String> paperbin(@RequestParam("name") String name) {
        Path source = Paths.get(baseDir + name);
        Path target = Paths.get(binDir + name);

        try {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
            return ResponseEntity.ok("File moved to paper bin successfully: " + name);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error moving file: " + e.getMessage());
        }
    }

    // Download file
    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> downloadFile(
            @RequestParam(value = "path") String path,
            @RequestParam(value = "type", required = false) String type) throws FileNotFoundException {

        String filePath = Paths.get(baseDir, path).toString();
        File file = new File(filePath);

        if (!file.exists()) {
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

    // View files contents
    @GetMapping("/content/{folderName}")
    public ResponseEntity<byte[]> getImageWithFolder(@PathVariable(value = "folderName", required = false) String folderName,
                                                     @RequestParam(value = "filename", required = true) String filename) {
        String filePath = Paths.get(baseDir, folderName != null ? folderName : "", filename).toString();

        return fileService.getFileResponse(filePath);
    }

    @GetMapping("/content")
    public ResponseEntity<byte[]> getImageWithoutFolder(@RequestParam("filename") String filename) {
        String filePath = Paths.get(baseDir, filename).toString();

        return fileService.getFileResponse(filePath);
    }


}