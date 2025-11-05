package eu.invouk.nexuschunk.admin;

import eu.invouk.nexuschunk.CommitDisplayDto;
import eu.invouk.nexuschunk.CommitDto;
import eu.invouk.nexuschunk.services.GithubService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@Slf4j
public class AdminController {

    private final GithubService githubService;

    public AdminController(GithubService githubService) {
        this.githubService = githubService;
    }


    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model){

        // 1. Získanie surových commitov
        List<CommitDto> rawCommits = githubService.getCommits().stream().limit(10).toList();

        // --- LOGIKA FORMÁTOVANIA DÁTUMU ---
        DateTimeFormatter targetFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        // Nastavte časovú zónu, v ktorej sa má dátum zobraziť (napr. stredoeurópsky čas)
        ZoneId targetZone = ZoneId.systemDefault();

        // 2. Konverzia surových DTO na DTO pre zobrazenie
        List<CommitDisplayDto> displayCommits = rawCommits.stream()
                .map(raw -> {
                    String formattedDate;
                    String rawDate = raw.commitDetails().committer().date(); // Prístup cez record metódy

                    if (rawDate != null) {
                        try {
                            // Parsuje ISO 8601 reťazec (napr. '2025-11-05T21:36:04Z')
                            Instant instant = Instant.parse(rawDate);
                            // Konvertuje na ZonedDateTime v cieľovej zóne (napr. pre +01:00)
                            ZonedDateTime zdt = instant.atZone(targetZone);
                            formattedDate = zdt.format(targetFormatter);
                        } catch (Exception e) {
                            log.error("Chyba parsovania dátumu z Gitu: {}", rawDate, e);
                            formattedDate = "Chybný formát";
                        }
                    } else {
                        formattedDate = "Dátum neznámy";
                    }

                    // Vytvorenie DTO pre Thymeleaf
                    return new CommitDisplayDto(
                            raw.sha(),
                            raw.commitDetails().message(),
                            raw.commitDetails().committer().name(),
                            formattedDate
                    );
                })
                .collect(Collectors.toList());

        log.warn("Formátované Commits: {}", displayCommits);

        // 3. Pridanie SPRACVANÝCH dát do modelu
        model.addAttribute("commits", displayCommits);

        return "admin/index";
    }


    @GetMapping("/admin/members")
    public String members(){
        return "admin/members";
    }
    @GetMapping("/admin/permission")
    public String permission(){
        return "admin/permission";
    }

    @GetMapping("/admin/settings")
    public String settings(){
        return "admin/settings";
    }
    @GetMapping("/admin/server")
    public String server(){
        return "admin/server";
    }

    @GetMapping("/admin/edit_member")
    public String editMember(){
        return "admin/edit_member";
    }

}
