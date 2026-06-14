package me.tihon.database;

import me.tihon.auth.Hasher;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDAO {

    private final DatabaseManager databaseManager;
    public UserDAO(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public boolean register(String username, String password) {

        String sql = "INSERT INTO users(username, password) VALUES (?, ?)";
        try(Connection connection = databaseManager.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, Hasher.hash(password));
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean login(String username, String password) {

        String sql = "SELECT * FROM users " + "WHERE username = ? AND password = ?";
        try(Connection connection = databaseManager.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, Hasher.hash(password));
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            return false;
        }
    }
}

