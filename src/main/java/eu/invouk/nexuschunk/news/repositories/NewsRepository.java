package eu.invouk.nexuschunk.news.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface NewsRepository extends JpaRepository<News, Long> {

    long countByCreatedDateGreaterThanEqual(LocalDateTime thirtyDaysAgo);

}
