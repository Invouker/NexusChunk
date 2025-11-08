package eu.invouk.nexuschunk.admin;

import eu.invouk.nexuschunk.admin.github.CommitMapper;
import eu.invouk.nexuschunk.admin.github.dtos.CommitDisplayDto;
import eu.invouk.nexuschunk.admin.github.dtos.CommitDto;
import eu.invouk.nexuschunk.news.repositories.NewsRepository;
import eu.invouk.nexuschunk.news.services.NewsLikeService;
import eu.invouk.nexuschunk.news.services.NewsService;
import eu.invouk.nexuschunk.services.GithubService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@Slf4j
public class AdminController {

    private final GithubService githubService;
    private final CommitMapper commitMapper;
    private final NewsService newsService;
    private final NewsLikeService newsLikeService;

    public AdminController(GithubService githubService, CommitMapper commitMapper, NewsService newsService, NewsLikeService newsLikeService) {
        this.githubService = githubService;
        this.commitMapper = commitMapper;
        this.newsService = newsService;
        this.newsLikeService = newsLikeService;
    }


    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model){

        //Commits
        List<CommitDto> rawCommits = githubService.getCommits().stream().limit(10).toList();
        List<CommitDisplayDto> displayCommits = commitMapper.mapToDisplayDtos(rawCommits);
        model.addAttribute("commits", displayCommits);

        //News
        model.addAttribute("countNewsForLastDays", newsService.countNewsForLastDays(30));
        model.addAttribute("countNews", newsService.countNews());

        //News Like
        model.addAttribute("countNewsLikeLastDays", newsLikeService.countLikesForLastDays(30));

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
