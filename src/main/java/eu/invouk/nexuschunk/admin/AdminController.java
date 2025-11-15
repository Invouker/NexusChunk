package eu.invouk.nexuschunk.admin;

import eu.invouk.nexuschunk.Utils;
import eu.invouk.nexuschunk.admin.dtos.UserDto;
import eu.invouk.nexuschunk.admin.github.CommitMapper;
import eu.invouk.nexuschunk.admin.github.dtos.CommitDisplayDto;
import eu.invouk.nexuschunk.admin.github.dtos.CommitDto;
import eu.invouk.nexuschunk.news.services.NewsLikeService;
import eu.invouk.nexuschunk.news.services.NewsService;
import eu.invouk.nexuschunk.services.GithubService;
import eu.invouk.nexuschunk.user.User;
import eu.invouk.nexuschunk.user.permissions.Role;
import eu.invouk.nexuschunk.user.permissions.repositories.RoleRepository;
import eu.invouk.nexuschunk.user.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@Slf4j
public class AdminController {

    private final GithubService githubService;
    private final CommitMapper commitMapper;
    private final NewsService newsService;
    private final NewsLikeService newsLikeService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public AdminController(GithubService githubService, CommitMapper commitMapper, NewsService newsService, NewsLikeService newsLikeService, UserRepository userRepository, RoleRepository roleRepository) {
        this.githubService = githubService;
        this.commitMapper = commitMapper;
        this.newsService = newsService;
        this.newsLikeService = newsLikeService;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @GetMapping("/admin/dashboard")
    @PreAuthorize("hasAuthority(@permissions.VIEW_ADMIN_DASHBOARD)")
    public String adminDashboard(Model model, Authentication authentication){

        //Commits
        List<CommitDto> rawCommits = githubService.getCommits().stream().limit(10).toList();
        List<CommitDisplayDto> displayCommits = commitMapper.mapToDisplayDtos(rawCommits);
        model.addAttribute("commits", displayCommits);

        //News
        model.addAttribute("countNewsForLastDays", newsService.countNewsForLastDays(30));
        model.addAttribute("countNews", newsService.countNews());

        //News Like
        model.addAttribute("countNewsLikeLastDays", newsLikeService.countLikesForLastDays(30));

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        // 2. (Voliteľné) Prevedenie na List<String> pre jednoduchšiu prácu
        List<String> permissions = authorities.stream()
                .map(GrantedAuthority::getAuthority) // Extrahujeme String názov povolenia
                .toList(); // Použijeme .toList()
        log.info(permissions.toString());

        return "admin/index";
    }
    @GetMapping("/admin/members")
    @PreAuthorize("hasAuthority(@permissions.VIEW_MEMBERS)")
    public String members(Model model, @RequestParam(defaultValue = "0") int page) {

        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Page<User> userPage = userRepository.findAll(PageRequest.of(page, 30, sort));
        model.addAttribute("userPage", userPage);

        return "admin/members";
    }

    @GetMapping("/admin/member/edit/{id}")
    @PreAuthorize("hasAuthority(@permissions.EDIT_MEMBER)")
    public String editMember(Model model, @PathVariable String id) {
        Optional<User> userOptional = userRepository.findById(Long.parseLong(id));

        if (userOptional.isEmpty()) {
            return "redirect:/admin/members";
        }

        List<Role> allRoles = roleRepository.findAll();

        User user = userOptional.get();
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUserName(user.getUsername());
        userDto.setEmail(user.getEmail());
        userDto.setMinecraftNick(user.getMinecraftNick());
        userDto.setUuid(user.getMinecraftUuid());
        userDto.setAboutMe(user.getAboutMe());
        userDto.setGithub(user.getGithubName());
        userDto.setDiscord(user.getDiscordName());
        userDto.setFacebook(user.getFacebookName());
        userDto.setInstagram(user.getInstagramName());
        userDto.setRoles(
                user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet())
        );
        userDto.setEnabled(user.isEnabled());
        model.addAttribute("user", userDto);
        model.addAttribute("allRoles", allRoles);
        return "admin/edit_member";
    }

    @PostMapping("/admin/member/edit/{id}")
    @PreAuthorize("hasAuthority(@permissions.EDIT_MEMBER)")
    public String editMember(@PathVariable String id, UserDto userDto) {
        Optional<User> userOptional = userRepository.findById(Long.parseLong(id));
        if (userOptional.isEmpty() || userDto == null) {
            return "redirect:/admin/members";
        }

        User user = userOptional.get();
        //user.setUsername(userDto.getUserName());
        //user.setEmail(Utils.emptyToNull(userDto.getEmail()));
        user.setMinecraftNick(Utils.emptyToNull(userDto.getMinecraftNick()));
        user.setMinecraftUuid(Utils.emptyToNull(userDto.getUuid()));
        user.setAboutMe(Utils.emptyToNull(userDto.getAboutMe()));
        user.setGithubName(Utils.emptyToNull(userDto.getGithub()));
        user.setDiscordName(Utils.emptyToNull(userDto.getDiscord()));
        user.setFacebookName(Utils.emptyToNull(userDto.getFacebook()));
        user.setInstagramName(Utils.emptyToNull(userDto.getInstagram()));
        user.setEnabled(userDto.isEnabled());
        Set<Role> newRoles = userDto.getRoles().stream()
                .map(roleRepository::findByName)   // Optional<Role>
                .flatMap(Optional::stream)         // Optional → Role
                .collect(Collectors.toSet());

        user.setRoles(newRoles);
        userRepository.save(user);
        return "redirect:/admin/members";
    }

    @GetMapping("/admin/settings")
    @PreAuthorize("hasAuthority(@permissions.VIEW_SETTINGS)")
    public String settings(){
        return "admin/settings";
    }
    @GetMapping("/admin/server")
    @PreAuthorize("hasAuthority(@permissions.VIEW_SERVER)")
    public String server(){
        return "admin/server";
    }

}
