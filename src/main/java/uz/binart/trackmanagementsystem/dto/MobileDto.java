package uz.binart.trackmanagementsystem.dto;

import lombok.Data;

import java.util.Date;

@Data
public class MobileDto {
    private String errorMessage = "";
    private Long timeStamp = new Date().getTime();
    private Object data;
}
