package uz.binart.trackmanagementsystem.util;

import lombok.Data;

import java.io.Serializable;

@Data
public class Stage implements Serializable {
    private String name;
    private Integer stage = 1;
}
