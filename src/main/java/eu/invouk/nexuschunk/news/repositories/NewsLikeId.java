package eu.invouk.nexuschunk.news.repositories;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Embeddable
@Data
public class NewsLikeId implements Serializable {

    private Long userId;
    private Long newsId;

    public NewsLikeId() {}

    public NewsLikeId(Long userId, Long newsId) {
        this.userId = userId;
        this.newsId = newsId;
    }
}