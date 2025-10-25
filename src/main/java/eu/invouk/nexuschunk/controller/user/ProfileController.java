package eu.invouk.nexuschunk.controller.user;

import eu.invouk.nexuschunk.user.User;
import eu.invouk.nexuschunk.user.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@Controller
public class ProfileController {

    private final UserRepository userRepository;

    public ProfileController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping({"/profile/{minecraftNick}", "/profile"})
    public String viewProfile(
            @PathVariable(required = false) String minecraftNick, // Používame názov z URL pre prehľadnosť
            Authentication authentication,
            Model model
    ) {
        String nickToLoad = minecraftNick;
        User authenticatedUser = null;
        boolean isOwner = false;

        // --- 1. Získanie dát prihláseného užívateľa---
        if (authentication != null && authentication.isAuthenticated()) {
            // Predpokladáme, že authentication.getPrincipal() je User alebo CustomUserDetails
            Object principal = authentication.getPrincipal();
            if (principal instanceof User) {
                authenticatedUser = (User) principal;
            } else {
                // Ak je to OAUTH2/Custom Principal, nájdeme ho v DB podľa mena/emailu
                String authName = authentication.getName();
                authenticatedUser = userRepository.findByMinecraftNick(authName)
                        .orElseGet(() -> userRepository.findByEmail(authName).orElse(null));
            }
        }

        // --- 2. Určenie, ktorý profil sa má načítať---

        // Ak je URL /profile A používateľ je prihlásený, zobrazíme jeho vlastný profil
        if (nickToLoad == null && authenticatedUser != null) {
            nickToLoad = authenticatedUser.getMinecraftNick();
            // Automaticky vieme, že je to vlastník
            isOwner = true;
        }

        // Ak URL bola /profile a nikto nie je prihlásený, presmerujeme na domov
        if (nickToLoad == null) {
            return "redirect:/";
        }

        // --- 3. Načítanie Cieľového Používateľa---
        Optional<User> optionalTargetUser = userRepository.findByMinecraftNick(nickToLoad);

        // Ak sa nenašiel nickom, skúsime to podľa emailu (ak je zadaný v URL, čo je zriedkavé)
        if (optionalTargetUser.isEmpty()) {
            optionalTargetUser = userRepository.findByEmail(nickToLoad);
        }

        if (optionalTargetUser.isEmpty()) {
            throw new ResponseStatusException(NOT_FOUND, "Hráč s nickom " + nickToLoad + " nebol nájdený!");
        }

        User targetUser = optionalTargetUser.get();

        // --- 4. Finálna kontrola Vlastníctva (ak to nebolo určené v kroku 2) ---
        if (!isOwner && authenticatedUser != null) {
            isOwner = targetUser.getMinecraftNick().equals(authenticatedUser.getMinecraftNick());
        }

        log.debug("Cieľový profil: {}", targetUser.getMinecraftNick());
        log.debug("Prihlásený vlastník: {}", authenticatedUser != null ? authenticatedUser.getMinecraftNick() : "NIE");
        log.debug("Je vlastník: {}", isOwner);

        model.addAttribute("targetUser", targetUser);
        model.addAttribute("isOwner", isOwner);

        return "profile";
    }
}
