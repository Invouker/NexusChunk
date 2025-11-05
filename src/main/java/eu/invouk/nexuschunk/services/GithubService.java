package eu.invouk.nexuschunk.services;

import eu.invouk.nexuschunk.CommitDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class GithubService {

    private final RestTemplate restTemplate;

    @Value("${github.api.url}")
    private String apiUrlTemplate;

    @Value("${github.api.token}")
    private String githubToken;

    @Value("${github.repo.owner}")
    private String owner;

    @Value("${github.repo.name}")
    private String repo;

    @Value("${github.branch.name}")
    private String branch;


    public GithubService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Cacheable("githubCommits")
    public List<CommitDto> getCommits() {
        System.out.println("--- FETCHING COMMITS FROM GITHUB API ---"); // Logovanie pre overenie cache

        try {
            String url = apiUrlTemplate
                    .replace("{owner}", owner)
                    .replace("{repo}", repo)
                    .replace("{branch}", branch);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept", "application/vnd.github.v3+json");

            // Podmienené pridanie tokenu pre vyšší rate limit
            if (githubToken != null && !githubToken.isEmpty()) {
                headers.set("Authorization", "token " + githubToken);
            }

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<CommitDto[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    CommitDto[].class
            );

            if (response.getBody() != null) {
                return Arrays.asList(response.getBody());
            }

        } catch (Exception e) {
            System.err.println("Chyba pri načítavaní commitov z GitHub: " + e.getMessage());
        }
        return Collections.emptyList();
    }
}