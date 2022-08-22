package uz.binart.trackmanagementsystem.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uz.binart.trackmanagementsystem.service.FileStorageService;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;


@Service
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService{

    private final String rootFolder = "uploads";

    //first string is path, second is filename with path
    public Pair<String, String> putFileInFolder(MultipartFile multipartFile, Long infoId) throws IOException{

        String currentDateAsFolder = getCurrentDateAsFolderName();

        String extension = Objects.requireNonNull(multipartFile.getContentType()).split("/")[1];

        String path = rootFolder + "/" + currentDateAsFolder + "/";
        String fileNameWithPath = rootFolder + "/" + currentDateAsFolder + "/" + multipartFile.getOriginalFilename() + infoId + "." + extension;
        File file = new File(fileNameWithPath);

        FileUtils.writeByteArrayToFile(file, multipartFile.getBytes());

        return Pair.of(path, fileNameWithPath);
    }

    private String getCurrentDateAsFolderName(){
        Calendar calendar = new GregorianCalendar();
        Date date = new Date();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH) + "_" + calendar.get(Calendar.MONTH) + "_" + calendar.get(Calendar.YEAR);
    }

}
