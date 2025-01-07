package Service;

import DAO.MessageDAO;
import Model.Message;
import java.util.*;

public class MessageService {
    private MessageDAO messageDAO;

    // Construct the DAO
    public MessageService() {
        messageDAO = new MessageDAO();
    }

    // Service method to create a new message
    public Message createMessage(String message_text, int account_id, long time_posted_epoch) {
        Message newMessage = messageDAO.createMessage(message_text, account_id, time_posted_epoch);
        return newMessage;
    }

    // Service method to retrieve all messages
    public List<Message> getAllMessages() {
        List<Message> messagesList = messageDAO.getAllMessages();
        return messagesList;
    }

    // Service method to retrieve a message by ID
    public Message getMessageByID(int message_id) {
        Message returnedMessage = messageDAO.getMessageByID(message_id);
        return returnedMessage;
    }

    // Service method to delete a message by ID
    public Message deleteMessageByID(int message_id) {
        Message returnedMessage = messageDAO.deleteMessageByID(message_id);
        return returnedMessage;
    }

    // Service method to update a message by ID
    public Message updateMessageByID(int message_id, String newMessage) {
        Message returnedMessage = messageDAO.updateMessageByID(message_id, newMessage);
        return returnedMessage;
    }

    // Service method to retrieve all messages by a particular user
    public List<Message> getAllMessagesByUser(int account_id) {
        List<Message> messagesList = messageDAO.getAllMessagesByUser(account_id);
        return messagesList;
    }
}
