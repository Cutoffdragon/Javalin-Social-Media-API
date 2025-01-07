package DAO;

import java.sql.*;
import Model.Account;
import Util.ConnectionUtil;

public class AccountDAO {

    // Create a new account
    public Account insertAccount(Account account) {
        // Establish Connection
        Connection connection = ConnectionUtil.getConnection();

        // Set account variables
        String username = account.getUsername();
        String password = account.getPassword();

        // Return null if username is blank or password is less than 4 characters
        if (username.isBlank() || password.length() < 4) {
            return null;
        }

        try {
            // Insert the new account into the database
            String sql = "INSERT INTO Account(username, password) VALUES(?, ?);";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            preparedStatement.executeUpdate();
            return account;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    // Get account by username and password credentials
    // Used for processing user logins
    public Account getAccountByCredentials(Account account) {
        // Establish Connection
        Connection connection = ConnectionUtil.getConnection();

        // Set account variables
        String username = account.getUsername();
        String password = account.getPassword();

        // Return null if credentials are null
        if (username.isBlank() || password.isBlank())
            return null;

        try {
            // Retrieve the account from the database
            String sql = "SELECT * FROM Account WHERE username = ? AND password = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            // Parse the result set and create a new account object
            // But first, we need to check if the result set actually contains an entry
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int returnedId = resultSet.getInt("account_id");
                String returnedUsername = resultSet.getString("username");
                String returnedPassword = resultSet.getString("password");

                Account accountObject = new Account(returnedId, returnedUsername, returnedPassword);

                // Return the account object
                return accountObject;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    // Get account by ID
    public Account getAccountByID(int account_id) {
        // Establish Connection
        Connection connection = ConnectionUtil.getConnection();

        try {
            // Retrieve the account from the database
            String sql = "SELECT * FROM Account WHERE account_id = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, account_id);
            ResultSet resultSet = preparedStatement.executeQuery();

            // Parse the result set to return a new account object
            // First, we need to check if anything was returned at all
            if(resultSet.next()) {
                return new Account(account_id, resultSet.getString("username"), resultSet.getString("password"));
            }
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }

        // If all else fails, return null
        return null;
    }

}
