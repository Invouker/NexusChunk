package eu.invouk.nexuschunk.news.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NewsRepository extends JpaRepository<News, Long> {


}
