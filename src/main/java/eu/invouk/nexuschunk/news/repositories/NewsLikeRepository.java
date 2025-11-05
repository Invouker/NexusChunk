package eu.invouk.nexuschunk.news.repositories;

import eu.invouk.nexuschunk.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NewsLikeRepository extends JpaRepository<NewsLike, NewsLikeId> {

    Optional<NewsLike> findByUserAndNews(User user, News news);
}