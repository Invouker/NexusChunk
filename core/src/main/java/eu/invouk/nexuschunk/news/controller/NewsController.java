package eu.invouk.nexuschunk.news.controller;

import eu.invouk.nexuschunk.news.dtos.NewsDto;
import eu.invouk.nexuschunk.news.repositories.News;
import eu.invouk.nexuschunk.news.repositories.NewsRepository;
import eu.invouk.nexuschunk.news.services.NewsLikeService;
import eu.invouk.nexuschunk.services.FileStorageService;
import eu.invouk.nexuschunk.services.UserService;
import eu.invouk.nexuschunk.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Controller
public class NewsController {

    private final NewsRepository newsRepository;
    private final UserService userService;
    private final FileStorageService fileStorageService;
    private final NewsLikeService newsLikeService;

    public NewsController(NewsRepository newsRepository, UserService userService, FileStorageService fileStorageService, NewsLikeService newsLikeService) {
        this.newsRepository = newsRepository;
        this.userService = userService;
        this.fileStorageService = fileStorageService;
        this.newsLikeService = newsLikeService;
    }

    @GetMapping("/admin/news")
    public String news(Model model){
        List<News> allNews = newsRepository.findAll();
        model.addAttribute("newsList", allNews);
        return "admin/news";
    }

    @GetMapping("/admin/news/create")
    public String createNews(Model model){
        model.addAttribute("news", new NewsDto());
        return "admin/create_news";
    }

    @GetMapping("/admin/news/edit/{id}")
    @PreAuthorize("hasAuthority(@permissions.VIEW_NEWS)")
    public String createNews(Model model, @PathVariable("id") Long id){

        News news = newsRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Neplatný ID novinky: " + id));

        model.addAttribute("news", news);
        return "admin/create_news";
    }

    @PostMapping("/admin/news/save")
    @PreAuthorize("hasAnyAuthority(@permissions.CREATE_NEWS, @permissions.VIEW_NEWS)")
    public String createNews(NewsDto newsDto, Principal principal) {
        boolean isNew = newsDto.getId() == null;
        News news;
        if(isNew) {
            User user = userService.getUserByPrincipal(principal);
            if(user == null)
                return "redirect:/";

            String userName = user.getUsername();
            news = new News();

            news.setAuthor(userName);

            log.info("News created: {}", news);
        } else {
            news = newsRepository.findById(newsDto.getId()).orElseThrow(() -> new IllegalArgumentException("Neplatný ID novinky: " + newsDto.getId()));

            log.info("Updating existing News item with ID: {}", newsDto.getId());
        }

        news.setTitle(newsDto.getTitle());
        news.setCreatedDate(LocalDateTime.now());
        news.setPublished(newsDto.isPublished());
        news.setContent(newsDto.getContent());

        MultipartFile imageFile = newsDto.getImage();

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String filename = fileStorageService.storeFile(imageFile);
                news.setImage(filename);

            } catch (IOException e) {
                log.error("Chyba pri ukladaní obrázku novinky: " + news.getTitle(), e);
                return "redirect:/admin/news/create?fileError";
            }
        }

        newsRepository.save(news);
        String message = isNew ? "created" : "updated";
        log.info("News successfully {}: {}", message, news.getId());
        return "redirect:/admin/news"; // return to news  page
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/news/like/{newsId}")
    public String toggleLike(@PathVariable Long newsId, Principal principal)  {
        if(principal == null) {
            log.warn("Principal is null and must be authenticated! He tried to like this post with id: {}", newsId);
            return "redirect:/";
        }
        User user = userService.getUserByPrincipal(principal);
        if(user == null) {
            log.warn("Authenticated Principal {} not found in database.", principal.getName());
            return "redirect:/";
        }

        newsLikeService.toggleLike(newsId, user);
        log.info("User {} toggled like for news with ID: {}", user.getUsername(), newsId);
        return "redirect:/";
    }

}
