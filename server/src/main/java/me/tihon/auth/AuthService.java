package me.tihon.auth;

import me.tihon.database.UserDAO;

public class AuthService {

    private final UserDAO userDAO;

    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public boolean register(String username, String password) {
        if (username == null || username.isBlank()) {
            return false;
        }
        if (password == null || password.isBlank()) {
            return false;
        }
        return userDAO.register(username, password);
    }

    public boolean login(String username, String password) {
        if (username == null || password == null) {
            return false;
        }
        return userDAO.login(username, password);
    }
}