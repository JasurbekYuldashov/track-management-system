package uz.binart.trackmanagementsystem.model.type;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "quantity_types")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class QuantityType implements Serializable {
    @Transient
    static final String sequenceName = "quantity_types_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Long id;

    private String name;

    private String lang;

}
