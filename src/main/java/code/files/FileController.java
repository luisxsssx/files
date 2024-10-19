package code.files;

import code.files.service.FolderService;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.Map;

@Controller
@RequestMapping("/home")
public class FileController {

    @Autowired
    private final FileService fileService;

    @Autowired
    private final FolderService folderService;

    @Value("${file.storage.bin}")
    private String binDir;

    @Value("${file.storage.location}")
    private String baseDir;

    private final ResourceLoader resourceLoader;

    public FileController(FileService fileService, FolderService folderService, ResourceLoader resourceLoader) {
        this.fileService = fileService;
        this.folderService = folderService;
        this.resourceLoader = resourceLoader;
    }

      /////////////////////////////////////
     ///      Folders Section          ///
    /////////////////////////////////////

    // Create folders
    @PostMapping("/folder/create")
    public ResponseEntity<Map<String, String>> createFolder(@RequestParam("folderName") String folderName,
                                                      @RequestParam(value = "parentFolder", required = false) String parentFolder) {
       return folderService.createFolder(folderName, parentFolder);
    }

    // Enter Folder
    @GetMapping("/folder")
    public ResponseEntity<List<Object>> folderContent(
            @RequestParam(value = "path") String path,
            @RequestParam(value = "type", required = false) String type) {
        List<Object> content = folderService.getFolderContent(baseDir, path, type);
        return ResponseEntity.ok(content);
    }

    @GetMapping("/all-content")
    public ResponseEntity<List<Object>> allContent(@RequestParam(value = "type") String type) {
        List<Object> content = folderService.allContent(type);
        return ResponseEntity.ok(content);
    }

      //////////////////////////////////
     ///      Files Section         ///
    //////////////////////////////////

    // Upload file to a specific folder
     @PostMapping("/upload")
     public ResponseEntity<Map<String, String>> uploadFileToFolder(@RequestParam("file") MultipartFile file,
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
    public ResponseEntity<Map<String, String>> delete(@RequestParam("path") String path){
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
        List<Object> content = folderService.getFolderContent(binDir, path, type);
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