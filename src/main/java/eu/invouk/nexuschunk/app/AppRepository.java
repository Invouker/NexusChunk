package eu.invouk.nexuschunk.app;

import org.springframework.data.repository.CrudRepository;

public interface AppRepository extends CrudRepository<AppSettings, String> {
}
