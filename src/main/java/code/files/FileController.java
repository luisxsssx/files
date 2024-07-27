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

@Controller
@RequestMapping("/home")
public class FileController {

    private String baseDir = "/home/luisxsssx/Documents/Code/documents";

    ///////////////////////////////////
    //      Folders Section          //
    ///////////////////////////////////

    @PostMapping("/create")
    public ResponseEntity<String> createFolder(@RequestParam("folderName") String folderName) {
        String baseDirPath = baseDir;
        String folderPath = Paths.get(baseDirPath, folderName).toString();

        File folder = new File(folderPath);
        if (folder.mkdirs()) {
            return ResponseEntity.ok("Succesfully created folder");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating folder");
        }
    }
    
    @RequestMapping(value = "/folder", method = RequestMethod.GET)
    public ResponseEntity<String[]> folderContent(@RequestParam String path) {
        String fullPath = path != null ? Paths.get(baseDir, path).toString() : baseDir;
        File folder = new File(fullPath);

        File[] folderContent = folder.listFiles();

        if(folderContent != null) {
            String[] content = folderContent.length > 0 ?
                    Arrays.stream(folderContent)
                            .map(File::getName)
                            .toArray(String[]::new)
                    : new String[0];
            return ResponseEntity.ok(content);
        } else {
            return ResponseEntity.ok(new String[0]);
        }
    }

    // Create folder whitin another folder
    @PostMapping("/folder/create")
    public ResponseEntity<String> createAnotherFolder(@RequestParam("folderName") String folderName,
                                                      @RequestParam("parentFolder") String parentFolder) {
        String baseDirPath = baseDir;
        String folderPath = Paths.get(baseDirPath, parentFolder, folderName).toString();

        File folder = new File(folderPath);
        if(folder.mkdir()) {
            return ResponseEntity.ok("Successfully created folder");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating folder");
        }
    }


    @RequestMapping(value = "/content", method = RequestMethod.GET)
    public ResponseEntity<String[]> getContent() {
        File folder = new File(baseDir);
        String[] files = folder.list();
        return ResponseEntity.ok(files);
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


}