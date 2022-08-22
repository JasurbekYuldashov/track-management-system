package uz.binart.trackmanagementsystem.model.type;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "ownership_types")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class OwnershipType implements Serializable {
    @Transient
    static final String sequenceName = "ownership_types_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "abbreviation")
    private String abbreviation;
}
