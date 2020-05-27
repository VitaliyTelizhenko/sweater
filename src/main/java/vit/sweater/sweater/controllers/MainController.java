package vit.sweater.sweater.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import vit.sweater.sweater.domain.Message;
import vit.sweater.sweater.domain.User;
import vit.sweater.sweater.repositories.MessageRepo;
import vit.sweater.sweater.services.MessageService;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Controller
public class MainController {

    private final MessageService messageService;

    @Value("${upload.path}")
    private String uploadPath;

    @Autowired
    public MainController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/")
    public String greeting(Model model){
        return "greeting";
    }

    @GetMapping("/main")
    public String main(@RequestParam(required = false, defaultValue = "") String filter,
                       Model model,
                       @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable){

        Page<Message> page = messageService.messageList(pageable, filter);

        model.addAttribute("page", page);
        model.addAttribute("url", "/main");
        model.addAttribute("filter", filter);

        return "main";
    }

    @PostMapping("/main")
    public String add(@AuthenticationPrincipal User user,
                      @RequestParam("file") MultipartFile file,
                      @Valid Message message,
                      BindingResult result,
                      Model model,
                      @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable
                      ) throws IOException {
        message.setAuthor(user);

        if(result.hasErrors()){
            Map<String, String> errorMap = ControllerUtil.getErrors(result);

            model.mergeAttributes(errorMap);
            model.addAttribute("message", message);

        } else {

            saveFile(file, message);

            model.addAttribute("message", null);

            messageService.save(message);
        }


        Page<Message> page = messageService.findAll(pageable);

        model.addAttribute("page", page);
        model.addAttribute("url", "/main");
        model.addAttribute("filter", "");

        return "main";
    }

    private void saveFile(MultipartFile file, Message message) throws IOException {
        if (file != null && !file.getOriginalFilename().isEmpty()) {
            File uploadDir = new File(uploadPath);

            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }

            String uuidFile = UUID.randomUUID().toString();
            String resultFileName = uuidFile + "." + file.getOriginalFilename();

            file.transferTo(new File(uploadPath + "/" + resultFileName));

            message.setFilename(resultFileName);
        }
    }

    @GetMapping("/user-messages/{user}")
    public String userMessages(@AuthenticationPrincipal User currentUser,
                               @PathVariable User user,
                               Model model,
                               @RequestParam(required = false) Message message,
                               @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable){

        Page<Message> page = messageService.findByAuthor(user, pageable);

        model.addAttribute("userChannel", user);
        model.addAttribute("subscriptionsCount", user.getSubscriptions().size());
        model.addAttribute("subscribersCount", user.getSubscribers().size());
        model.addAttribute("page", page);
        model.addAttribute("url", "/user-messages/" + user.getId());
        model.addAttribute("message", message);
        model.addAttribute("isSubscriber", user.getSubscribers().contains(currentUser));

        model.addAttribute("isCurrentUser", currentUser.equals(user));

        return "userMessages";
    }

    @PostMapping("/user-messages/{user}")
    public String updateMessage(@AuthenticationPrincipal User currentUser,
                                @PathVariable Long user,
                                @RequestParam("id") Message message,
                                @RequestParam("text") String text,
                                @RequestParam("tag") String tag,
                                @RequestParam("file") MultipartFile file
                                ) throws IOException {
        if (message.getAuthor().equals(currentUser)) {
            if(!StringUtils.isEmpty(text)){
                message.setText(text);
            }
            if(!StringUtils.isEmpty(tag)){
                message.setTag(tag);
            }

            saveFile(file, message);

            messageService.save(message);
        }

        return "redirect:/user-messages/" + user;
    }

    @GetMapping("/user-messages/delete/{user}")
    public String deleteMessage(@PathVariable User user,
                                @RequestParam Message message){
        messageService.delete(message);

        return "redirect:/user-messages/" + user.getId();
    }
}
