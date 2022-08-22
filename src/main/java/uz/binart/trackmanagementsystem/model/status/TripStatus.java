package uz.binart.trackmanagementsystem.model.status;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "trip_statuses")
public class TripStatus {
    @Transient
    static final String sequenceName = "trip_statuses_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Long id;

    private String name;

    private String color;
}
