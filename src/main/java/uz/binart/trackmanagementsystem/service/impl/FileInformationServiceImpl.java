package uz.binart.trackmanagementsystem.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.binart.trackmanagementsystem.exception.FileStorageException;
import uz.binart.trackmanagementsystem.model.FileInformation;
import uz.binart.trackmanagementsystem.repository.FileInformationRepository;
import uz.binart.trackmanagementsystem.service.ActionService;
import uz.binart.trackmanagementsystem.service.FileInformationService;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FileInformationServiceImpl implements FileInformationService {

    private final FileInformationRepository fileInformationRepository;
    private final ActionService actionService;

    public Long storeInfo(String originalFilename, String fileType, Long size, Long uploadedById){

        if(originalFilename.contains("..")) {
            throw new FileStorageException("Filename contains invalid path sequence " + originalFilename);
        }

        FileInformation fileInformation = new FileInformation();

        fileInformation.setOriginalFileName(originalFilename);
        fileInformation.setDeleted(false);
        fileInformation.setFileType(fileType);
        fileInformation.setSize(size);
        fileInformation.setUploadedAt(new Date());
        fileInformation.setUploadedById(uploadedById);
        FileInformation savedInfo = fileInformationRepository.save(fileInformation);

        return savedInfo.getId();
    }

    public Optional<FileInformation> getById(Long id){
        return fileInformationRepository.findByIdAndDeletedFalse(id);
    }


    public FileInformation update(FileInformation fileInformation, Long userId){
        FileInformation oldInfo = fileInformationRepository.getOne(fileInformation.getId());
        actionService.captureUpdate(oldInfo, fileInformation, "files_information", userId);
        return fileInformationRepository.save(fileInformation);
    }

    public FileInformation completeCreate(FileInformation fileInformation, Long userId){
        FileInformation savedInfo = fileInformationRepository.save(fileInformation);
        actionService.captureCreate(savedInfo, "files_information", userId);
        return savedInfo;
    }


    public void deleteById(Long id, Long userId){
        Optional<FileInformation> fileOpt = getById(id);
        if(fileOpt.isPresent()){
            FileInformation fileInformation = fileOpt.get();
            fileInformation.setDeleted(true);
            update(fileInformation, userId);
        }
    }

}
