package eu.invouk.nexuschunk.news.controller;

import eu.invouk.nexuschunk.app.settings.AppSettingsService;
import eu.invouk.nexuschunk.news.repositories.News;
import eu.invouk.nexuschunk.news.repositories.NewsRepository;
import eu.invouk.nexuschunk.news.services.NewsLikeService;
import eu.invouk.nexuschunk.services.UserService;
import eu.invouk.nexuschunk.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Controller
public class HomeController {

    private final NewsRepository newsRepository;
    private final UserService userService;
    private final NewsLikeService newsLikeService;
    private final AppSettingsService appSettingsService;

    public HomeController(NewsRepository newsRepository, UserService userService, NewsLikeService newsLikeService, AppSettingsService appSettingsService) {
        this.newsRepository = newsRepository;
        this.userService = userService;
        this.newsLikeService = newsLikeService;
        this.appSettingsService = appSettingsService;
    }

    @GetMapping("/")
    public String home(Model model, @RequestParam(defaultValue = "0") int page, Principal principal) {

        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Page<News> newsPage = newsRepository.findAll(PageRequest.of(page, 10, sort));
        model.addAttribute("newsPage", newsPage);

        // Logika pre zistenie stavu lajku
        Map<Long, Boolean> likedStatusMap = new HashMap<>();
        User currentUser = null;

        // KONTROLA 1: Ak je Principal dostupný (Používateľ je prihlásený)
        if (principal != null) {
            currentUser = userService.getUserByPrincipal(principal);
        }

        // KONTROLA 2: Kontrola stavu lajku pre každú novinku, IBA ak máme používateľa
        if (currentUser != null) {
            final User user = currentUser;
            newsPage.forEach(newsItem -> {
                boolean isLiked = newsLikeService.isLikedByUser(newsItem.getId(), user);
                likedStatusMap.put(newsItem.getId(), isLiked);
            });
        }

        // Predtým tu bol "else return "redirect:/";" - TENTO RIADOK SPÔSOBOVAL SLUČKU!

        // 3. Pridanie mapy stavov do modelu pre Thymeleaf
        model.addAttribute("likedStatus", likedStatusMap);

        model.addAttribute("isMaintenanceMode", appSettingsService.isMaintenanceMode());

        return "index";
    }

}
