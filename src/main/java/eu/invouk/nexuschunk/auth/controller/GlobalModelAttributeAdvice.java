package eu.invouk.nexuschunk.auth.controller;

import eu.invouk.nexuschunk.auth.model.EmailDto;
import eu.invouk.nexuschunk.auth.model.UserLoginDto;
import eu.invouk.nexuschunk.auth.model.UserRegistrationDto;
import eu.invouk.nexuschunk.services.AvatarService;
import eu.invouk.nexuschunk.user.User;
import eu.invouk.nexuschunk.user.repositories.UserRepository;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.Optional;

@ControllerAdvice
public class GlobalModelAttributeAdvice {

    private final AvatarService avatarService;
    private final UserRepository userRepository;

    public GlobalModelAttributeAdvice(AvatarService avatarService, UserRepository userRepository) {
        this.avatarService = avatarService;
        this.userRepository = userRepository;
    }

    /**
     * Umiestni prázdne UserRegistrationDto do všetkých Modelov
     * pod názvom "userRegistrationDto" (ak použijete tento názov v modali).
     */
    @ModelAttribute("userRegistrationDto")
    public UserRegistrationDto userRegistrationDto() {
        return new UserRegistrationDto();
    }

    /**
     * Umiestni prázdne UserLoginDto do všetkých Modelov
     * pod názvom "userLoginDto".
     */
    @ModelAttribute("userLoginDto")
    public UserLoginDto userLoginDto() {
        // Ak nemáte špeciálne Login DTO, môžete použiť aj UserRegistrationDto,
        // ale pre prihlásenie je lepšie mať kratšie DTO (username, password)
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
        if (principal != null) {
            String username = principal.getName();

            // Nájdite vašu entitu User z databázy na základe mena
            Optional<User> currentUser = userRepository.findByEmail(username);

            currentUser.ifPresent(user -> model.addAttribute("avatar", avatarService.getAvatar(user, 100)));
        }
    }
    @ModelAttribute
    public void addGlobalUser(Model model, Principal principal) {
        if (principal != null) {
            String username = principal.getName();

            // Nájdite vašu entitu User z databázy na základe mena
            Optional<User> currentUser = userRepository.findByEmail(username);

            currentUser.ifPresent(user -> model.addAttribute("user", user));
        }
    }

    // Voliteľné: Pridajte ďalšie DTO, napr. pre Forgot Password
    // @ModelAttribute("forgotPasswordDto")
    // public ForgotPasswordDto forgotPasswordDto() {
    //     return new ForgotPasswordDto();
    // }

}