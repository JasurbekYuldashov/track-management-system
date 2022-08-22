package uz.binart.trackmanagementsystem.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "locations")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Location implements Serializable {

    @Transient
    static final String sequenceName = "locations_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Long id;

    private String name;

    private String ansi;

    private String parentAnsi;

    //timezones for status and regions from east to west, second is optional
    @Column(name = "first_time_zone")
    private Integer firstTimeZone;

    @Column(name = "second_time_zone")
    private Integer secondTimeZone;

    //parent_time_zone for cities first or second
    @Column(name = "parent_time_zone")
    private Integer parentTimeZone;

    @Column(name = "deleted")
    private Boolean deleted = false;

}
