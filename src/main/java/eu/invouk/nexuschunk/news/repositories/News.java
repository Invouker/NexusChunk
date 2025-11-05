package eu.invouk.nexuschunk.news.repositories;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

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

    @Lob
    private byte[] image;

    @ManyToMany
    @JoinTable(
            name = "news_category",
            joinColumns = @JoinColumn(name = "news_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories;

    @Column
    private LocalDateTime createdDate;

    @Column(columnDefinition = "TEXT")
    private String content;

}
