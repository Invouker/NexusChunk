package eu.invouk.nexuschunk.news.repositories;

import eu.invouk.nexuschunk.user.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "news_like")
@Data
public class NewsLike {

    @EmbeddedId
    private NewsLikeId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId") // Mapuje userId z NewsLikeId
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("newsId") // Mapuje newsId z NewsLikeId
    @JoinColumn(name = "news_id", nullable = false)
    private News news; // Predpokladáme, že máte entitu News

    @Column(nullable = false)
    private LocalDateTime likedAt = LocalDateTime.now();

    // Konštruktory a gettery/settery
    public NewsLike() {}

    public NewsLike(User user, News news) {
        this.user = user;
        this.news = news;
        // Inicializácia kompozitného kľúča
        this.id = new NewsLikeId(user.getId(), news.getId());
    }
}