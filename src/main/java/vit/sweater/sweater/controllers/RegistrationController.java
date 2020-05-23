package vit.sweater.sweater.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import vit.sweater.sweater.domain.User;
import vit.sweater.sweater.services.UserService;

@Controller
public class RegistrationController {

    private final UserService userService;
    
    @Autowired
    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/registration")
    public String registration(){
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(User user, Model model){

        if(!userService.addUser(user)){
            model.addAttribute("message", "User exists");
            return "registration";
        }

        return "redirect:/login";
    }

    @GetMapping("/activate/{code}")
    public String activate(Model model, @PathVariable String code){
        boolean isActivated = userService.activateUser(code);

        if(isActivated){
            model.addAttribute("message", "User successfully activated");
        } else {
            model.addAttribute("message", "Activation failed");
        }
        return "login";
    }
}
