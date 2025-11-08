package eu.invouk.nexuschunk.admin.github;

import eu.invouk.nexuschunk.admin.github.dtos.CommitDisplayDto;
import eu.invouk.nexuschunk.admin.github.dtos.CommitDto;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CommitMapper {

    private static final Logger log = LoggerFactory.getLogger(CommitMapper.class);
    private final DateTimeFormatter targetFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private final ZoneId targetZone = ZoneId.systemDefault();

    /**
     * Konvertuje zoznam surových CommitDto na DTO pre zobrazenie s formátovaným dátumom.
     */
    public List<CommitDisplayDto> mapToDisplayDtos(List<CommitDto> rawCommits) {
        return rawCommits.stream()
                .map(this::mapToDisplayDto) // Použijeme metódu pre konverziu jedného objektu
                .collect(Collectors.toList());
    }

    /**
     * Vykonáva samotnú logiku formátovania dátumu.
     */
    private CommitDisplayDto mapToDisplayDto(CommitDto raw) {
        String formattedDate;
        String rawDate = raw.commitDetails().committer().date();

        if (rawDate != null) {
            try {
                // Konverzia na základe ISO 8601 a formátovanie
                Instant instant = Instant.parse(rawDate);
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
    }

}
