package uz.binart.trackmanagementsystem.service;

import org.springframework.data.util.Pair;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileStorageService {

    Pair<String, String> putFileInFolder(MultipartFile multipartFile, Long infoId) throws IOException;

}
