package eu.invouk.nexuschunk.auth.controller;

import eu.invouk.nexuschunk.auth.model.EmailDto;
import eu.invouk.nexuschunk.auth.model.UserLoginDto;
import eu.invouk.nexuschunk.auth.model.UserRegistrationDto;
import eu.invouk.nexuschunk.services.AvatarService;
import eu.invouk.nexuschunk.services.UserService;
import eu.invouk.nexuschunk.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.security.Principal;

@ControllerAdvice
@Slf4j
public class GlobalModelAttributeAdvice {

    private final AvatarService avatarService;
    private final UserService userService;

    public GlobalModelAttributeAdvice(AvatarService avatarService, UserService userService) {
        this.avatarService = avatarService;
        this.userService = userService;
    }

    @ModelAttribute("userRegistrationDto")
    public UserRegistrationDto userRegistrationDto() {
        return new UserRegistrationDto();
    }

    @ModelAttribute("userLoginDto")
    public UserLoginDto userLoginDto() {
        return new UserLoginDto();
    }

    @ModelAttribute("userVerificationCodeDto")
    public EmailDto userVerificationCodeDto() {
        return new EmailDto();
    }

    @ModelAttribute("forgotPasswordDto")
    public EmailDto forgotPasswordDto() {
        return new EmailDto();
    }


    @ModelAttribute
    public void addGlobalAvatar(Model model, Principal principal) {
        if (!model.containsAttribute("avatar") && principal != null) {
            User user = userService.getUserByPrincipal(principal);
            log.info("addGlobalAvatar");
            if (user != null)
                model.addAttribute("avatar", avatarService.getAvatar(user, 100));
        }
    }
    @ModelAttribute
    public void addGlobalUser(Model model, Principal principal) {
        if (!model.containsAttribute("user") && principal != null) {
            User user = userService.getUserByPrincipal(principal);
            log.info("addGlobalUser");
            if (user != null)
                model.addAttribute("user", user);
        }
    }
}