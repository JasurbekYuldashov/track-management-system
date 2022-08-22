package uz.binart.trackmanagementsystem.dto;

import lombok.Data;

@Data
public class ActionsDto {

    private Long userId;

    private String tableName;

    private Long actionType;

}
