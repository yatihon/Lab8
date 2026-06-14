package me.tihon.command.cmd;

import me.tihon.command.Command;
import me.tihon.dto.Request;
import me.tihon.dto.Response;
import me.tihon.manager.CollectionManager;

public class PrintDescending implements Command {

    private final CollectionManager manager;
    public PrintDescending(CollectionManager manager) {
        this.manager = manager;
    }

    @Override
    public Response execute(Request request) {
        String res = manager.getCollection().stream().sorted((a, b) -> b.compareTo(a))
                .map(Object::toString).reduce("", (a, b) -> a + b + "\n");
        return new Response(res.isEmpty() ? "Коллекция пустая" : res);
    }

    @Override
    public boolean requiresAuth() {
        return true;
    }
}
