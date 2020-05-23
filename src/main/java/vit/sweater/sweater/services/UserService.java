package vit.sweater.sweater.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import vit.sweater.sweater.domain.Role;
import vit.sweater.sweater.domain.User;
import vit.sweater.sweater.repositories.UserRepo;

import java.util.Collections;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

    private final UserRepo userRepo;
    private final MailService mailService;

    public UserService(UserRepo userRepo, MailService mailService) {
        this.userRepo = userRepo;
        this.mailService = mailService;
    }

    @Override
    public UserDetails loadUserByUsername(String s) {
        User user = userRepo.findByUsername(s);
        if (user != null) {
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

        user.setActive(true);
        user.setRoles(Collections.singleton(Role.USER));
        user.setActivationCode(UUID.randomUUID().toString());
        userRepo.save(user);

        if(!StringUtils.isEmpty(user.getEmail())){
            String message = String.format(
                    "Hello, %s! \n" +
                            "Welcome to Sweater. Visit next link: http://localhost:8080/activate/%s",
                    user.getUsername(), user.getActivationCode()
            );
            mailService.send(user.getEmail(), "Activation code", message);
        }

        return true;
    }

    public boolean activateUser(String code) {
        User user = userRepo.findByActivationCode(code);

        if(user == null){
            return false;
        }

        user.setActivationCode(null);

        userRepo.save(user);

        return true;
    }
}
