package Controller;

import io.javalin.Javalin;
import io.javalin.http.Context;
import java.util.*;
import Model.Account;
import Model.Message;
import Service.AccountService;
import Service.MessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;


/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {
    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create();
        app.get("example-endpoint", this::exampleHandler);
        app.post("/register", this::createNewAccountHandler);
        app.post("login", this::authenticateAccountHandler);
        app.post("/messages", this::createNewMessageHandler);
        app.get("/messages", this::getAllMessagesHandler);
        app.get("/messages/{message_id}", this::getMessageByIdHandler);
        app.delete("/messages/{message_id}", this::deleteMessageByIdHandler);
        app.patch("/messages/{message_id}", this::updateMessageByIdHandler);
        app.get("/accounts/{account_id}/messages", this::getAllMessagesByUserHandler);
        return app;
    }

    // Declare service variables
    AccountService accountService;
    MessageService messageService;

    // Constructor which instantiates service variables
    public SocialMediaController() {
        this.accountService = new AccountService();
        this.messageService = new MessageService();
    }

    /**
     * This is an example handler for an example endpoint.
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    private void exampleHandler(Context context) {
        context.json("sample text");
    }

    // Handler to create an account POST /register
    private void createNewAccountHandler(Context ctx) {
        try {
            Account account = ctx.bodyAsClass(Account.class);
            Account newAccount = accountService.createAccount(account);
    
            if (newAccount == null) {
                ctx.status(400);
                return;
            }
    
            ctx.json(accountService.authenticateAccount(newAccount)).status(200);
        } catch (Exception e) {
            ctx.status(500).result("Internal Server Error");
        }
    }
    
    

    // Handler to authenticate an account POST /login
    private void authenticateAccountHandler(Context ctx) {
        try{
            Account account = ctx.bodyAsClass(Account.class);
            Account authenticatedAccount = accountService.authenticateAccount(account);

            if(authenticatedAccount == null) {
                ctx.status(401);
                return;
            }
            
            ctx.json(authenticatedAccount).status(200);
        } catch(Exception e) {
            ctx.status(500).result("Internal Server Error");
        }
    }

    // Handler to create a message POST /messages
    private void createNewMessageHandler(Context ctx) {
        try{
            Message message = ctx.bodyAsClass(Message.class);
            Message newMessage = messageService.createMessage(message.getMessage_text(), message.getPosted_by(), message.getTime_posted_epoch());

            if(newMessage == null) {
                ctx.status(400);
                return;
            }

            ctx.json(newMessage).status(200);
        } catch(Exception e) {
            ctx.status(500).result("Internal Server Error");
        }
    }

    // Handler to get all messages GET /messages
    private void getAllMessagesHandler(Context ctx) {
        try{
            List<Message> messagesList = messageService.getAllMessages();
            ctx.json(messagesList).status(200);
        } catch(Exception e) {
            ctx.status(500).result("Internal Server Error");
        }
    }

    // Handler to get a message by ID GET /messages/{message_id}
    private void getMessageByIdHandler(Context ctx) {
        try{
            int message_id = Integer.parseInt(ctx.pathParam("message_id"));
            Message returnedMessage = messageService.getMessageByID(message_id);

            if(returnedMessage == null) {
                ctx.json("").status(200);
                return;
            }
        
            ctx.json(returnedMessage).status(200);
        } catch(Exception e) {
            ctx.status(500).result("Internal Server Error");
        }
    }

    // Handler to delete a message by ID DELETE /messages/{message_id}
    private void deleteMessageByIdHandler(Context ctx) {
        try{
            int message_id = Integer.parseInt(ctx.pathParam("message_id"));
            Message returnedMessage = messageService.deleteMessageByID(message_id);

            if(returnedMessage == null) {
                ctx.json("").status(200);
                return;
            }
        
            ctx.json(returnedMessage).status(200);
        } catch(Exception e) {
            ctx.status(500).result("Internal Server Error");
        }
    }

    // Handler to update a message by message ID PATCH /messages/{message_id}
    private void updateMessageByIdHandler(Context ctx) {
        try{
            // We need to first parse the request body to obtain the message field
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> requestBody = objectMapper.readValue(ctx.body(), new TypeReference<Map<String, Object>>() {});

            // Now we set our variables and obtain the updated message
            int message_id = Integer.parseInt(ctx.pathParam("message_id"));
            String message_text = (String) requestBody.get("message_text");
            Message returnedMessage = messageService.updateMessageByID(message_id, message_text);

            if(returnedMessage == null) {
                ctx.status(400);
                return;
            }

            ctx.json(returnedMessage).status(200);
        } catch(Exception e) {
            ctx.status(500).result("Internal Server Error");
        }
    }

    // Handler to get all messages by a particular user GET /accounts/{account_id}/messages
    private void getAllMessagesByUserHandler(Context ctx) {
        try{
            int account_id = Integer.parseInt(ctx.pathParam("account_id"));
            List<Message> messagesList = messageService.getAllMessagesByUser(account_id);
            ctx.json(messagesList).status(200);
        } catch(Exception e) {
            ctx.status(500).result("Internal Server Error");
        }
    }

}