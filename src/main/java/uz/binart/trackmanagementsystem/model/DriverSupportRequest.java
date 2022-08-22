package uz.binart.trackmanagementsystem.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "driver_support_requests")
public class DriverSupportRequest {

    @Transient
    static final String sequenceName = "driver_support_requests_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Long id;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "message")
    private String message;

    @Column(name = "driver_id")
    private Long driverId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "watched")
    private Boolean watched = false;

}
