package eu.invouk.nexuschunk.news.services;

import eu.invouk.nexuschunk.news.repositories.News;
import eu.invouk.nexuschunk.news.repositories.NewsLike;
import eu.invouk.nexuschunk.news.repositories.NewsLikeRepository;
import eu.invouk.nexuschunk.news.repositories.NewsRepository;
import eu.invouk.nexuschunk.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class NewsLikeService {

    private final NewsLikeRepository newsLikeRepository;
    private final NewsRepository newsRepository;

    public NewsLikeService(NewsLikeRepository newsLikeRepository, NewsRepository newsRepository) {
        this.newsLikeRepository = newsLikeRepository;
        this.newsRepository = newsRepository;
    }

    /**
     * Prepína stav lajku pre novinku a daného používateľa.
     *
     * @param newsId ID novinky.
     * @param user   Prihlásený používateľ.
     */
    @Transactional
    public void toggleLike(Long newsId, User user) {

        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new IllegalArgumentException("Novinka nenájdená s ID: " + newsId));

        Optional<NewsLike> existingLike = newsLikeRepository.findByUserAndNews(user, news);

        if (existingLike.isPresent()) {
            // UNLIKE (Odstránenie)
            newsLikeRepository.delete(existingLike.get());

            // Znížime počítadlo priamo v entite News
            news.setLikes(news.getLikes() - 1);
            newsRepository.save(news);
        } else {
            // LIKE (Pridanie)
            NewsLike newLike = new NewsLike(user, news);
            newsLikeRepository.save(newLike);

            // Zvýšime počítadlo priamo v entite News
            news.setLikes(news.getLikes() + 1);
            newsRepository.save(news);
        }
    }

    public boolean isLikedByUser(Long newsId, User user) {
        // 1. Nájdeme novinku (optimálne by bolo načítať len News)
        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new IllegalArgumentException("Novinka nenájdená s ID: " + newsId));

        // 2. Kontrolujeme, či existuje záznam Like
        return newsLikeRepository.findByUserAndNews(user, news).isPresent();
    }

    public long countLikesForLastDays(long days) {
        // 1. Vypočíta LocalDateTime X dní dozadu
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(days);

        // 2. Zavolá Repository, ktorá vykoná COUNT s podmienkou WHERE likedAt >= dátum
        return newsLikeRepository.countByLikedAtGreaterThanEqual(thirtyDaysAgo);
    }
}