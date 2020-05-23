package vit.sweater.sweater.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import vit.sweater.sweater.domain.User;

public interface UserRepo extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findByActivationCode(String code);
}
