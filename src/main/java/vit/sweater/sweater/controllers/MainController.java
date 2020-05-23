package vit.sweater.sweater.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import vit.sweater.sweater.domain.Message;
import vit.sweater.sweater.domain.User;
import vit.sweater.sweater.repositories.MessageRepo;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
class MainController {

    private final MessageRepo messageRepo;

    @Value("${upload.path}")
    private String uploadPath;

    @Autowired
    public MainController(MessageRepo messageRepo) {
        this.messageRepo = messageRepo;
    }

    @GetMapping("/")
    public String greeting(Model model){
        return "greeting";
    }

    @GetMapping("/main")
    public String main(@RequestParam(required = false, defaultValue = "") String filter,
                       Model model){
        List<Message> messages;

        if(filter != null && !filter.isEmpty()){
            messages = messageRepo.findByTag(filter);
        } else {
            messages = messageRepo.findAll();
        }

        model.addAttribute("messages", messages);
        model.addAttribute("filter", filter);

        return "main";
    }

    @PostMapping("/main")
    public String add(@AuthenticationPrincipal User user,
                      @RequestParam String text,
                      @RequestParam String tag,
                      @RequestParam("file") MultipartFile file,
                      Model model) throws IOException {
        Message message = new Message(text, tag, user);

        if(file != null && !file.getOriginalFilename().isEmpty()){
            File uploadDir = new File(uploadPath);

            if(!uploadDir.exists()){
                uploadDir.mkdir();
            }

            String uuidFile = UUID.randomUUID().toString();
            String resultFileName = uuidFile + "." + file.getOriginalFilename();

            file.transferTo(new File(uploadPath + "/" + resultFileName));

            message.setFilename(resultFileName);
        }

        messageRepo.save(message);

        List<Message> messages = messageRepo.findAll();

        model.addAttribute("messages", messages);
        model.addAttribute("filter", "");

        return "main";
    }
}
