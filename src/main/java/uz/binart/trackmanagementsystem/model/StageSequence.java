package uz.binart.trackmanagementsystem.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import uz.binart.trackmanagementsystem.util.Stage;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "stage_sequences")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class StageSequence implements Serializable {
    @Transient
    static final String sequenceName = "stage_sequences_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Long id;

    private String name;

    @Type(type = "jsonb")
    @Column(name = "stages", columnDefinition = "jsonb")
    private List<Stage> sequence;

    @Column(name = "deleted")
    private Boolean deleted = false;

}
