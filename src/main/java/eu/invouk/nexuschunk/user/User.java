package eu.invouk.nexuschunk.user;

import eu.invouk.nexuschunk.user.permissions.Role;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "user")
@Data
public class User {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /*@Column(unique = true, nullable = false)
    private String name;*/
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;

    @Column(name = "minecraft_nick", unique = true, nullable = false)
    private String minecraftNick;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(
                    name = "user_id",
                    foreignKey = @ForeignKey(name = "FK_USER_ROLES_USER_ID")
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id",
                    foreignKey = @ForeignKey(name = "FK_USER_ROLES_ROLE_ID")
            )
    )
    private Set<Role> roles;

    private boolean enabled = false;

    @Column(name = "minecraft_uuid", unique = true)
    private String minecraftUuid;

    @Column(name = "oauth_provider")
    private String oauthProvider;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private VerificationToken verificationToken;

    @Column
    private LocalDateTime registrationDate;

    @Column
    private LocalDateTime lastLogin;

    @Column
    private LocalDateTime lastActivity;

}
