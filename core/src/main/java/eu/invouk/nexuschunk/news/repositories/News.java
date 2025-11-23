package eu.invouk.nexuschunk.news.repositories;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table
@Data
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String title;

    @Column(length = 256)
    private String hashtags;

    @Column(length = 30)
    private String author;

    @Column
    private boolean published;

    @Lob
    private String image;

    @Column
    private LocalDateTime createdDate;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column
    private int likes = 0;

    @Column
    private int views = 0;

}
