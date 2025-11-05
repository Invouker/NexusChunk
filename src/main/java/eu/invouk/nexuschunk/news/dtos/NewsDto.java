package eu.invouk.nexuschunk.news.dtos;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class NewsDto {

    private Long id;

    private String title;
    private String hashtags;
    private MultipartFile image;
    private String content;
    private boolean published;

}
