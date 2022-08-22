package uz.binart.trackmanagementsystem.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "expiration_notification")
public class ExpirationNotification {

    @Transient
    static final String sequenceName = "expiration_notification_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Long id;

    private String expirationFieldName;

    private String expirationEntityLink;

    private Boolean wasSeen = false;

    private Long expirationTime;

}
