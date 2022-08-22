package uz.binart.trackmanagementsystem.model.status;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "unit_statuses")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class UnitStatus implements Serializable {

    @Transient
    static final String sequenceName = "unit_statuses_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Long id;

    private String name;

    private String color;
}
