package me.tihon;

import javafx.animation.*;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import me.tihon.model.HumanBeing;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class VisualPane extends Pane {

    private static class Node {
        Circle circle;
        Text label;
        Timeline pulseTimeline;
        HumanBeing data;
        Node(Circle circle, Text label, Timeline pulseTimeline, HumanBeing data) {
            this.circle = circle;
            this.label = label;
            this.pulseTimeline = pulseTimeline;
            this.data = data;
        }
    }

    private String currentUser;
    private Consumer<HumanBeing> editCallback;
    private final Map<String, Color> ownerColors = new HashMap<>();
    private final Map<Integer, Node> nodes = new LinkedHashMap<>();

    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
    }

    public void setEditCallback(Consumer<HumanBeing> editCallback) {
        this.editCallback = editCallback;
    }

    public void draw(TreeSet<HumanBeing> humans) {

        if (humans == null) humans = new TreeSet<>();
        drawBG();
        double minX = humans.stream().mapToDouble(h -> h.getCoordinates().getX()).min().orElse(0);
        double maxX = humans.stream().mapToDouble(h -> h.getCoordinates().getX()).max().orElse(minX+1);
        double minY = humans.stream().mapToDouble(h -> h.getCoordinates().getY()).min().orElse(0);
        double maxY = humans.stream().mapToDouble(h -> h.getCoordinates().getY()).max().orElse(minY+1);
        double paneWidth = getWidth() <= 0 ? 700 : getWidth();
        double paneHeight = getHeight() <= 0 ? 500 : getHeight();
        double padding = 50;

        Map<Integer, HumanBeing> incoming = humans.stream()
                .collect(Collectors.toMap(h -> h.getId(), h -> h));

        Iterator<Map.Entry<Integer, Node>> iter = nodes.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Integer, Node> entry = iter.next();
            if (!incoming.containsKey(entry.getKey())) {
                animateRemove(entry.getValue());
                iter.remove();
            }
        }

        for (HumanBeing human : humans) {
            int id = human.getId();
            double drawX = padding + normalize(human.getCoordinates().getX(), minX, maxX) * (paneWidth - 2 * padding);
            double drawY = padding + normalize(human.getCoordinates().getY(), minY, maxY) * (paneHeight - 2 * padding);
            double r = calculateRadius(human);
            Color  color = getColorForOwner(human.getOwner());

            if (nodes.containsKey(id)) {
                updateNode(nodes.get(id), human, drawX, drawY, r, color);
            } else {
                Node node = createNode(human, drawX, drawY, r, color);
                nodes.put(id, node);
                getChildren().addAll(node.circle, node.label);
                playAnimation(node.circle);
                node.pulseTimeline.play();
            }
        }
    }

    private Node createNode(HumanBeing human, double x, double y, double r, Color color) {
        Circle circle = new Circle(x, y, r);
        circle.setFill(color.deriveColor(0, 1, 1, 0.85));
        circle.setStroke(color.darker());
        circle.setStrokeWidth(2.5);

        Text label = new Text(human.getName());
        label.setFill(Color.BLACK);
        label.setStyle("-fx-font-size: 11; -fx-font-weight: bold;");
        label.setMouseTransparent(true);
        positionLabel(label, x, y, r);

        Timeline pulse = buildPulse(circle, r);
        circle.setOnMouseEntered(e -> {
            circle.setStroke(Color.BLACK);
            circle.setStrokeWidth(3.5);
        });
        circle.setOnMouseExited(e -> {
            circle.setStroke(color.darker());
            circle.setStrokeWidth(2.5);
        });
        circle.setOnMouseClicked(e -> handleClick(human));

        return new Node(circle, label, pulse, human);
    }

    private void updateNode(Node node, HumanBeing human, double x, double y, double r, Color color) {
        node.data = human;
        node.circle.setCenterX(x);
        node.circle.setCenterY(y);
        node.label.setText(human.getName());
        positionLabel(node.label, x, y, r);

        node.pulseTimeline.stop();
        node.pulseTimeline = buildPulse(node.circle, r);
        node.circle.setRadius(r);
        node.circle.setFill(color.deriveColor(0, 1, 1, 0.85));
        node.pulseTimeline.play();
    }

    private void handleClick(HumanBeing human) {
        boolean isOwner = human.getOwner() != null && human.getOwner().equals(currentUser);
        if (isOwner && editCallback != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(human.getName());
            alert.setHeaderText(buildText(human));
            alert.setContentText("Открыть редактор?");

            ButtonType editB = new javafx.scene.control.ButtonType("Редактировать");
            ButtonType closeB = new javafx.scene.control.ButtonType("Закрыть");
            alert.getButtonTypes().setAll(editB, closeB);
            alert.showAndWait().ifPresent(result -> {
                if (result == editB) editCallback.accept(human);
            });
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("HumanBeing");
            alert.setHeaderText(human.getName());
            alert.setContentText(buildText(human));
            alert.showAndWait();
        }
    }

    private String buildText(HumanBeing h) {
        Localization loc = Localization.getInstance();
        return String.format(
                "%s: %d%n%s: %s%nX: %s  Y: %s%n%s: %s%n%s: %s%n%s: %b  %s: %b%n%s: %s%n%s: %d",
                loc.get(Localization.COL_ID),         h.getId(),
                loc.get(Localization.COL_OWNER),      h.getOwner(),
                loc.formatDec(h.getCoordinates().getX()),
                loc.formatNumber(h.getCoordinates().getY()),
                loc.get(Localization.COL_IMPACT),     loc.formatDec(
                        h.getImpactSpeed() != null ? h.getImpactSpeed() : 0f),
                loc.get(Localization.COL_WEAPON),     h.getWeaponType(),
                loc.get(Localization.COL_REAL_HERO),  h.getRealHero(),
                loc.get(Localization.COL_TOOTHPICK),  h.getHasToothpick(),
                loc.get(Localization.COL_SOUNDTRACK), h.getSoundtrackName(),
                loc.get(Localization.COL_MINUTES),    h.getMinutesOfWaiting());
    }

    private double calculateRadius(HumanBeing human) {
        if (human.getImpactSpeed() == null) return 15;
        return Math.max(10, Math.min(40, human.getImpactSpeed() / 5.0));
    }

    private void playAnimation(Circle circle) {
        circle.setScaleX(0);
        circle.setScaleY(0);
        ScaleTransition grow = new ScaleTransition(Duration.millis(350), circle);
        grow.setToX(1.15);
        grow.setToY(1.15);

        ScaleTransition settle = new ScaleTransition(Duration.millis(150), circle);
        settle.setToX(1.0);
        settle.setToY(1.0);
        SequentialTransition seq = new SequentialTransition(grow, settle);
        seq.play();
    }

    private void animateRemove(Node node) {
        node.pulseTimeline.stop();
        ScaleTransition shrink = new ScaleTransition(Duration.millis(300), node.circle);
        shrink.setToX(0);
        shrink.setToY(0);
        shrink.setOnFinished(e -> getChildren().removeAll(node.circle, node.label));
        shrink.play();

        FadeTransition fade = new FadeTransition(Duration.millis(300), node.label);
        fade.setToValue(0);
        fade.setOnFinished(e -> getChildren().remove(node.label));
        fade.play();
    }

    private Timeline buildPulse(Circle circle, double RR) {
        Timeline tl = new Timeline(new KeyFrame(Duration.ZERO,
                new KeyValue(circle.radiusProperty(), RR, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.millis(1000),
                        new KeyValue(circle.radiusProperty(), RR * 1.08, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.millis(2000),
                        new KeyValue(circle.radiusProperty(), RR, Interpolator.EASE_BOTH)));
        tl.setCycleCount(Animation.INDEFINITE);
        return tl;
    }

    private void positionLabel(Text label, double x, double y, double r) {
        label.setX(x - label.getBoundsInLocal().getWidth() / 2);
        label.setY(y + r + 14);
    }

    private double normalize(double value, double min, double max) {
        if (max == min) return 0.5;
        return (value - min) / (max - min);
    }

    private Color getColorForOwner(String owner) {
        return ownerColors.computeIfAbsent(owner,
                o -> Color.hsb(Math.abs(o.hashCode() % 360), 0.85, 0.85));
    }

    private void drawBG() {
        getChildren().removeIf(n -> "grid".equals(n.getUserData()));

        javafx.scene.canvas.Canvas canvas = new javafx.scene.canvas.Canvas(
                getWidth() <= 0 ? 700 : getWidth(), getHeight() <= 0 ? 500 : getHeight());
        canvas.setUserData("grid");
        canvas.setMouseTransparent(true);

        javafx.scene.canvas.GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setStroke(javafx.scene.paint.Color.web("#313244"));
        gc.setLineWidth(0.5);

        double w = canvas.getWidth();
        double h = canvas.getHeight();
        double step = 50;
        for (double x = 0; x < w; x += step) {
            gc.strokeLine(x, 0, x, h);
        }
        for (double y = 0; y < h; y += step) {
            gc.strokeLine(0, y, w, y);
        }

        gc.setStroke(javafx.scene.paint.Color.web("#45475a"));
        gc.setLineWidth(1);
        gc.strokeLine(w / 2, 0, w / 2, h);
        gc.strokeLine(0, h / 2, w, h / 2);
        getChildren().add(0, canvas);
    }
}
