package uz.binart.trackmanagementsystem.service;


import org.springframework.web.multipart.MultipartFile;
import uz.binart.trackmanagementsystem.model.FileInformation;

import java.util.Optional;
import java.util.Set;

public interface FileService {

    Long storeFile(MultipartFile file, Long postedById);

    Optional<FileInformation> getInfoById(Long id);

    void updateFileInformation(FileInformation fileInformation, Long userId);

    void deleteById(Long fileId, Long userId);

    void updateFiles(Set<Long> newFilesKeySet, Set<Long> oldFilesKeySet, Long userId);

}
