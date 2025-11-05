package eu.invouk.nexuschunk.news.controller;

import eu.invouk.nexuschunk.news.repositories.News;
import eu.invouk.nexuschunk.news.repositories.NewsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    private NewsRepository newsRepository;

    public HomeController(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    @GetMapping("/")
    public String home(Model model, @RequestParam(defaultValue = "0") int page) {

        Page<News> news = newsRepository.findAll(PageRequest.of(page, 10));
        model.addAttribute("newsPage", news);

        return "index";
    }

}
