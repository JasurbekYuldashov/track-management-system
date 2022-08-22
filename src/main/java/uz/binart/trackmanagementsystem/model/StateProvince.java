package uz.binart.trackmanagementsystem.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "state_province")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class StateProvince implements Serializable {

    @Transient
    static final String sequenceName = "state_province_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Long id;

    private String name;

    private String lang;

    private String code;

    @Column(name = "deleted")
    private Boolean deleted = false;

}
