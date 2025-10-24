package eu.invouk.nexuschunk.controller.auth;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributeAdvice {

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

    // Voliteľné: Pridajte ďalšie DTO, napr. pre Forgot Password
    // @ModelAttribute("forgotPasswordDto")
    // public ForgotPasswordDto forgotPasswordDto() {
    //     return new ForgotPasswordDto();
    // }

}