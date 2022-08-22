package uz.binart.trackmanagementsystem.dto;

import lombok.Data;

@Data
public class ActionListDto {

    private String username;

    private String tableName;

    private String actionType;

    private Long timeStamp;

    private Long entityId;
}
