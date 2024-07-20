package code.files;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.util.Arrays;

@Controller
@RequestMapping("/home")
public class FileController {

    private String folderPath = "/home/luisxsssx/Documents/Code/spring/files/documents";

    ///////////////////////////////////
    //      Folders Section          //
    ///////////////////////////////////

    @PostMapping("/folder/create")
    public ResponseEntity<String> createFolder(@RequestParam("folderName") String folderName) {
        String baseDirPath = folderPath;
        String folderPath = Paths.get(baseDirPath, folderName).toString();

        File folder = new File(folderPath);
        if (folder.mkdirs()) {
            return ResponseEntity.ok("Succesfully created folder");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating folder");
        }
    }

    // Create folder whitin another folder
    @PostMapping("/folders")
    public ResponseEntity<String> createAnotherFolder(@RequestParam("folderName") String folderName,
                                                      @RequestParam("parentFolder") String parentFolder) {
        String baseDirPath = folderPath;
        String folderPath = Paths.get(baseDirPath, parentFolder, folderName).toString();

        File folder = new File(folderPath);
        if(folder.mkdir()) {
            return ResponseEntity.ok("Successfully created folder");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating folder");
        }
    }

    @RequestMapping(value = "/folder", method = RequestMethod.GET)
    public ResponseEntity<String[]> folderContent(@RequestParam(required = false) String name) {

        File folder = new File(folderPath);
        File[] folderContent = folder.listFiles();

        if (folderContent != null) {

            String[] filteredContent = folderContent.length > 0 ?
                    Arrays.stream(folderContent)
                            .filter(file -> name == null || file.getName().contains(name))
                            .map(File::getName)
                            .toArray(String[]::new)
                    : new String[0];

            return ResponseEntity.ok(filteredContent);
        } else {

            return ResponseEntity.ok(new String[0]);
        }
    }

    @RequestMapping(value = "/content", method = RequestMethod.GET)
    public ResponseEntity<String[]> getContent() {
        File folder = new File(folderPath);
        String[] files = folder.list();
        return ResponseEntity.ok(files);
    }

    ///////////////////////////////////
    //      Files Section          //
    ///////////////////////////////////

    @RequestMapping(value = "/uploadFiles", method = RequestMethod.POST)
    public String uploadFile(@RequestParam("file")MultipartFile file) {
        String path = folderPath + File.separator + file.getOriginalFilename();
        String fileUploadStatus;

        try {
            FileOutputStream fout = new FileOutputStream(path);
            fout.write(file.getBytes());

            fout.close();
            fileUploadStatus = "File Uploaded Succesfully";
        }
        catch (Exception e) {
            e.printStackTrace();
            fileUploadStatus = "Error in uploading file: " + e;
        }

        return fileUploadStatus;
    }

}