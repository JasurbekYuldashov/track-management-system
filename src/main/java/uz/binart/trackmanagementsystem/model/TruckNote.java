package uz.binart.trackmanagementsystem.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "truck_notes")
public class TruckNote {

    @Transient
    static final String sequenceName = "truck_notes_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Long id;

    private String content;

    @Column(name = "posted_date")
    private Long postedDate;

    @Column(name = "truck_id")
    private Long truckId;

    @Column(name = "author_id")
    private Long authorId;

}
