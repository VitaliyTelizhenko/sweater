package vit.sweater.sweater.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vit.sweater.sweater.domain.Message;
import vit.sweater.sweater.domain.User;
import vit.sweater.sweater.repositories.MessageRepo;

@Service
public class MessageService {

    private final MessageRepo messageRepo;

    public MessageService(MessageRepo messageRepo) {
        this.messageRepo = messageRepo;
    }

    public Page<Message> messageList(Pageable pageable, String filter){

        if(filter != null && !filter.isEmpty()){
            return messageRepo.findByTag(filter, pageable);
        } else {
            return messageRepo.findAll(pageable);
        }
    }

    public void save(Message message) {

        messageRepo.save(message);

    }

    public Page<Message> findByAuthor(User user, Pageable pageable) {
        return messageRepo.findByAuthor(user, pageable);
    }

    public void delete(Message message) {

        messageRepo.delete(message);
    }

    public Page<Message> findAll(Pageable pageable) {
        return messageRepo.findAll(pageable);
    }
}
