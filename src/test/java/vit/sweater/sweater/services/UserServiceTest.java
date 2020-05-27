package vit.sweater.sweater.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import vit.sweater.sweater.domain.Role;
import vit.sweater.sweater.domain.User;
import vit.sweater.sweater.repositories.UserRepo;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepo userRepo;

    @MockBean
    private MailService mailService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    void addUser() {
        User user = new User();
        user.setEmail("handrix24@gmail.com");

        boolean isCreated = userService.addUser(user);

        assertTrue(isCreated);
        assertNotNull(user.getActivationCode());
        assertTrue(is(user.getRoles()).matches(Collections.singleton(Role.USER)));

        verify(userRepo, times(1)).save(user);
        verify(mailService, times(1))
                .send(eq(user.getEmail()), eq("Activation code"), contains("Welcome to Sweater"));

    }

    @Test
    public void addUserFailTest(){

        User user = new User();
        user.setUsername("James");

        doReturn(new User()).when(userRepo).findByUsername("James");

        boolean isCreated = userService.addUser(user);

        assertFalse(isCreated);
        verify(userRepo, times(0)).save(any(User.class));
        verify(mailService, times(0))
                .send(anyString(), anyString(),  anyString());
    }

    @Test
    public void activateUserTest() {

        User returned = new User();
        returned.setActivationCode("some code");

        doReturn(returned).when(userRepo).findByActivationCode("ActCode");

        boolean activated = userService.activateUser("ActCode");

        assertTrue(activated);
        assertNull(returned.getActivationCode());
        assertTrue(returned.isActive());
        verify(userRepo, times(1)).save(returned);
    }

    @Test
    public void activateUserFailTest(){

        boolean activated = userService.activateUser("ActCode2");

        assertFalse(activated);
        verify(userRepo, times(0)).save(any(User.class));
    }
}