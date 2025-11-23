package eu.invouk.nexuschunk.news.services;

import eu.invouk.nexuschunk.news.repositories.NewsRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class NewsService {

    private final NewsRepository newsRepository;

    public NewsService(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    /**
     * Získava celkový počet noviniek publikovaných za posledných 30 dní.
     *
     * @return Počet noviniek.
     */
    public long countNewsForLastDays(int days) {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(days);

        return newsRepository.countByCreatedDateGreaterThanEqual(thirtyDaysAgo);
    }
    public long countNews() {
        return newsRepository.count();
    }
}
