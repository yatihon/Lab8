package me.tihon;

import javafx.application.Platform;
import me.tihon.command.CommandType;
import me.tihon.dto.Request;
import me.tihon.dto.Response;
import me.tihon.model.HumanBeing;

import java.util.TreeSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class Refresher {

    private static final int INTERVAL = 15;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "refresher-thread"); t.setDaemon(true); return t;
    });
    private final NetworkManager networkManager;
    private final String username;
    private final String password;
    private final Consumer<TreeSet<HumanBeing>> onUpdate;

    public Refresher(NetworkManager networkManager, String username, String password,
                     Consumer<TreeSet<HumanBeing>> onUpdate) {
        this.networkManager = networkManager;
        this.username = username;
        this.password = password;
        this.onUpdate = onUpdate;
    }

    public void startR() {
        scheduler.scheduleAtFixedRate(this::poll, 0, INTERVAL, TimeUnit.SECONDS);
    }

    public void stopR() {
        scheduler.shutdownNow();
    }

    public void poll() {
        try {
            Response response = networkManager.send(new Request(CommandType.SHOW, null, username, password));
            TreeSet<HumanBeing> collection = response.getCollection();
            Platform.runLater(() -> onUpdate.accept(collection));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
