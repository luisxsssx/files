package code.files;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;

@Controller
@RequestMapping("/home")
public class FileController {

    private String baseDir = "/home/luisxsssx/Documents/Code/documents";

    ///////////////////////////////////
    //      Folders Section          //
    ///////////////////////////////////

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
    @RequestMapping(value = "/folder", method = RequestMethod.GET)
    public ResponseEntity<Object[]> folderContent(@RequestParam (value = "path") String path) {
        String fullPath = path != null ? Paths.get(baseDir, path).toString() : baseDir;
        File folder = new File(fullPath);

        File[] folderContent = folder.listFiles();

        if (folderContent != null) {
            Object[] content = folderContent.length > 0 ?
                    Arrays.stream(folderContent)
                            .map(file -> {
                                if (file.isDirectory()) {
                                    return file.getName() + " (Directory)";
                                } else if (file.isFile()) {
                                    return file.getName() + " (File)";
                                } else {
                                    return file.getName() + " (Unknown)";
                                }
                            })
                            .toArray(Object[]::new)
                    : new Object[0];
            return ResponseEntity.ok(content);
        } else {
            return ResponseEntity.ok(new Object[0]);
        }
    }

    ///////////////////////////////////
    //      Files Section          //
    ///////////////////////////////////

    // Upload file to a specific folder
    @PostMapping("/uploadToFolder")
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
            FileOutputStream fout = new FileOutputStream(filePath);
            fout.write(file.getBytes());
            fout.close();
            return ResponseEntity.ok("File uploaded succesfully to folder " + folderName);
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
}