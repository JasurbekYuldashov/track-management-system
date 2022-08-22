package uz.binart.trackmanagementsystem.dto;

import lombok.Data;
import uz.binart.trackmanagementsystem.util.Stage;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class StageSequenceDto {
    private Long id;
    @NotNull
    @NotBlank
    private String name;
    @NotEmpty
    private List<Stage> sequence;
}
