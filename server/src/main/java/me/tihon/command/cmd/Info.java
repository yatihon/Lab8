package me.tihon.command.cmd;

import me.tihon.command.Command;
import me.tihon.dto.Request;
import me.tihon.dto.Response;
import me.tihon.manager.CollectionManager;

public class Info implements Command {

    private final CollectionManager manager;
    public Info(CollectionManager manager) {
        this.manager = manager;
    }

    @Override
    public Response execute(Request request) {
        return new Response(
                "Тип: " + manager.getCollection().getClass().getSimpleName() +
                        "\nРазмер: " + manager.getCollection().size() +
                        "\nДата инициализации: " + manager.getInitDate()
        );
    }

    @Override
    public boolean requiresAuth() {
        return true;
    }
}
