package eu.invouk.nexuschunk.permissions;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "permission")
@Data
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;


    public Permission(String name) {
        this.name = name;
    }

    public Permission() {
    }
}
