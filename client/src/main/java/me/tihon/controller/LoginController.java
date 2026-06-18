package me.tihon.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import me.tihon.Localization;
import me.tihon.NetworkManager;
import me.tihon.command.CommandType;
import me.tihon.dto.Request;
import me.tihon.dto.Response;

public class LoginController {

    @FXML private Label titleLabel;
    @FXML private Button loginB;
    @FXML private Button registerB;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;
    @FXML private Label langLabel;
    @FXML private ComboBox<Localization.Lang> langBox;
    private NetworkManager networkManager;

    @FXML
    private void initialize() {
        try {
            networkManager = new NetworkManager();
        } catch (Exception e) {
            statusLabel.setText(Localization.t(Localization.LOGIN_ERR_CONNECT));
        }

        langBox.getItems().addAll(Localization.Lang.values());
        langBox.setValue(Localization.getInstance().getCurrentLang());
        langBox.setOnAction(e -> {
            Localization.Lang selected = langBox.getValue();
            if (selected != null) {
                Localization.getInstance().setLang(selected);
                refreshLocale();
            }
        });

        Localization.getInstance().langProperty().addListener(
                (obs, old, val) -> refreshLocale());
        refreshLocale();
    }

    private void refreshLocale() {
        titleLabel.setText(Localization.t(Localization.LOGIN_TITLE));
        usernameField.setPromptText(Localization.t(Localization.LOGIN_USERNAME));
        passwordField.setPromptText(Localization.t(Localization.LOGIN_PASSWORD));
        loginB.setText(Localization.t(Localization.LOGIN_BTN_LOGIN));
        registerB.setText(Localization.t(Localization.LOGIN_BTN_REGISTER));
        langLabel.setText(Localization.t(Localization.LANG_LABEL) + ":");
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
            statusLabel.setText(Localization.t(Localization.LOGIN_ERR_EMPTY));
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
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
            MainController controller = loader.getController();
            controller.init(username, password, networkManager);
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(scene);
            stage.setWidth(1280);
            stage.setHeight(820);
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText(e.getMessage());
        }
    }

}
