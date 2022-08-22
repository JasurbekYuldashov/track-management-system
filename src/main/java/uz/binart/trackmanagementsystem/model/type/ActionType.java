package uz.binart.trackmanagementsystem.model.type;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "action_types")
public class ActionType {
    @Transient
    static final String sequenceName = "action_types_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Long id;

    private String name;
    @Column(name = "language_tag")
    private String tag;

}
