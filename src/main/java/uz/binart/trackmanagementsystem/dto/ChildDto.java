package uz.binart.trackmanagementsystem.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ChildDto {
    @NotNull
    private Long parentId;
    private String street;
    @NotNull
    private String city;
    @NotNull
    private Long stateProvinceId;
}
