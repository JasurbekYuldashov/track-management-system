package uz.binart.trackmanagementsystem.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "files_information")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class FileInformation implements Serializable {

    @Transient
    static final String sequenceName = "files_information_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Long id;

    @Column(name = "original_file_name")
    private String originalFileName;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "path")
    private String path;

    @Column(name = "file_name_with_path")
    private String fileNameWithPath;

    @Column(name = "size")
    private Long size;

    @Column(name = "uploaded_at")
    private Date uploadedAt;

    @Column(name = "deleted")
    private Boolean deleted;

    @Column(name = "uploaded_by_id")
    private Long uploadedById;

}
