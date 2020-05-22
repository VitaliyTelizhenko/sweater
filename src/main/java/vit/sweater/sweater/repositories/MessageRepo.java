package vit.sweater.sweater.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import vit.sweater.sweater.domain.Message;

import java.util.List;

public interface MessageRepo extends JpaRepository<Message, Long> {
    List<Message> findByTag(String tag);
}
