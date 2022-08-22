package uz.binart.trackmanagementsystem.dto;

import lombok.Data;

@Data
public class TruckNoteDto {
    private Long id;
    private String content;
    private String author;
    private Long postedDate;
}
