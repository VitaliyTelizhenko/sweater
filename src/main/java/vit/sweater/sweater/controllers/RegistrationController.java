package vit.sweater.sweater.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import vit.sweater.sweater.domain.User;
import vit.sweater.sweater.dto.CaptchaResponseDTO;
import vit.sweater.sweater.services.UserService;

import javax.validation.Valid;
import java.util.Collections;
import java.util.Map;

@Controller
public class RegistrationController {

    private final static String CAPTCHA_URL = "https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s";

    @Value("${recaptcha.secret}")
    private String secret;

    private final UserService userService;
    private final RestTemplate restTemplate;
    
    @Autowired
    public RegistrationController(UserService userService, RestTemplate restTemplate) {
        this.userService = userService;
        this.restTemplate = restTemplate;
    }

    @GetMapping("/registration")
    public String registration(){
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(@RequestParam("g-recaptcha-response") String capchaResponse,
                          @Valid User user,
                          BindingResult result,
                          Model model){

        String url = String.format(CAPTCHA_URL, secret, capchaResponse);

        CaptchaResponseDTO response = restTemplate.postForObject(url, Collections.emptyList(), CaptchaResponseDTO.class);

        if (!response.isSuccess()){
            model.addAttribute("captchaError", "Fill Captcha");
        }


        if(user.getPassword() != null && !user.getPassword().equals(user.getPassword2())){
            model.addAttribute("passwordError", "Passwords are different!");

            return "registration";
        }

        if(result.hasErrors() || !response.isSuccess()){
            Map<String, String> errorMap = ControllerUtil.getErrors(result);

            model.mergeAttributes(errorMap);

            return "registration";
        }

        if(!userService.addUser(user)){
            model.addAttribute("usernameError", "User exists");
            return "registration";
        }

        model.addAttribute("messageOK", "Visit your email to activate");

        return "login";
    }

    @GetMapping("/activate/{code}")
    public String activate(Model model, @PathVariable String code){
        boolean isActivated = userService.activateUser(code);

        if(isActivated){
            model.addAttribute("messageOK", "User successfully activated");
        } else {
            model.addAttribute("message", "Activation failed");
        }
        return "login";
    }

    @GetMapping("/login-error")
    public String loginError(Model model){

        model.addAttribute("message", "Wrong username or password");
        return "login";
    }
}
