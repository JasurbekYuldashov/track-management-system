package uz.binart.trackmanagementsystem.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
@Entity
@Table(name = "users")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User implements Serializable {
    @Transient
    static final String sequenceName = "users_id_seq";
    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Long id;
    @Column(name = "username")
    private String username;
    @JsonIgnore
    @Column(name = "password")
    private String password;
    @Column(name = "email")
    private String email;
    @Column(name = "phone_number")
    private String phone;
    @JsonIgnore
    @Column(name = "role_id")
    private Integer roleId;
    @Column(name = "account_created_date")
    private Date registeredAt;
    @Column(name = "additional_phone_number")
    private String additionalPhoneNumber;
    @Column(name = "odd_name")
    private String name;
    @Column(name = "attached_driver_id")
    private Long attachedDriverId;
    @Column(name = "deleted")
    private Boolean deleted = false;

    @Type(type = "jsonb")
    @Column(name = "visible_ids", columnDefinition = "jsonb")
    private List<Long> visibleIds = new ArrayList<>();

    @Type(type = "jsonb")
    @Column(name = "visible_team_ids", columnDefinition = "jsonb")
    private List<Long> visibleTeamIds = new ArrayList<>();

}