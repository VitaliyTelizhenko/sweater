package vit.sweater.sweater.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vit.sweater.sweater.domain.Message;
import vit.sweater.sweater.domain.User;


public interface MessageRepo extends JpaRepository<Message, Long> {
    Page<Message> findAll(Pageable pageable);

    Page<Message> findByAuthor(User user, Pageable pageable);

    Page<Message> findByTag(String tag, Pageable pageable);
}
