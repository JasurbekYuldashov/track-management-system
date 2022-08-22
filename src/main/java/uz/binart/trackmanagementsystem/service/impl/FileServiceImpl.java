package uz.binart.trackmanagementsystem.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uz.binart.trackmanagementsystem.exception.CustomInternalServerErrorException;
import uz.binart.trackmanagementsystem.model.FileInformation;
import uz.binart.trackmanagementsystem.service.FileInformationService;
import uz.binart.trackmanagementsystem.service.FileService;
import uz.binart.trackmanagementsystem.service.FileStorageService;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileStorageService fileStorageService;
    private final FileInformationService fileInformationService;

    public Long storeFile(MultipartFile file, Long postedById) {

        Long fileInformationId = fileInformationService.storeInfo(file.getOriginalFilename(), file.getContentType(), file.getSize(), postedById);

        Pair<String, String> pathAndNameWithPath;

        try {
            pathAndNameWithPath = fileStorageService.putFileInFolder(file, fileInformationId);
        }catch (IOException exception){
            exception.printStackTrace();
            throw new CustomInternalServerErrorException(exception.getMessage());
        }

        Optional<FileInformation> savedInfoOpt = fileInformationService.getById(fileInformationId);

        FileInformation fileInformation;
        if(savedInfoOpt.isPresent()){
            fileInformation = savedInfoOpt.get();
            fileInformation.setPath(pathAndNameWithPath.getFirst());
            fileInformation.setFileNameWithPath(pathAndNameWithPath.getSecond());
            fileInformation.setFileType(file.getContentType());
            fileInformationService.completeCreate(fileInformation, postedById);
        }else return null;

        return fileInformation.getId();
    }

    public Optional<FileInformation> getInfoById(Long id){
        return fileInformationService.getById(id);
    }

    public void updateFileInformation(FileInformation fileInformation, Long userId){
        
        fileInformationService.update(fileInformation, userId);
    }

    public void deleteById(Long fileId, Long userId){
        Optional<FileInformation> fileInformationOpt = getInfoById(fileId);

        if(fileInformationOpt.isPresent()){
            FileInformation fileInformation = fileInformationOpt.get();
            fileInformation.setDeleted(true);
            updateFileInformation(fileInformation, userId);
        }

    }

    public void updateFiles(Set<Long> newFilesKeySet, Set<Long> oldFilesKeySet, Long userId){

        for(Long oldFileId: oldFilesKeySet){
            if(!newFilesKeySet.contains(oldFileId))
                deleteById(oldFileId, userId);
        }
    }

}
