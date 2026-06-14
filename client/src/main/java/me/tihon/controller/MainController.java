package me.tihon.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import me.tihon.NetworkManager;
import me.tihon.VisualPane;
import me.tihon.command.CommandType;
import me.tihon.dto.Request;
import me.tihon.dto.Response;
import me.tihon.model.HumanBeing;

import java.util.TreeSet;

public class MainController {

    @FXML private Label currentUserLabel;
    @FXML private TableView<HumanBeing> table;
    @FXML private TableColumn<HumanBeing, Integer> idColumn;
    @FXML private TableColumn<HumanBeing, String> ownerColumn;
    @FXML private TableColumn<HumanBeing, String> nameColumn;
    @FXML private TableColumn<HumanBeing, Float> xColumn;
    @FXML private TableColumn<HumanBeing, Integer> yColumn;
    @FXML private TableColumn<HumanBeing, Float> impactColumn;
    @FXML private TableColumn<HumanBeing, String> weaponColumn;
    @FXML private VisualPane visualPane;

    private String username;
    private String password;
    private NetworkManager networkManager;
    private final ObservableList<HumanBeing> data = FXCollections.observableArrayList();

    public void init(String username, String password, NetworkManager networkManager) {
        this.username = username;
        this.password = password;
        this.networkManager = networkManager;
        currentUserLabel.setText("Пользователь: " + username);
        configureTable();
        refreshCollection();
    }

    private void configureTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        ownerColumn.setCellValueFactory(new PropertyValueFactory<>("owner"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        impactColumn.setCellValueFactory(new PropertyValueFactory<>("impactSpeed"));
        weaponColumn.setCellValueFactory(new PropertyValueFactory<>("weaponType"));
        xColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getCoordinates().getX()));
        yColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getCoordinates().getY()));
        table.setItems(data);
    }

    @FXML
    private void refreshCollection() {
        Response response = networkManager.send(new Request(CommandType.SHOW, null, username, password));
        TreeSet<HumanBeing> collection = response.getCollection();
        data.clear();
        if (collection != null) {
            data.addAll(collection);
            visualPane.draw(collection);
        }
    }

    @FXML
    private void deleteSelected() {
        HumanBeing selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        Response response = networkManager.send(new Request(CommandType.REMOVE_BY_ID,
                selected.getId(), username, password));
        showMessage(response.getMessage());
        refreshCollection();
    }

    @FXML
    private void showInfo() {
        Response response = networkManager.send(new Request(CommandType.INFO, null, username, password));
        showMessage(response.getMessage());
    }

    @FXML
    private void showHelp() {
        Response response = networkManager.send(new Request(CommandType.HELP, null, username, password));
        showMessage(response.getMessage());
    }

    private void showMessage(String txt) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(txt);
        alert.showAndWait();
    }

    @FXML
    private void addHuman() {
        HumanBeing human = showEditDialog(null);
        if (human == null) return;
        Response response = networkManager.send(new Request(CommandType.ADD, human, username, password));
        showMessage(response.getMessage());
        refreshCollection();
    }

    @FXML
    private void editHuman() {
        HumanBeing selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        HumanBeing edited = showEditDialog(selected);
        if (edited == null) return;
        edited.setId(selected.getId());
        Response response = networkManager.send(new Request(CommandType.UPDATE, edited, username, password));
        showMessage(response.getMessage());
        refreshCollection();
    }

    @FXML
    private void clearCollection() {
        System.out.println("CLEAR CLICKED");
        Response response = networkManager.send(new Request(CommandType.CLEAR, null, username, password));
        showMessage(response.getMessage());
        refreshCollection();
    }

    @FXML
    private void addIfMax() {
        HumanBeing human = showEditDialog(null);
        if (human == null) return;
        Response response = networkManager.send(new Request(CommandType.ADD_IF_MAX, human, username, password));
        showMessage(response.getMessage());
        refreshCollection();
    }

    @FXML
    private void addIfMin() {
        HumanBeing human = showEditDialog(null);
        if (human == null) return;
        Response response = networkManager.send(new Request(CommandType.ADD_IF_MIN, human, username, password));
        showMessage(response.getMessage());
        refreshCollection();
    }

    @FXML
    private void removeLower() {
        HumanBeing selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        Response response = networkManager.send(new Request(CommandType.REMOVE_LOWER, selected, username, password));
        showMessage(response.getMessage());
        refreshCollection();
    }

    @FXML
    private HumanBeing showEditDialog(HumanBeing human) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edit.fxml"));
            Scene scene = new Scene(loader.load());
            EditController controller = loader.getController();
            if (human != null) controller.setHuman(human);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            return controller.getResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
