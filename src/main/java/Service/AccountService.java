package Service;

import DAO.AccountDAO;
import Model.Account;

public class AccountService {
    private AccountDAO accountDAO;

    // Construct the DAO
    public AccountService() {
        accountDAO = new AccountDAO();
    }

    // Service method to create an account
    // Gets the newly created account and returns it
    public Account createAccount(Account account) {
        Account newAccount = accountDAO.insertAccount(account);

        return newAccount;
    }

    // Service method to authenticate an account
    public Account authenticateAccount(Account account) {
        Account authenticatedAccount = accountDAO.getAccountByCredentials(account);

        return authenticatedAccount;
    }

    // Service method to get an account by ID
    public Account getAccountById(int account_id) {
        Account validatedAccount = accountDAO.getAccountByID(account_id);
        return validatedAccount;
    }
}
