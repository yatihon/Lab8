package me.tihon.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import me.tihon.*;
import me.tihon.command.CommandType;
import me.tihon.dto.Request;
import me.tihon.dto.Response;
import me.tihon.model.HumanBeing;

import java.io.File;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

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
    @FXML private TableColumn<HumanBeing, String>  realHeroColumn;
    @FXML private TableColumn<HumanBeing, String>  soundtrackColumn;
    @FXML private TableColumn<HumanBeing, Long> minutesColumn;

    @FXML private TextField filterField;
    @FXML private ComboBox<String> filterColumnBox;
    @FXML private ComboBox<String> sortColumnBox;
    @FXML private ComboBox<String> sortOrderBox;
    @FXML private ComboBox<Localization.Lang> langBox;
    @FXML private VisualPane visualPane;

    @FXML private Label filterLabel;
    @FXML private Label filterColLabel;
    @FXML private Label sortLabel;
    @FXML private Label langLabel;
    @FXML private Label statusCountLabel;
    @FXML private Label statusTimeLabel;
    @FXML private Label statusConnLabel;
    @FXML private Button btnRefresh;
    @FXML private Button btnInfo;
    @FXML private Button btnHelp;
    @FXML private Button btnAdd;
    @FXML private Button btnEdit;
    @FXML private Button btnDelete;
    @FXML private Button btnClear;
    @FXML private Button btnAddIfMax;
    @FXML private Button btnAddIfMin;
    @FXML private Button btnRemoveLower;
    @FXML private Button btnPrintDesc;
    @FXML private Button btnFilterName;
    @FXML private Button btnFilterMin;
    @FXML private Button btnScript;


    private String username;
    private String password;
    private NetworkManager networkManager;
    private Refresher refresher;
    private final ObservableList<HumanBeing> rawData = FXCollections.observableArrayList();
    private final ObservableList<HumanBeing> data = FXCollections.observableArrayList();

    public void init(String username, String password, NetworkManager networkManager) {
        this.username = username;
        this.password = password;
        this.networkManager = networkManager;
        currentUserLabel.setText("Пользователь: " + username);
        configureTable();
        configureFilterBoxes();
        configureLangBox();
        Localization.getInstance().langProperty().addListener(
                (obs, oldL, newL) -> refreshLocale());
        refreshLocale();
        visualPane.setCurrentUser(username);
        visualPane.setEditCallback(this::editHumanDirectly);

        refresher = new Refresher(networkManager, username, password, this::applyUpdate);
        refresher.startR();
        Platform.runLater(() -> {
            Stage stage = (Stage) currentUserLabel.getScene().getWindow();
            if (stage != null) stage.setOnCloseRequest(e -> refresher.stopR());
        });
    }

    private void configureTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        ownerColumn.setCellValueFactory(new PropertyValueFactory<>("owner"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        impactColumn.setCellValueFactory(new PropertyValueFactory<>("impactSpeed"));
        weaponColumn.setCellValueFactory(new PropertyValueFactory<>("weaponType"));
        soundtrackColumn.setCellValueFactory(new PropertyValueFactory<>("soundtrackName"));
        minutesColumn.setCellValueFactory(new PropertyValueFactory<>("minutesOfWaiting"));
        xColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getCoordinates().getX()));
        yColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getCoordinates().getY()));
        realHeroColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(String.valueOf(cell.getValue().getRealHero())));

        table.setRowFactory(tv -> {
            TableRow<HumanBeing> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    HumanBeing h = row.getItem();
                    if (username.equals(h.getOwner())) {
                        editHumanDirectly(h);
                    } else {
                        showMessage("Вы можете редактировать только свои объекты");
                    }
                }
            });
            return row;
        });
        table.setItems(data);
    }

    private void configureLangBox() {
        langBox.getItems().addAll(Localization.Lang.values());
        langBox.setValue(Localization.getInstance().getCurrentLang());
        langBox.setOnAction(e -> {
            Localization.Lang lang = langBox.getValue();
            if (lang != null) Localization.getInstance().setLang(lang);
        });
    }

    private void configureFilterBoxes() {
        List<String> columns = List.of(
                "id", "owner", "name", "x", "y",
                "impactSpeed", "weapon", "realHero", "soundtrack", "minutes");
        filterColumnBox.getItems().addAll(columns);
        filterColumnBox.setValue("name");
        sortColumnBox.getItems().addAll(columns);
        sortColumnBox.setValue("id");
        sortOrderBox.getItems().addAll(Localization.t(Localization.SORT_ASC),
                Localization.t(Localization.SORT_DESC));
        sortOrderBox.setValue(Localization.t(Localization.SORT_ASC));

        filterField.textProperty().addListener(
                (obs, old, val) -> applyFilterAndSort());
        filterColumnBox.setOnAction(e -> applyFilterAndSort());
        sortColumnBox.setOnAction(e -> applyFilterAndSort());
        sortOrderBox.setOnAction(e -> applyFilterAndSort());
    }

    private void applyUpdate(TreeSet<HumanBeing> collection) {
        if (collection == null) {
            if (statusConnLabel != null) {
                statusConnLabel.setText("● Нет связи");
                statusConnLabel.setStyle("-fx-text-fill: #f38ba8; -fx-font-size: 11px;");
            }
            return;
        }
        rawData.setAll(collection);
        applyFilterAndSort();
        visualPane.draw(new TreeSet<>(rawData));
        updateStatusBar(collection.size());
    }

    private void updateStatusBar(int total) {
        if (statusCountLabel == null) return;
        int mine = (int) rawData.stream()
                .filter(h -> username.equals(h.getOwner())).count();
        statusCountLabel.setText("Объектов: " + total + "  •  Ваших: " + mine);
        statusTimeLabel.setText("Обновлено: " +
                Localization.getInstance().formatDateTime(java.time.ZonedDateTime.now()));
        statusConnLabel.setText("● Online");
        statusConnLabel.setStyle("-fx-text-fill: #a6e3a1; -fx-font-size: 11px;");
    }

    private void applyFilterAndSort() {
        String filterText = filterField.getText().trim().toLowerCase();
        String filterCol = filterColumnBox.getValue();
        String sortCol = sortColumnBox.getValue();
        boolean descending = Localization.t(Localization.SORT_DESC)
                .equals(sortOrderBox.getValue());

        List<HumanBeing> res = rawData.stream().filter(h -> filterText.isEmpty()
                || getAsString(h, filterCol).toLowerCase().contains(filterText))
                .sorted(descending ? getComparator(sortCol).reversed() : getComparator(sortCol))
                .collect(Collectors.toList());
        data.setAll(res);
    }

    private String getAsString(HumanBeing h, String col) {
        if (col == null) return "";
        return switch (col) {
            case "id" -> String.valueOf(h.getId());
            case "owner" -> h.getOwner() != null ? h.getOwner() : "";
            case "name" -> h.getName() != null ? h.getName() : "";
            case "x" -> String.valueOf(h.getCoordinates().getX());
            case "y" -> String.valueOf(h.getCoordinates().getY());
            case "impactSpeed" -> String.valueOf(h.getImpactSpeed());
            case "weapon" -> h.getWeaponType() != null ? h.getWeaponType().name() : "";
            case "realHero" -> String.valueOf(h.getRealHero());
            case "soundtrack" -> h.getSoundtrackName() != null ? h.getSoundtrackName() : "";
            case "minutes" -> String.valueOf(h.getMinutesOfWaiting());
            default -> "";
        };
    }

    private Comparator<HumanBeing> getComparator(String col) {
        return switch (col != null ? col : "id") {
            case "id" -> Comparator.comparingInt(h -> nvl0(h.getId()));
            case "owner" -> Comparator.comparing(h -> nvl(h.getOwner()));
            case "name" -> Comparator.comparing(h -> nvl(h.getName()));
            case "x" -> Comparator.comparingDouble(h -> h.getCoordinates().getX());
            case "y" -> Comparator.comparingInt(h -> nvl0(h.getCoordinates().getY()));
            case "impactSpeed" -> Comparator.comparingDouble(
                    h -> h.getImpactSpeed() != null ? h.getImpactSpeed() : 0f);
            case "weapon" -> Comparator.comparing(
                    h -> h.getWeaponType() != null ? h.getWeaponType().name() : "");
            case "realHero" -> Comparator.comparing(h -> String.valueOf(h.getRealHero()));
            case "soundtrack" -> Comparator.comparing(h -> nvl(h.getSoundtrackName()));
            case "minutes" -> Comparator.comparingLong(
                    h -> h.getMinutesOfWaiting() != null ? h.getMinutesOfWaiting() : 0L);
            default -> Comparator.comparingInt(h -> nvl0(h.getId()));
        };
    }

    private static String nvl(String s) { return s != null ? s : ""; }
    private static int nvl0(Integer i) { return i != null ? i : 0; }

    @FXML
    private void refreshCollection() {
        Response response = networkManager.send(new Request(CommandType.SHOW, null, username, password));
        applyUpdate(response.getCollection());
    }

    @FXML
    private void deleteSelected() {
        HumanBeing selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        if (!username.equals(selected.getOwner())) {
            showMessage("Вы можете удалять только свои объекты");
            return;
        }
        Response response = networkManager.send(new Request(CommandType.REMOVE_BY_ID,
                selected.getId(), username, password));
        showMessage(response.getMessage());
        refreshCollection();
    }

    @FXML
    private void showInfo() {
        Response response = networkManager.send(new Request(CommandType.INFO, null, username, password));
        TextArea ta = new TextArea(response.getMessage());
        ta.setEditable(false);
        ta.setWrapText(true);
        ta.setPrefSize(360, 120);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Информация");
        alert.setHeaderText(null);
        alert.getDialogPane().setContent(ta);
        alert.showAndWait();
    }

    @FXML
    private void showHelp() {
        Response response = networkManager.send(new Request(CommandType.HELP, null, username, password));
        showMessage(response.getMessage());
    }

    private void showMessage(String txt) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(txt != null ? txt : "");
        alert.getDialogPane().getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
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
        if (!username.equals(selected.getOwner())) {
            showMessage("Вы можете редактировать только свои объекты");
            return;
        }
        editHumanDirectly(selected);
    }

    @FXML
    private void editHumanDirectly(HumanBeing h) {
        HumanBeing edited = showEditDialog(h);
        if (edited == null) return;
        edited.setId(h.getId());
        Response response = networkManager.send(new Request(CommandType.UPDATE, edited, username, password));
        showMessage(response.getMessage());
        refreshCollection();
    }

    @FXML
    private void clearCollection() {
        refresher.stopR();
        try {
            Response response = networkManager.send(new Request(CommandType.CLEAR, null, username, password));
            showMessage(response.getMessage());
            Response show = networkManager.send(new Request(CommandType.SHOW, null, username, password));
            applyUpdate(show.getCollection());
        } finally {
            refresher = new Refresher(networkManager, username, password, this::applyUpdate);
            refresher.startR();
        }
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
    private void printDescending() {
        Response response = networkManager.send(new Request(CommandType.PRINT_DESCENDING, null, username, password));
        showMessage(response.getMessage());
    }

    @FXML
    private void filterStartsWithName() {
        TextInputDialog dlg = new TextInputDialog();
        dlg.setTitle("Фильтр по имени");
        dlg.setHeaderText("Введите начало имени:");
        dlg.showAndWait().ifPresent(prefix -> {
            Response response = networkManager.send(new Request(CommandType.FILTER_STARTS_WITH_NAME, prefix, username, password));
            showMessage(response.getMessage());
        });
    }

    @FXML
    private void filterLessThanMinutes() {
        TextInputDialog dlg = new TextInputDialog();
        dlg.setTitle("Фильтр по минутам");
        dlg.setHeaderText("Показать объекты с MOW меньше:");
        dlg.showAndWait().ifPresent(val -> {
            try {
                long minutes = Long.parseLong(val.trim());
                Response response = networkManager.send(new Request(CommandType.FILTER_LESS_THAN_MINUTES, minutes, username, password));
                showMessage(response.getMessage());
            } catch (NumberFormatException e) {
                showMessage("Введите целое число.");
            }
        });
    }

    @FXML
    private void executeScript() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Выбрать скрипт");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text files", "*.txt"),
                new FileChooser.ExtensionFilter("All files", "*.*"));
        File file = chooser.showOpenDialog(
                btnScript.getScene().getWindow());
        if (file == null) return;

        ScriptExecutor executor = new ScriptExecutor(networkManager, username, password);
        List<String> results = executor.execute(file.getAbsolutePath());

        TextArea ta = new TextArea(String.join("\n", results));
        ta.setEditable(false);
        ta.setWrapText(true);
        ta.setPrefSize(500, 300);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Execute Script");
        alert.setHeaderText(file.getName());
        alert.getDialogPane().setContent(ta);
        alert.showAndWait();

        refreshCollection();
    }

    @FXML
    private HumanBeing showEditDialog(HumanBeing human) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edit.fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
            EditController controller = loader.getController();
            if (human != null) controller.setHuman(human);
            Stage stage = new Stage();
            stage.setTitle(human == null
                    ? Localization.t(Localization.EDIT_TITLE_ADD)
                    : Localization.t(Localization.EDIT_TITLE_EDIT));
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            return controller.getResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void refreshLocale() {
        Localization loc = Localization.getInstance();
        Stage stage = currentUserLabel.getScene() != null
                ? (Stage) currentUserLabel.getScene().getWindow() : null;
        if (stage != null) stage.setTitle(loc.get(Localization.APP_TITLE));
        currentUserLabel.setText(loc.get(Localization.USER_LABEL) + ": " + username);
        langLabel.setText(loc.get(Localization.LANG_LABEL) + ":");

        idColumn.setText(loc.get(Localization.COL_ID));
        ownerColumn.setText(loc.get(Localization.COL_OWNER));
        nameColumn.setText(loc.get(Localization.COL_NAME));
        xColumn.setText(loc.get(Localization.COL_X));
        yColumn.setText(loc.get(Localization.COL_Y));
        impactColumn.setText(loc.get(Localization.COL_IMPACT));
        weaponColumn.setText(loc.get(Localization.COL_WEAPON));
        realHeroColumn.setText(loc.get(Localization.COL_REAL_HERO));
        soundtrackColumn.setText(loc.get(Localization.COL_SOUNDTRACK));
        minutesColumn.setText(loc.get(Localization.COL_MINUTES));

        btnRefresh.setText(loc.get(Localization.BTN_REFRESH));
        btnInfo.setText(loc.get(Localization.BTN_INFO));
        btnHelp.setText(loc.get(Localization.BTN_HELP));
        btnAdd.setText(loc.get(Localization.BTN_ADD));
        btnEdit.setText(loc.get(Localization.BTN_EDIT));
        btnDelete.setText(loc.get(Localization.BTN_DELETE));
        btnClear.setText(loc.get(Localization.BTN_CLEAR));
        btnAddIfMax.setText(loc.get(Localization.BTN_ADD_IF_MAX));
        btnAddIfMin.setText(loc.get(Localization.BTN_ADD_IF_MIN));
        btnRemoveLower.setText(loc.get(Localization.BTN_REMOVE_LOWER));
        btnPrintDesc.setText(loc.get(Localization.BTN_PRINT_DESC));
        btnFilterName.setText(loc.get(Localization.BTN_FILTER_NAME));
        btnFilterMin.setText(loc.get(Localization.BTN_FILTER_MIN));
        btnScript.setText("ExecuteScript");

        filterLabel.setText(loc.get(Localization.FILTER_LABEL));
        filterColLabel.setText(loc.get(Localization.FILTER_COL_LABEL));
        sortLabel.setText(loc.get(Localization.SORT_LABEL));
        filterField.setPromptText(loc.get(Localization.FILTER_LABEL));
        langLabel.setText(loc.get(Localization.LANG_LABEL) + ":");

        String currentSort = sortOrderBox.getValue();
        sortOrderBox.getItems().setAll(
                loc.get(Localization.SORT_ASC),
                loc.get(Localization.SORT_DESC));

        boolean wasDesc = currentSort != null &&
                (currentSort.equals("По убыванию") || currentSort.equals("Absteigend") ||
                        currentSort.equals("Faldende")    || currentSort.equals("Descending"));
        sortOrderBox.setValue(wasDesc
                ? loc.get(Localization.SORT_DESC)
                : loc.get(Localization.SORT_ASC));

        table.setPlaceholder(new Label(loc.get(Localization.COLLECTION_EMPTY)));
    }
}
