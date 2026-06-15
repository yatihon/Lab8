package me.tihon.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import me.tihon.model.Car;
import me.tihon.model.Coordinates;
import me.tihon.model.HumanBeing;
import me.tihon.model.WeaponType;


public class EditController {

    @FXML private TextField nameField;
    @FXML private TextField xField;
    @FXML private TextField yField;
    @FXML private CheckBox realHeroBox;
    @FXML private CheckBox toothpickBox;
    @FXML private TextField impactField;
    @FXML private TextField soundtrackField;
    @FXML private TextField minutesField;
    @FXML private ComboBox<WeaponType> weaponBox;
    @FXML private CheckBox carCoolBox;
    private HumanBeing result;

    @FXML
    private void initialize() {
        weaponBox.getItems().addAll(WeaponType.values());
    }

    @FXML
    private void save() {
        try {
            result = getHuman();
            ((Stage) nameField.getScene().getWindow()).close();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Некорректные данные");
            alert.showAndWait();
        }
    }

    public HumanBeing getResult() {
        return result;
    }

    private void validate() {

        if (nameField.getText().trim().isEmpty())
            throw new IllegalArgumentException("Имя не может быть пустым");
        if (soundtrackField.getText().trim().isEmpty())
            throw new IllegalArgumentException("SoundtrackName не может быть пустым");

        float x;
        try {
            x = Float.parseFloat(xField.getText().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("X должен быть числом");
        }
        if (x > 231) throw new IllegalArgumentException("X должен быть <= 231");

        try {
            Float.parseFloat(impactField.getText().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("ImpactSpeed должен быть числом");
        }
        try {
            Integer.parseInt(yField.getText().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Y должен быть числом");
        }
        try {
            Integer.parseInt(minutesField.getText().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("MinutesOfWaiting должен быть числом");
        }
    }

    public HumanBeing getHuman() {
        validate();
        Coordinates coordinates = new Coordinates(Float.parseFloat(xField.getText().trim()),
                Integer.parseInt(yField.getText().trim()));
        Car car = new Car(carCoolBox.isSelected());

        return new HumanBeing(
                null,
                null,
                nameField.getText().trim(),
                coordinates,
                realHeroBox.isSelected(),
                toothpickBox.isSelected(),
                Float.parseFloat(impactField.getText().trim()),
                soundtrackField.getText().trim(),
                Long.parseLong(minutesField.getText().trim()),
                weaponBox.getValue(),
                car);
    }

    public void setHuman(HumanBeing human) {
        nameField.setText(human.getName());
        xField.setText(String.valueOf(human.getCoordinates().getX()));
        yField.setText(String.valueOf(human.getCoordinates().getY()));
        realHeroBox.setSelected(human.getRealHero());
        toothpickBox.setSelected(human.getHasToothpick());
        impactField.setText(String.valueOf(human.getImpactSpeed()));
        soundtrackField.setText(human.getSoundtrackName());
        minutesField.setText(String.valueOf(human.getMinutesOfWaiting()));
        weaponBox.setValue(human.getWeaponType());
        carCoolBox.setSelected(human.getCar() != null && human.getCar().isCool());
    }

    @FXML
    private void cancel() {
        result = null;
        ((Stage) nameField.getScene().getWindow()).close();
    }
}
