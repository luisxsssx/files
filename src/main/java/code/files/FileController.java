package code.files;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.ResourceLoader;
import code.files.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Paths;
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
       return fileService.createFolder(folderName, parentFolder);
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
     ///      Files Section         ///
    //////////////////////////////////

    // Upload file to a specific folder
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFileToFolder(@RequestParam("file") MultipartFile file,
                                                     @RequestParam(value = "folderName", required = false) String folderName) {
        return fileService.uploadFile(file, folderName);
    }

    // Rename a file or folder
    @PostMapping("/rename")
    public ResponseEntity<String> rename(@RequestParam("currentName") String currentName,
                                         @RequestParam("newName") String newName,
                                         @RequestParam(value = "folderName", required = false) String folderName) {
        return fileService.rename(currentName, newName, folderName);
    }

    // Delete file/folder
    @DeleteMapping("/delete")
    public ResponseEntity<String> delete(@RequestParam("path") String path){
       return fileService.delete(path);
    }

    // Move file to paper bin
    @PostMapping("/paper-bin/{folderName}")
    public ResponseEntity<String> moveFileToPaperBin(
            @PathVariable(value = "folderName", required = false) String folderName,
            @RequestParam("filename") String filename) {

        String filePath = Paths.get(folderName != null ? folderName : "", filename).toString();
        return fileService.moveToPaperBin(filePath);
    }

    @GetMapping("/paper-bin")
    public ResponseEntity<List<Object>> paperBin(
            @RequestParam(value = "path") String path,
            @RequestParam(value = "type", required = false) String type) {
        List<Object> content = fileService.getFolderContent(binDir, path, type);
        return ResponseEntity.ok(content);
    }

    // Download file
    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> downloadFile(
            @RequestParam(value = "path") String path,
            @RequestParam(value = "type", required = false) String type) throws FileNotFoundException {

       return fileService.downloadFile(path, type);
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