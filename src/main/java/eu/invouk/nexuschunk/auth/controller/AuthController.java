package eu.invouk.nexuschunk.auth.controller;

import eu.invouk.nexuschunk.auth.model.EmailDto;
import eu.invouk.nexuschunk.auth.model.RecaptchaProperties;
import eu.invouk.nexuschunk.auth.model.UserRegistrationDto;
import eu.invouk.nexuschunk.user.Role;
import eu.invouk.nexuschunk.user.User;
import eu.invouk.nexuschunk.user.VerificationToken;
import eu.invouk.nexuschunk.user.permissions.repositories.RoleRepository;
import eu.invouk.nexuschunk.user.repositories.UserRepository;
import eu.invouk.nexuschunk.user.repositories.VerificationTokenRepository;
import eu.invouk.nexuschunk.services.EmailService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final RecaptchaProperties recaptchaProperties;
    private final WebClient webClient;
    private final EmailService emailService;

    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$";
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);

    public AuthController(UserRepository userRepository, RoleRepository roleRepository,VerificationTokenRepository verificationTokenRepository, PasswordEncoder passwordEncoder, WebClient webClient, RecaptchaProperties recaptchaProperties, EmailService emailService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.recaptchaProperties = recaptchaProperties;
        this.webClient = webClient;
        this.emailService = emailService;
    }

    @GetMapping("/verify")
    public String tokenVerify(Model model, @RequestParam("token") String token) {
        return verificationTokenRepository.findByToken(token).map(verificationToken -> {
          if(verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
              verificationTokenRepository.delete(verificationToken);
              verificationTokenRepository.save(verificationToken);
              return "redirect:/?error=expired_token";
          }

          User user = verificationToken.getUser();
          user.setEnabled(true);
          verificationTokenRepository.delete(verificationToken);
          user.setVerificationToken(null);
          userRepository.save(user);

          return "redirect:/?verified";
        }).orElse("redirect:/?error=token_unavailable");
    }

    @PostMapping("/resend_verification_code")
    public String tokenResend(@ModelAttribute("userVerificationCodeDto") EmailDto emailDto) {
        String email =  emailDto.getEmail();
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isEmpty())
            return "redirect:/?error=user_not_found";

        User user = optionalUser.get();
        if(user.isEnabled())
            return "redirect:/?error=user_already_verified";

        Optional<VerificationToken> verificationToken = verificationTokenRepository.findByUser(user);
        if(verificationToken.isPresent()) {
            user.setVerificationToken(null);
            userRepository.save(user);
        }

        String token = getToken();
        VerificationToken newVerificatioNToken = sendVerificationCode(user, token);
        user.setVerificationToken(newVerificatioNToken);
        userRepository.save(user);

        return "redirect:/?sent_verification_code";
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@ModelAttribute("forgotPasswordDto") EmailDto emailDto) {
        String email = emailDto.getEmail();
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isEmpty())
            return "redirect:/?error=user_not_found";

        User user = optionalUser.get();
        resetPasswordForUser(user);

        return "redirect:/?forgot-password";
    }

    @PostMapping("/register")
    public String registerUserAccount(@ModelAttribute("user") UserRegistrationDto registrationDto, @RequestParam(name = "g-recaptcha-response") String recaptchaResponse, Model model) {
        if (userRepository.findByEmail(registrationDto.getEmail()).isPresent()) {
            model.addAttribute("error", "Užívateľ s týmto emailom už existuje.");
            log.warn("Email are already registred!");
            return "redirect:/?error=user_registred&modal=register";
        }

        if (userRepository.findByMinecraftNick(registrationDto.getMinecraftNick()).isPresent()) {
            model.addAttribute("error", "Užívateľ s týmto nickom už existuje.");
            log.warn("Username with this nickname already exists!");
            return "redirect:/?error=user_registered&modal=register";
        }

        if(passwordEncoder.matches(registrationDto.getPassword(), registrationDto.getConfirmPassword())) {
            model.addAttribute("error", "Heslá sa nezhodujú!");
            log.warn("Password doesnt match!");
            return "redirect:/?error=password_not_match&modal=register";
        }

        if (!verifyRecaptcha(recaptchaResponse)) {
            //model.addAttribute("error", "Prosím, overte, že nie ste robot.");
            log.warn("Recaptcha not recognized!");
            model.addAttribute("user", registrationDto); // Vráti dáta naspäť
            return "redirect:/?error=recaptcha&modal=register";
        }

        if(registrationDto.getPassword().length() < 8) {
            log.warn("Password it too short!");
            return "redirect:/?error=password_too_short&modal=register";
        }

        if(validatePassword(registrationDto.getPassword())) {
            log.warn("Password it not complex!");
            return "redirect:/?error=password_complexity&modal=register";
        }

        Optional<Role> userRoleOptional = roleRepository.findByName("ROLE_USER");
        Role userRole;
        if (userRoleOptional.isEmpty()) {
            userRole = new Role();
            userRole.setName("ROLE_USER");
            roleRepository.save(userRole);
        } else userRole = userRoleOptional.get();

        User user = new User();
        user.setEmail(registrationDto.getEmail());
        user.setMinecraftNick(registrationDto.getMinecraftNick());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setRoles(Collections.singleton(userRole)); // Priradíme rolu ROLE_USER
        user.setRegistrationDate(LocalDateTime.now());
        user.setEnabled(false);

        String token = getToken();
        VerificationToken verificationToken = sendVerificationCode(user, token);
        user.setVerificationToken(verificationToken);

        userRepository.save(user);
        verificationTokenRepository.save(verificationToken);

        return "redirect:/?registered&modal=login";
    }

    private boolean validatePassword(String password) {
        if (password == null) {
            return false;
        }
        Matcher matcher = PASSWORD_PATTERN.matcher(password);
        return matcher.matches();
    }
    
    private VerificationToken sendVerificationCode(User user, String code) {
        VerificationToken token = new VerificationToken();
        token.setToken(code);
        token.setUser(user);
        token.setExpiryDate(LocalDateTime.now().plusMinutes(30));

        emailService.sendHtmlEmail(
                user.getEmail(),
                "Potvrdenie E-Mailovej adresy",
                "<h3>Prosím klikni na tento odkaz pre potvrdenie emailu a dokončenie registrácie!</h3><br>" +
                        "<h5>Kód pre overenie: " + code + " </h5> <br>" +
                        "<a href='http://localhost:8080/verify?token=" + code + "'>KLIKNI PRE OVERENIE!</a>"
        );

        return token;
    }

    private String resetPasswordForUser(User user) {
        String tokenizedPassword = getToken();
        String passwordShorted = tokenizedPassword.substring(0, 10); // max 10 char password

        String hashedPassword = passwordEncoder.encode(passwordShorted);
        user.setPassword(hashedPassword);
        userRepository.save(user);
        emailService.sendHtmlEmail(
                user.getEmail(),
                "Resetovanie hesla",
                "<h3>Tvoje heslo sa práve resetovalo!</h3><br>" +
                        "<h5>Tvoje nové heslo: " + passwordShorted + " </h5> <br>" +
                        "Prosím ihneď si ho zmeň!"
        );

        return passwordShorted;
    }

    private String getToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private boolean verifyRecaptcha(String recaptchaResponse) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("secret", recaptchaProperties.getSecretKey());
        formData.add("response", recaptchaResponse);

        RecaptchaResponse apiResponse = webClient.post()
                .uri(recaptchaProperties.getVerifyUrl())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(formData)
                .retrieve()
                .bodyToMono(RecaptchaResponse.class)
                .block();

        return apiResponse != null && apiResponse.isSuccess();
    }

    @Data
    public static class RecaptchaResponse {
        private boolean success;
        private List<String> errorCodes;
    }

}
