package vit.sweater.sweater.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
class GreetingController {

    @GetMapping("/")
    public String greeting(Map<String, Object> model){
        return "greeting";
    }

}
