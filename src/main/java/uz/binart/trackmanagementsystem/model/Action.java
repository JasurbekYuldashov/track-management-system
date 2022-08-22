package uz.binart.trackmanagementsystem.model;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "actions")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class Action implements Serializable {

    @Transient
    static final String sequenceName = "actions_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Long id;
    @Column(name = "action_type_id")
    private Long actionTypeId;
    @Column(name = "made_by_id")
    private Long madeById;
    @Column(name = "action_time")
    private Date actionTime;
    @Type(type = "jsonb")
    @Column(name = "initial_object", columnDefinition = "jsonb")
    private Object initialObject;
    @Type(type = "jsonb")
    @Column(name = "result_object", columnDefinition = "jsonb")
    private Object resultObject;
    @Column(name = "table_name")
    private String tableName;
    @Column(name = "deleted")
    private Boolean deleted = false;
    @Column(name = "entity_id")
    private Long entityId;
}
