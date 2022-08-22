package uz.binart.trackmanagementsystem.controller;

import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.binart.trackmanagementsystem.model.FileInformation;
import uz.binart.trackmanagementsystem.model.User;
import uz.binart.trackmanagementsystem.service.FileService;
import uz.binart.trackmanagementsystem.service.UserService;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
    private final UserService userService;

    @PostMapping("/upload")
    public ResponseEntity<Long> uploadFile(@RequestParam(value = "file", required = false)MultipartFile file){


        User user = userService.getCurrentUserFromContext();

        Long savedInfoId = fileService.storeFile(file, user.getId());

        return ResponseEntity.ok(savedInfoId);
    }

    @GetMapping("/{file_id}")
    public ResponseEntity<Resource> viewFile2(@PathVariable(name = "file_id") Long fileId) throws IOException {
        Optional<FileInformation> fileInformationOpt = fileService.getInfoById(fileId);

        if(fileInformationOpt.isPresent() && !fileInformationOpt.get().getDeleted()) {
            FileInformation fileInformation = fileInformationOpt.get();
            return ResponseEntity.ok().contentType(MediaType.parseMediaType(fileInformation.getFileType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileInformation.getOriginalFileName() + "\"")
                    .body(new ByteArrayResource(
                            FileUtils.readFileToByteArray(new java.io.File(fileInformation.getFileNameWithPath()))
                    ));
        }

        else return ResponseEntity.badRequest().body(null);
    }

    @GetMapping("/info/{id}")
    public ResponseEntity<?> getInfoById(@PathVariable(name = "id")Long id){
        Optional<FileInformation> fileInformationOptional = fileService.getInfoById(id);
        if(fileInformationOptional.isPresent())
            return ResponseEntity.ok(fileInformationOptional.get());
        else return ResponseEntity.status(HttpStatus.NOT_FOUND).body("not found file");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable(name = "id")Long id){

        Optional<FileInformation> fileInformationOpt = fileService.getInfoById(id);
        User user = userService.getCurrentUserFromContext();
        if(fileInformationOpt.isPresent()){
            FileInformation fileInformation = fileInformationOpt.get();
            fileInformation.setDeleted(true);
            fileService.updateFileInformation(fileInformation, user.getId());
        }

        return ResponseEntity.ok().body("deleted successfully");
    }


}
