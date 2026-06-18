package me.tihon.command.cmd;

import me.tihon.command.Command;
import me.tihon.dto.Request;
import me.tihon.dto.Response;
import me.tihon.manager.CollectionManager;
import java.time.format.DateTimeFormatter;

public class Info implements Command {

    private final CollectionManager manager;
    public Info(CollectionManager manager) {
        this.manager = manager;
    }

    @Override
    public Response execute(Request request) {
        var collection = manager.getCollection();

        String date = manager.getInitDate() != null ? manager.getInitDate().format(
                DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")) : "неизвестно";
        return new Response(
                "Тип коллекции: " + collection.getClass().getSimpleName() + "\n" +
                        "Размер: " + collection.size() + "\n" + "Дата инициализации: " + date);
    }
    @Override
    public boolean requiresAuth() {
        return true;
    }
}
