package uz.binart.trackmanagementsystem.dto;

import lombok.Data;

@Data
public class NewNoteDto {
    private String content;
    private Long truckId;
}
