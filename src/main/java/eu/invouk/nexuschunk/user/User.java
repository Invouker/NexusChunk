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

    @Basic
    @Column(unique = true, nullable = false)
    private String username;

    @Basic
    @Column(unique = true, nullable = false)
    private String email;

    @Basic
    @Column(nullable = false)
    private String password;

    @Basic
    @Column(name = "minecraft_nick", unique = true)
    private String minecraftNick;

    @Basic
    @Column(name = "minecraft_uuid", unique = true)
    private String minecraftUuid;

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

    @Column(name = "oauth_provider")
    private String oauthProvider;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private VerificationToken verificationToken;

    @Column
    private LocalDateTime registrationDate;

    @Column()
    private LocalDateTime lastLogin;

    @Column()
    private LocalDateTime lastActivity = LocalDateTime.now();

    @Column(length = 2048)
    private String aboutMe;

    @Column(length = 50)
    private String discordName;

    @Column(length = 50)
    private String facebookName;

    @Column(length = 50)
    private String instagramName;

    @Column(length = 50)
    private String githubName;

}
