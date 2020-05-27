package vit.sweater.sweater.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import vit.sweater.sweater.domain.Role;
import vit.sweater.sweater.domain.User;
import vit.sweater.sweater.services.UserService;

import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController( UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public String userList(Model model){

        model.addAttribute("users", userService.findAll());

        return "userList";
    }

    @GetMapping("{user}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String userEditForm(@PathVariable User user, Model model){
        model.addAttribute("user", user);
        model.addAttribute("roles", Role.values());

        return "userEdit";
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public String saveUser(@RequestParam String username,
                           @RequestParam Map<String, String> form,
                           @RequestParam("userId") User user){
        userService.saveUser(username, form, user);

        return "redirect:/user";
    }

    @GetMapping("/profile")
    public String getProfile(Model model, @AuthenticationPrincipal User user){
        model.addAttribute("username", user.getUsername());
        model.addAttribute("email", user.getEmail());

        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile( @AuthenticationPrincipal User user,
                                 @RequestParam String password,
                                 @RequestParam String email,
                                 Model model){
        if(StringUtils.isEmpty(email) || StringUtils.isEmpty(password)){
            model.addAttribute("error", "Cannot be empty!");
            model.addAttribute("username", user.getUsername());
            model.addAttribute("email", user.getEmail());
            return "profile";
        }

        userService.updateProfile(user, password, email);

        if(user.getActivationCode() != null){
            model.addAttribute("messageOK", "Visit your email to activate");
            return "login";
        }
        model.addAttribute("messagePass","Password changed!");
        return "login";
    }

    @GetMapping("subscribe/{user}")
    public String subscribe(@AuthenticationPrincipal User currentUser,
                            @PathVariable User user
                            ){
        userService.subscribe(currentUser, user);

        return "redirect:/user-messages/" + user.getId();
    }

    @GetMapping("unsubscribe/{user}")
    public String unsubscribe(@AuthenticationPrincipal User currentUser,
                            @PathVariable User user
                            ){
        userService.unsubscribe(currentUser, user);

        return "redirect:/user-messages/" + user.getId();
    }

    @GetMapping("{type}/{user}/list")
    public String userLists(Model model,
                            @PathVariable User user,
                            @PathVariable String type){
        model.addAttribute("userChannel", user);
        model.addAttribute("type", type);

        if("subscriptions".equals(type)){
            model.addAttribute("users", user.getSubscriptions());
        } else {
            model.addAttribute("users", user.getSubscribers());
        }

        return "subscriptions";
    }
}
