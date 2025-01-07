package DAO;

import java.sql.*;
import java.util.*;
import Model.Message;
import Service.AccountService;
import Util.ConnectionUtil;

public class MessageDAO {
    
    // Create a new message
    public Message createMessage(String message_text, int account_id, long time_posted_epoch) {
        // Establish Connection
        Connection connection = ConnectionUtil.getConnection();

        // Create an instance of the account service to check for an account via id
        AccountService accountService = new AccountService();

        // Set message variables
        int posted_by = account_id;

        // Check to see if the message is blank or if the message is over 255 characters in length
        if(message_text.isBlank() || message_text.length() > 255)
            return null;

        // Check to see if the account_id points to a valid account
        if(accountService.getAccountById(account_id) == null) 
            return null;
    
        try {
            // Insert the message into the database
            String sql = "INSERT INTO Message(posted_by, message_text, time_posted_epoch) VALUES(?, ?, ?);";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, posted_by);
            preparedStatement.setString(2, message_text);
            preparedStatement.setLong(3, time_posted_epoch);

            // Check to see if anything was created
            // Then, we get the ID of the newly created message and return a new message object
            int rowsAffected = preparedStatement.executeUpdate();
            if(rowsAffected > 0) {
                int newMessageId = getMessageID(message_text);
                return new Message(newMessageId, posted_by, message_text, time_posted_epoch);
            }
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    // Get a message ID
    public int getMessageID(String message_text) {
        // Establish Connection
        Connection connection = ConnectionUtil.getConnection();

        // Check to see if the message is blank
        if(message_text.isBlank())
            return 0;
        
        try {
            // Create a prepared statement and execute it
            String sql = "SELECT message_id FROM message WHERE message_text = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, message_text);
            ResultSet resultSet = preparedStatement.executeQuery();

            // Check to see if anything was returned, and then parse the result set
            if (resultSet.next()) {
                return resultSet.getInt("message_id");
            }
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }

        // If all else fails, return 0
        return 0;
    }

    // Get all messages
    public List<Message> getAllMessages() {
        // Establish Connection
        Connection connection = ConnectionUtil.getConnection();

        // Create a list to store the messages
        List<Message> messageList = new ArrayList<>();

        try {
            // Create a sql statement and execute it
            String sql = "SELECT * FROM message;";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            // Parse the result set and return a list containing all messages
            while(resultSet.next()) {
                int message_id = resultSet.getInt("message_id");
                String message_text = resultSet.getString("message_text");
                int posted_by = resultSet.getInt("posted_by");
                long time_posted_epoch = resultSet.getLong("time_posted_epoch");
                messageList.add(new Message(message_id, posted_by, message_text, time_posted_epoch));
            }

        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }

        // Return the messageList, which will be empty if there are no messages
        return messageList;
    }


    // Get message by message ID
    public Message getMessageByID(int message_id) {
        // Establish Connection
        Connection connection = ConnectionUtil.getConnection();

        try {
            // Create a prepared statement to retrieve the message
            String sql = "SELECT * FROM message WHERE message_id = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, message_id);
            ResultSet resultSet = preparedStatement.executeQuery();

            // Parse the result set to obtain and return the message
            // First, check to see if anything was returned at all
            if(resultSet.next()) {
                // Create variable to store the returned message, and return it
                Message returnedMessage = new Message(message_id, resultSet.getInt("posted_by"), resultSet.getString("message_text"), resultSet.getLong("time_posted_epoch"));  ;
                return returnedMessage;
            }
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }

        // Return null if there is no message
        return null;
    }

    // Delete message by message_id
    public Message deleteMessageByID(int message_id) {
        // Establish Connection
        Connection connection = ConnectionUtil.getConnection();

        // Store the original message so we can return it
        Message returnedMessage = getMessageByID(message_id);

        try {
            // Create a prepared statement to delete the message, and execute it
            String sql = "DELETE FROM message WHERE message_id = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, message_id);
            preparedStatement.executeUpdate();

            // Now, we return the original message
            return returnedMessage;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        // If all else fails, return null
        return null;
    }

    // Update a message by message_id
    public Message updateMessageByID(int message_id, String newMessage) {
        // Establish Connection
        Connection connection = ConnectionUtil.getConnection();

        // Check to see if the message is over 255 characters in length, or if the message is empty
        if(newMessage.length() > 255 || newMessage.isBlank())
            return null;

        try {
            // Create a prepared statement to update the message and execute it
            String sql = "UPDATE message SET message_text = ? WHERE message_id = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, newMessage);
            preparedStatement.setInt(2, message_id);
            int rowsAffected = preparedStatement.executeUpdate();

            // Check to see if anything was actually updated
            if(rowsAffected < 1) {
                return null;
            }

            // If the operation was successful, we retrieve the updated message and return it
            Message updatedMessage = getMessageByID(message_id);
            return updatedMessage;
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }

        // If all else fails, return null
        return null;
    }

    // Retrieve all messages written by a particular user
    public List<Message> getAllMessagesByUser(int account_id) {
        // Establish Connection
        Connection connection = ConnectionUtil.getConnection();

        // Create our list to store the returned messages
        List<Message> messageList = new ArrayList<>();

        try {
            // Create a prepared statement and execute it
            String sql = "SELECT * FROM message WHERE posted_by = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, account_id);
            ResultSet resultSet = preparedStatement.executeQuery();

            // Parse the result set and store the returned messages in our list
            while(resultSet.next()) {
                Message returnedMessage = new Message(resultSet.getInt("message_id"), resultSet.getInt("posted_by"), resultSet.getString("message_text"), resultSet.getLong("time_posted_epoch"));
                messageList.add(returnedMessage);
            }
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }

        // Return our list, which will be empty if the operation was unsuccessful
        return messageList;
    }
}
