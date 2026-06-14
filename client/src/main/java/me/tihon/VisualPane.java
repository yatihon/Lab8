package me.tihon;

import javafx.animation.ScaleTransition;
import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import me.tihon.model.HumanBeing;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

public class VisualPane extends Pane {

    private final Map<String, Color> ownerColors = new HashMap<>();

    public void draw(TreeSet<HumanBeing> humans) {
        getChildren().clear();
        if (humans == null || humans.isEmpty()) return;
        double minX = humans.stream().mapToDouble(h -> h.getCoordinates().getX()).min().orElse(0);
        double maxX = humans.stream().mapToDouble(h -> h.getCoordinates().getX()).max().orElse(100);
        double minY = humans.stream().mapToDouble(h -> h.getCoordinates().getY()).min().orElse(0);
        double maxY = humans.stream().mapToDouble(h -> h.getCoordinates().getY()).max().orElse(100);
        double paneWidth = getWidth() <= 0 ? 600 : getWidth();
        double paneHeight = getHeight() <= 0 ? 500 : getHeight();
        double padding = 40;

        for (HumanBeing human : humans) {
            double normX = (human.getCoordinates().getX() - minX) / Math.max(1, maxX - minX);
            double normY = (human.getCoordinates().getY() - minY) / Math.max(1, maxY - minY);
            double drawX = padding + normX * (paneWidth - 2 * padding);
            double drawY = padding + normY * (paneHeight - 2 * padding);
            double r = calculateRadius(human);

            Circle circle = new Circle(drawX, drawY, r);
            circle.setFill(getColorForOwner(human.getOwner()));
            circle.setStroke(Color.BLACK);
            playAnimation(circle);
            circle.setOnMouseClicked(event -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("HumanBeing");
                alert.setHeaderText(human.getName());
                alert.setContentText(
                        "ID: " + human.getId() + "\n" +
                                "Owner: " + human.getOwner() + "\n" +
                                "X: " + human.getCoordinates().getX() + "\n" +
                                "Y: " + human.getCoordinates().getY() + "\n" +
                                "ImpactSpeed: " + human.getImpactSpeed() + "\n" +
                                "Weapon: " + human.getWeaponType());
                alert.showAndWait();
            });
            getChildren().add(circle);
        }
    }

    private double calculateRadius(HumanBeing human) {
        if (human.getImpactSpeed() == null) return 15;
        return Math.max(10, Math.min(40, human.getImpactSpeed() / 5.0));
    }

    private void playAnimation(Circle circle) {
        circle.setScaleX(0);
        circle.setScaleY(0);
        ScaleTransition animation = new ScaleTransition(Duration.millis(400), circle);
        animation.setToX(1);
        animation.setToY(1);
        animation.play();
    }

    private Color getColorForOwner(String owner) {
        return ownerColors.computeIfAbsent(owner,
                o -> Color.hsb(Math.abs(o.hashCode() % 360), 0.85, 0.85));
    }
}
