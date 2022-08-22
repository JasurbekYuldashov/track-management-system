package uz.binart.trackmanagementsystem.service;

import uz.binart.trackmanagementsystem.model.FileInformation;

import java.util.Optional;

public interface FileInformationService {

    Long storeInfo(String originalFilename, String fileType, Long size, Long uploadedById);

    Optional<FileInformation> getById(Long id);

    FileInformation update(FileInformation fileInformation, Long userId);

    FileInformation completeCreate(FileInformation fileInformation, Long userId);

    void deleteById(Long id, Long userId);

}
