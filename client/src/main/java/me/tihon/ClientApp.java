package me.tihon;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setTitle("HumanBeing Manager");
        stage.setScene(scene);
        stage.setMinWidth(400);
        stage.setMinHeight(250);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
