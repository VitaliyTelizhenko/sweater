package vit.sweater.sweater.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import vit.sweater.sweater.domain.Message;
import vit.sweater.sweater.domain.Role;
import vit.sweater.sweater.domain.User;
import vit.sweater.sweater.repositories.UserRepo;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    private final UserRepo userRepo;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepo userRepo, MailService mailService, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.mailService = mailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String s) {
        User user = userRepo.findByUsername(s);
        if (user != null && user.getActivationCode() == null) {
            return user;
        }
        throw new UsernameNotFoundException(
                "User '" + s + "' not found");
    }

    public boolean addUser(User user){
        User userFromDb = userRepo.findByUsername(user.getUsername());

        if(userFromDb != null){
            return false;
        }

        user.setRoles(Collections.singleton(Role.USER));
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        sendMessage(user);

        userRepo.save(user);

        return true;
    }

    private void sendMessage(User user) {
        user.setActivationCode(UUID.randomUUID().toString());

        String message = String.format(
                "Hello, %s! \n" +
                        "Welcome to Sweater. Visit next link: http://localhost:8080/activate/%s",
                user.getUsername(), user.getActivationCode()
        );
        mailService.send(user.getEmail(), "Activation code", message);
    }

    public boolean activateUser(String code) {
        User user = userRepo.findByActivationCode(code);

        if(user == null){
            return false;
        }

        user.setActivationCode(null);
        user.setActive(true);

        userRepo.save(user);

        return true;
    }

    public List<User> findAll() {
        return userRepo.findAll();
    }

    public void saveUser(String username, Map<String, String> form, User user) {
        user.setUsername(username);

        Set<String> roles = Arrays.stream(Role.values())
                .map(Role::name)
                .collect(Collectors.toSet());

        user.getRoles().clear();

        for(String key : form.keySet()){
            if(roles.contains(key)){
                user.getRoles().add(Role.valueOf(key));
            }
        }

        userRepo.save(user);
    }

    public void updateProfile(User user, String password, String email) {

        String userEmail = user.getEmail();

        boolean isEmailChanged = (email != null && !email.equals(userEmail)) ||
                (userEmail != null && !userEmail.equals(email));

        if(isEmailChanged){
            user.setEmail(email);

            if(!StringUtils.isEmpty(email)){
                sendMessage(user);
            }
        }

        if(!StringUtils.isEmpty(password)){
            user.setPassword(passwordEncoder.encode(password));
        }

        userRepo.save(user);
    }

    public void subscribe(User currentUser, User user) {
        user.getSubscribers().add(currentUser);

        userRepo.save(user);
    }

    public void unsubscribe(User currentUser, User user) {
        user.getSubscribers().remove(currentUser);

        userRepo.save(user);
    }

    public void deleteMessage(User user, Message message) {

        user.getMessages().remove(message);

        userRepo.save(user);
    }
}
