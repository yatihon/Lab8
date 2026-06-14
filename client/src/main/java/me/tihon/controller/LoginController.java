package me.tihon.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import me.tihon.NetworkManager;
import me.tihon.command.CommandType;
import me.tihon.dto.Request;
import me.tihon.dto.Response;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;
    private NetworkManager networkManager;

    @FXML
    private void initialize() {
        try {
            networkManager = new NetworkManager();
        } catch (Exception e) {
            statusLabel.setText("Ошибка подключения");
        }
    }

    @FXML
    private void login() {
        authenticate(CommandType.LOGIN);
    }

    @FXML
    private void register() {
        authenticate(CommandType.REGISTER);
    }

    private void authenticate(CommandType command) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Введите логин и пароль");
            return;
        }
        Request request = new Request(command, null, username, password);
        Response response = networkManager.send(request);
        statusLabel.setText(response.getMessage());
        String message = response.getMessage().toLowerCase();
        if (message.contains("успеш") || message.contains("success")) {
            openMainWindow(username, password);
        }
    }

    private void openMainWindow(String username, String password) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));
            Scene scene = new Scene(loader.load());
            MainController controller = loader.getController();
            controller.init(username, password, networkManager);
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(scene);
            stage.setWidth(1200);
            stage.setHeight(800);
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText(e.getMessage());
        }
    }

}
